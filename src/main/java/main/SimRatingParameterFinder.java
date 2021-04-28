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
import recommenders.SimRating;
import scoreToRating.SimpleScoreToRating;
import utils.DBManager;
import utils.DataPreparer;
import utils.Printer;
import utils.Settings;

/**
 * This class performs a grid search of the parameter space for the SimRating
 * recommender for 100 users in order to find the best parameters.
 * @author Brian Davis
 */
public class SimRatingParameterFinder {
    private final static String OUTPUT_HEADERS = "alpha\t\tbeta\t\tgamma\t\t\tNDCG\t\tMAE\t\tRMSE\t\tMAP\t\tPrec\t\tRecall\t\tF1\t\tMRR\t\t\ttimestamp";
    private final static double STEP_SIZE = 0.10;
    public static void main(String[] args) {
        
        Settings.loadNewSetting("test_settings.cfg");
        Printer printer = Printer.getFilePrinterInstance(); // File only to speed up output
        printer.print(3,"SimRatingParameterFinder.java");
        
        HashMap<Integer, ArrayList<SimRatingParameterFinder.SimRatingResult>> resultsHash = new HashMap<>();
        ArrayList<SimRatingParameterFinder.SimRatingResult> resultArray;
        
        printer.print(3, "numUsers: " + Settings.getNumUsers());
        DecimalFormat twoForm = new DecimalFormat("0.00");
        DecimalFormat threeForm = new DecimalFormat("0.000");
        
        // Get K and numFolds from the Settings
        int k = Settings.getTopK();
        int numFolds = Settings.getNumFolds();
        
        // Initialize our database
        DBManager dbManager = DBManager.getInstance(); 
        dbManager.getDataWithEnoughRatings();
        DataPreparer datapreparer = new DataPreparer();
        
        List<HashMap<Integer, User>> userList = datapreparer.getUserLists();
        
        // For outputting completion percentages
        int loopCount = 0; // To keep track of how many times we actually are evaluating
        int totalSteps = (int)((1.0 / STEP_SIZE)*(1.0 / STEP_SIZE) * (1.0 / STEP_SIZE));
        float previousPercent = 0.0f;
        
        while(userList.size() > 0) { // While there are still folds to perform
            HashMap<Integer, User> users = userList.remove(0);  // Get the users
            int foldNumber = (numFolds - userList.size());
            
            resultArray = new ArrayList<>();
            
            for(double alpha = 0.0; Math.abs(1.0 - alpha) > 0.0005; alpha += STEP_SIZE) {
                Settings.setAlpha(alpha);
                
                for(double beta = 0.0; Math.abs(1.0 - beta) > 0.0005; beta += STEP_SIZE) {
                    Settings.setBeta(beta);
                    
                    for(double gamma = 0.0; Math.abs(1.0 - gamma) > 0.0005; gamma += STEP_SIZE) {
                        Settings.setGamma(gamma);
                        Result currentResult = new Result();

                        SimRating simRating = new SimRating(users);

                        HashMap<User, LinkedList<Prediction>> predictionScores = new HashMap<>(simRating.predict(users)); // For storing Results from multiple folds

                        // EVALUATION
                        NDCG ndcg = new NDCG();
                        double NDCGSuccessRate = ndcg.evaluate(predictionScores,k);//,k);

                        MAP map = new MAP();
                        double MAPSuccessRate = map.evaluate(predictionScores, k);

                        Precision prec = new Precision();
                        double PrecisionSuccessRate = prec.evaluate(predictionScores, k);

                        Recall recall = new Recall();
                        double RecallSuccessRate = recall.evaluate(predictionScores,k);

                        F1Score f1score = new F1Score();
                        double F1ScoreSuccessRate = f1score.evaluate(predictionScores,k);

                        // Now for the remaining evaluators which require ratings:
                        SimpleScoreToRating str = new SimpleScoreToRating();
                        HashMap<User, LinkedList<Prediction>> predictionRatings = new HashMap<>(str.scoreToRating(simRating, predictionScores));

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

                        SimRatingParameterFinder.SimRatingResult currentMTTResult = new SimRatingResult(currentResult,alpha,beta, gamma);
                        resultArray.add(currentMTTResult);


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
            
        } // end for all folds
        
        // Now we have our resultHash and can output the results to a file
        if(Settings.getWriteEvalToFile()) {
            System.out.println("Writing results to file!");
            
            try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(new File(printer.getDirFile(),printer.getFileName()),true)))) {
                out.write("Total Loop Count: " + loopCount + "\n");
                for(Map.Entry<Integer, ArrayList<SimRatingParameterFinder.SimRatingResult>> currentEntry : resultsHash.entrySet()) {
                    out.write("\nFold " + currentEntry.getKey() + "...\n");
                    out.write(OUTPUT_HEADERS + "\n");

                    for(SimRatingParameterFinder.SimRatingResult currentPWResult :  currentEntry.getValue()) {
                        out.write(currentPWResult.toString() + "\n");
                        out.flush();
                    }

                }
                
            }catch(IOException e) {
                System.out.println("Error writing to file." + e);
            }
            
            
        } // end if write to file
        
        // Now output the number of ocurrences for each value from the evaluation
        ArrayList<SimRatingResult> simResultArray = resultsHash.get(1);
        ArrayList<Result> tmpResults = new ArrayList<>();
        for(SimRatingResult mttRes : simResultArray) {
            tmpResults.add(mttRes.result);
        }
        SimRatingResult.printCounts(tmpResults,loopCount);
        
    }
    
    /**
     * This class stores the parameter settings and evaluator results for a run of the SimRating recommender
     */
    public static class SimRatingResult extends Result {
        final static DecimalFormat TWOFORMAT = new DecimalFormat("0.00");
        
        // For storing the Rocchio parameters
        public double alpha;
        public double beta;
        public double gamma;
        
        // For storing the evaluator results
        public Result result;
        
        /**
         * Standard constructor for a SimRatingResult object
         * @param res The evaluator results
         * @param alph The setting for Rocchio alpha for a run
         * @param bet The setting for Rocchio beta for a run
         * @param gam  The setting for Rocchio gamma for a run
         */
        public SimRatingResult(Result res, double alph, double bet, double gam) {
            this.result = res;
            this.alpha = alph;
            this.beta = bet;
            this.gamma = gam;
        }
        
        /**
         * Generates a String representation of a SimRatingResult object
         * @return  A String representation of a SimRatingResult object
         */
        @Override
        public String toString() {
            String ret = TWOFORMAT.format(alpha) + "\t\t" + TWOFORMAT.format(beta) + "\t\t" + TWOFORMAT.format(gamma) + "\t\t\t" + result.toString();
            
            return ret;
        }
        
        // GETTERS
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
        
        
        @Override
        public double getAlpha() {
            return alpha;
        }
        
        @Override
        public double getBeta() {
            return beta;
        }
        
        @Override
        public double getGamma() {
            return gamma;
        }
        
        /**
         * Overrides the Object.equals method to compare objects to SimRatingResult object
         * 
         * @param obj The object to compare with this SimRatingResult object
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

            SimRatingParameterFinder.SimRatingResult rhs = (SimRatingParameterFinder.SimRatingResult)obj;
            
            return new EqualsBuilder()
                    .append(alpha, rhs.alpha)
                    .append(beta, rhs.beta)
                    .append(gamma, rhs.gamma)
                    .append(result, rhs.result)
                    .isEquals();
            
        }
        
        /**
         * Generates a hashCode for this SimRatingResult 
         * @return A hashCode for this SimRatingResult
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
        
    } // end SimRatingResult
} // end SimRatingParameterFinder
