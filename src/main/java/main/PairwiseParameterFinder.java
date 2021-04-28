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
import recommenders.Pairwise;
import scoreToRating.SimpleScoreToRating;
import utils.DBManager;
import utils.DataPreparer;
import utils.Printer;
import utils.Settings;

/**
 * This class performs a grid search of the parameter space for the Pairwise 
 * recommender for 100 users in order to find the best parameters.
 * 
 * @author Brian Davis
 */
public class PairwiseParameterFinder {
    private final static String OUTPUT_HEADERS = "SAMP_PERC\t\tNDCG\t\tMAE\t\tRMSE\t\tMAP\t\tPrec\t\tRecall\t\tF1\t\tMRR\t\t\ttimestamp";
    private final static double SAMP_PERC_STEP_SIZE = 0.050;
    
    public static void main(String[] args) {
        Settings.loadNewSetting("test_settings.cfg");
        Printer printer = Printer.getFilePrinterInstance(); // File only to speed up output
        printer.print(3,"PairwiseParameterFinder.java");
        
        HashMap<Integer, ArrayList<PairwiseResult>> resultsHash = new HashMap<>(); // For storing multiple folds
        ArrayList<PairwiseResult> resultArray; // For storing the final PairwiseResults
        
        
        printer.print(3, "numUsers: " + Settings.getNumUsers());
                
        // Get K and numFolds from the Settings
        int k = Settings.getTopK();
        int numFolds = Settings.getNumFolds();
        
        // Initialize our database
        DBManager dbManager = DBManager.getInstance(); 
        dbManager.getDataWithEnoughRatings();
        DataPreparer datapreparer = new DataPreparer();
        
        List<HashMap<Integer, User>> userList = datapreparer.getUserLists();
        
        int loopCount = 0; // To keep track of how many times we actually are evaluating
        int totalSteps = (int)( 1.0 / SAMP_PERC_STEP_SIZE); // For outputting completion percentages
        float previousPercent = 0.0f; // For outputting completion percentages
        
        while(userList.size() > 0) { // While there are still folds to perform
            HashMap<Integer, User> users = userList.remove(0);  // Get the users
            int foldNumber = (numFolds - userList.size());
            
            resultArray = new ArrayList<>();
                        
            for(double perc = 0.0; Math.abs(1.0 - perc) > 0.0005; perc += SAMP_PERC_STEP_SIZE) {
                Settings.setPairwiseSampleSize(perc); // Set the parameter
                Result currentResult = new Result();
                
                Pairwise recommender = new Pairwise(users); // Generate new user profiles
                                
                // Generate new Predictions
                HashMap<User, LinkedList<Prediction>> predictionScores = new HashMap<>(recommender.predict(users)); 
                
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

                PairwiseParameterFinder.PairwiseResult currentPWResult = new PairwiseResult(currentResult,perc);
                resultArray.add(currentPWResult);
                                
                loopCount++;
                
                // Now output our total percentage completed for debugging
                float percent = (float)Math.floor(loopCount *100f / totalSteps);
                if(percent != previousPercent && percent % 10 == 0 ) {
                    Timestamp ts = new Timestamp(new Date().getTime());
                    System.out.println("[" + ts + "]: " + percent + "% complete.");
                }
                previousPercent = percent;
            } // end for perc
        
            // Add the fold's results to the Hash to be output later, if desired
            resultsHash.put(foldNumber, resultArray);
        } // end for all folds
        
        // Now we have our resultHash and can output the results to a file
        if(Settings.getWriteEvalToFile()) {
            System.out.println("Writing results to file!");
            
            try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(new File(printer.getDirFile(),printer.getFileName()),true)))) {
                out.write("Total Loop Count: " + loopCount + "\n");
                for(Map.Entry<Integer, ArrayList<PairwiseResult>> currentEntry : resultsHash.entrySet()) {
                    out.write("\nFold " + currentEntry.getKey() + "...\n");
                    out.write(OUTPUT_HEADERS + "\n");

                    for(PairwiseResult currentPWResult :  currentEntry.getValue()) {
                        out.write(currentPWResult.toString() + "\n");
                        out.flush();
                    }

                }
                
                
            }catch(IOException e) {
                System.out.println("Error writing to file." + e);
            }
        } // end if write to file
        
        // Now output the number of ocurrences for each value from the evaluation
        ArrayList<PairwiseResult> pwResultArray = resultsHash.get(1);
        ArrayList<Result> tmpResults = new ArrayList<>();
        for(PairwiseResult pwRes : pwResultArray) {
            tmpResults.add(pwRes.result);
        }
        PairwiseResult.printCounts(tmpResults,loopCount);
        
    } // end main
    
    /**
     * This Result subclass stores the additional parameters of the Pairwise recommender
     */
    public static class PairwiseResult extends Result {
        final static DecimalFormat TWOFORMAT = new DecimalFormat("0.00"); // For formatting the output to have two decimal places
        public double samp_perc = 0.0; // The percentage of a user's movies to be sampled
        public Result result;
        
        /**
         * Standard constructor
         * @param res The usual Result object with the evaluator results
         * @param sample_percentage The value of the sampPerc parameter for this run
         */
        public PairwiseResult(Result res, double sample_percentage) {
            this.samp_perc = sample_percentage;
            this.result = res;
        }
        
        /**
         * Generates a String representation of a PairwiseResult object.
         * @return  A String representation of a PairwiseResult object
         */
        @Override
        public String toString() {
            String ret = TWOFORMAT.format(samp_perc) + "\t\t\t" + result.toString();
            
            return ret;
        }
        
        // GETTERS
        @Override
        public double getSampPerc() {
            return this.samp_perc;
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
        
        /**
         * Overrides the Object.equals method for comparing objects to PairwiseResult objects
         * @param obj The object to compare to this PairwiseResult object
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

            PairwiseResult rhs = (PairwiseResult)obj;
            
            return new EqualsBuilder()
                    .append(samp_perc, rhs.samp_perc)
                    .append(result, rhs.result)
                    .isEquals();
            
        }
        
        /**
         * Generates a hashCode for this PairwiseResult object
         * @return  A hashCode for this PairwiseResult object
         */
        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 31)
                    .append(samp_perc)
                    .append(result)
                    .toHashCode();
        }
        
    }
    
}
