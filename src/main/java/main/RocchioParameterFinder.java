package main;

import datastructures.Prediction;
import datastructures.Result;
import datastructures.User;
import evaluators.NDCG;
import evaluators.F1Score;
import evaluators.MAE;
import evaluators.MAP;
import evaluators.MRR;
import evaluators.Precision;
import evaluators.RMSE;
import evaluators.Recall;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import recommenders.RecommenderInterface;
import recommenders.Rocchio;
import scoreToRating.SimpleScoreToRating;
import utils.DBManager;
import utils.DataPreparer;
import utils.Printer;
import utils.Settings;

/**
 * This class performs a grid search of the parameter space for the Rocchio
 * recommender for 100 users in order to find the best parameters.
 * 
 * @author Brian Davis
 */
public class RocchioParameterFinder {
    private final static String OUTPUT_HEADERS = "alpha\t\tbeta\t\tgamma\t\tNDCG\t\tMAE\t\tRMSE\t\tMAP\t\tPrec\t\tRecall\t\tF1\t\tMRR\t\t\ttimestamp";
    
    public static void main(String[] args) {
        Settings.loadNewSetting("test_settings.cfg");
        Printer printer = Printer.getFilePrinterInstance(); // Only print to file to speed things up
        
        printer.print(3,"RocchioParameterFinder.java");
        HashMap<Integer, ArrayList<RocchioResult>> resultsHash = new HashMap<>();
        
        printer.print(3, "Requested number of Users: " + Settings.getNumUsers());
        
        // Upper and lower bounds for the Rocchio parameters
        final double INITIAL_ALPHA = 0.0;
        final double INITIAL_BETA = 0.0;
        final double INITIAL_GAMMA = 0.0;
        final double FINAL_ALPHA = 1.0;
        final double FINAL_BETA = 1.0;
        final double FINAL_GAMMA = 1.0;
       
        // The amount by which to increase a parameter after a run
        final double ROCCHIO_STEP_SIZE = 0.10;
        
        ArrayList<RocchioResult> resultArray; // For storing the results of a run
        
        // Get K and numFolds from the Settings
        int k = Settings.getTopK();
        int numFolds = Settings.getNumFolds();
        
        // Initialize our database
        DBManager dbManager = DBManager.getInstance();
        dbManager.getDataWithEnoughRatings();
        DataPreparer datapreparer = new DataPreparer();
        
        // For outputting completion percentages
        int loopCount = 0; // To keep track of how many times we actually evaluate
        int totalSteps = (int)(((FINAL_ALPHA - INITIAL_ALPHA)/ ROCCHIO_STEP_SIZE) * ((FINAL_BETA - INITIAL_BETA) / ROCCHIO_STEP_SIZE) * ((FINAL_GAMMA - INITIAL_GAMMA) / ROCCHIO_STEP_SIZE)); // Total number of steps, for calculating percent complete
        float previousPercent = 0.0f;

        
        List<HashMap<Integer, User>> userList = datapreparer.getUserLists();
        while(userList.size() > 0) { // While there are still folds to perform
            HashMap<Integer, User> users = userList.remove(0);  // Get the users

            int foldNumber = (numFolds - userList.size());
            
            resultArray = new ArrayList<>();
            
            for(double alpha = INITIAL_ALPHA; Math.abs(FINAL_ALPHA - alpha) > 0.0005; alpha += ROCCHIO_STEP_SIZE) {
                Settings.setAlpha(alpha);
            
                for(double beta = INITIAL_BETA; Math.abs(FINAL_BETA - beta) > 0.0005; beta += ROCCHIO_STEP_SIZE) {
                    Settings.setBeta(beta);
                    for(double gamma = INITIAL_GAMMA; Math.abs(FINAL_GAMMA - gamma) > 0.0005; gamma += ROCCHIO_STEP_SIZE) {

                        Settings.setGamma(gamma);

                        Result currentResult = new Result();  // A Result to store the evaluation results
                        RecommenderInterface recommender = new Rocchio(users);

                        HashMap<User, LinkedList<Prediction>> predictionScores = new HashMap<>(recommender.predict(users)); 

                        // EVALUATION
                        NDCG ndcg = new NDCG();
                        double NDCGSuccessRate = ndcg.evaluate(predictionScores, k);

                        MAP map = new MAP();
                        double MAPSuccessRate = map.evaluate(predictionScores, k);

                        Precision prec = new Precision();
                        double PrecisionSuccessRate = prec.evaluate(predictionScores, k);

                        Recall recall = new Recall();
                        double RecallSuccessRate = recall.evaluate(predictionScores, k);

                        F1Score f1score = new F1Score();
                        double F1ScoreSuccessRate = f1score.evaluate(predictionScores, k);

                        SimpleScoreToRating str = new SimpleScoreToRating();
                        HashMap<User, LinkedList<Prediction>> predictionRatings = new HashMap<>(str.scoreToRating(recommender, predictionScores));
                                                
                        MAE mae = new MAE();
                        double MAESuccessRate = mae.evaluate(predictionRatings, k); 

                        RMSE rmse = new RMSE();
                        double RMSESuccessRate = rmse.evaluate(predictionRatings, k);

                        MRR mrr = new MRR();
                        double MRRSuccessRate = mrr.evaluate(predictionRatings, k);

                        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

                        // Set the currentResult values
                        currentResult.set(Result.EvalType.NDCG, NDCGSuccessRate);
                        currentResult.set(Result.EvalType.MAE,MAESuccessRate);
                        currentResult.set(Result.EvalType.RMSE,RMSESuccessRate);
                        currentResult.set(Result.EvalType.MAP,MAPSuccessRate);
                        currentResult.set(Result.EvalType.PREC, PrecisionSuccessRate);
                        currentResult.set(Result.EvalType.RECALL, RecallSuccessRate);
                        currentResult.set(Result.EvalType.F1, F1ScoreSuccessRate);
                        currentResult.set(Result.EvalType.MRR, MRRSuccessRate);
                        currentResult.set(timestamp);

                        RocchioParameterFinder.RocchioResult currentRocchioResult = new RocchioParameterFinder.RocchioResult(currentResult, alpha, beta, gamma);

                        resultArray.add(currentRocchioResult);
                        
                        loopCount++;
                        
                        // Now output our total percentage completed for debugging
                        float percent = (float)Math.floor(loopCount *100f / totalSteps);
                        if(percent != previousPercent && percent % 10 == 0 ) {
                            Timestamp ts = new Timestamp(new Date().getTime());
                            System.out.println("[" + ts + "]: " + percent + "% complete.");
                        }
                        previousPercent = percent;

                    } // end for gamma
                } // end for beta
            } // end for alpha
            
            // Add the fold's results to the Hash to be output later, if desired
            resultsHash.put(foldNumber, resultArray);
            
        } // end while there are folds remaining
        
        // Now we have our resultHash and can output the results to a file
        if(Settings.getWriteEvalToFile()) {
            System.out.println("Writing results to file!");
            
            try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(new File(printer.getDirFile(),printer.getFileName()),true)))) {
                out.write("Total Loop Count: " + loopCount + "\n");
                for(Map.Entry<Integer, ArrayList<RocchioParameterFinder.RocchioResult>> currentEntry : resultsHash.entrySet()) {
                    out.write("\nFold " + currentEntry.getKey() + "...\n");
                    out.write(OUTPUT_HEADERS + "\n");

                    for(RocchioParameterFinder.RocchioResult currentRocRes :  currentEntry.getValue()) {
                        out.write(currentRocRes.toString() + "\n");
                        out.flush();
                    }

                }
            }
            catch(IOException e) {
                System.out.println("Error writing to file." + e);
            }
           
        } // end if writeEvalToFile() 
        
        // Now output the number of ocurrences for each value from the evaluation
        ArrayList<RocchioResult> rocResultArray = resultsHash.get(1);
        ArrayList<Result> tmpResults = new ArrayList<>();
        for(RocchioResult rocRes : rocResultArray) {
            tmpResults.add(rocRes.result);
        }
        RocchioResult.printCounts(tmpResults,loopCount);
        
    } // end main
    
    /**
     * A Result subclass which stores the parameter settings for each run
     */
    public static class RocchioResult extends Result {
        private double alpha, beta, gamma;
        private Result result;
        final static DecimalFormat TWOFORMAT = new DecimalFormat("0.00"); // For formatting the output to have just two decimal places
        
        /**
         * Default constructor for storing parameter settings for a run
         * 
         * @param res The evaluator results for this run
         * @param alph The Rocchio alpha setting for this run
         * @param bet The Rocchio beta setting for this run
         * @param gam  The Rocchio gamma setting for this run
         */
        public RocchioResult(Result res, double alph, double bet, double gam) {
            this.result = res;
            this.alpha = alph;
            this.beta = bet;
            this.gamma = gam;
        }
        
        /**
         * Overrides the Object.equals method to compare objects to RocchioResult objects
         * 
         * @param obj The object to compare to this RocchioResult object
         * @return Whether the two objects are equal
         */
        @Override
        public boolean equals(Object obj)  {
            if(obj == null)
            return false;
        if(getClass() != obj.getClass())
            return false;
        if(this == obj)
            return true;
        
        RocchioResult rhs = (RocchioResult)obj;
        
        return new EqualsBuilder()
                .append(alpha, rhs.alpha)
                .append(beta, rhs.beta)
                .append(gamma, rhs.gamma)
                .append(result, rhs.result)
                .isEquals();
        }
        
        /**
         * Generates a hashCode for this RocchioResult object
         * 
         * @return A hashCode for this RocchioResult object
         */
        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 31)
                    .append(alpha)
                    .append(beta)
                    .append(gamma)
                    .append(result)
                    .toHashCode();
        }
        
        /**
         * Generates a String representation of the RocchioResult object
         * 
         * @return A String representation of the RocchioResult object
         */
        @Override
        public String toString() {
            String ret = TWOFORMAT.format(alpha) + "\t\t" + TWOFORMAT.format(beta) + "\t\t" + TWOFORMAT.format(gamma) + "\t\t" + result.toString();
            
            return ret;
        }
        
        // GETTERS
        @Override
        public double getAlpha() {
            return this.alpha;
        }
        
        @Override
        public double getBeta() {
            return this.beta;
        }
        
        @Override
        public double getGamma() {
            return this.gamma;
        }
        
        @Override
        public double getNDCG() {
        return this.result.getNDCG();
        }
    
        @Override
        public double getF1() {
            return this.result.getF1();
        }

        @Override
        public double getMAE() {
            return this.result.getMAE();
        }
        
        @Override
        public double getMAP() {
            return this.result.getMAP();
        }
        
        @Override
        public double getMRR() {
            return this.result.getMRR();
        }
        
        @Override
        public double getPrec() {
            return this.result.getPrec();
        }
        
        @Override
        public double getRecall() {
            return this.result.getRecall();
        }
        
        @Override
        public double getRMSE() {
            return this.result.getRMSE();
        }
                
    } // end RocchioResult
    
} // end RocchioParameterFinder
