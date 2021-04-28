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
import recommenders.MultipleTopicTracker;
import scoreToRating.SimpleScoreToRating;
import utils.DBManager;
import utils.DataPreparer;
import utils.Printer;
import utils.Settings;

/**
 * This class performs a grid search of the parameter space for the MTT recommender for 100 users in order to
 * find the best parameters.
 * 
 * @author Brian Davis
 */
public class MTTParameterFinder {
    private final static String OUTPUT_HEADERS = "newCat\t\tdelCat\t\t\tNDCG\t\tMAE\t\tRMSE\t\tMAP\t\tPrec\t\tRecall\t\tF1\t\tMRR\t\t\ttimestamp";
    private final static double STEP_SIZE = 0.10;
    public static void main(String[] args) {
        
        Settings.loadNewSetting("test_settings.cfg");
        Printer printer = Printer.getFilePrinterInstance(); // File only to speed up output
        printer.print(3,"MTTParameterFinder.java");
        
        HashMap<Integer, ArrayList<MTTParameterFinder.MTTResult>> resultsHash = new HashMap<>();
        ArrayList<MTTParameterFinder.MTTResult> resultArray;
        
        
        printer.print(3, "numUsers: " + Settings.getNumUsers());
        
        // Get K from the Settings
        int k = Settings.getTopK();
        int numFolds = Settings.getNumFolds();
        
        // Initialize our database
        DBManager dbManager = DBManager.getInstance(); 
        dbManager.getDataWithEnoughRatings();
        DataPreparer datapreparer = new DataPreparer();
        
        List<HashMap<Integer, User>> userList = datapreparer.getUserLists();
        
        int loopCount = 0; // To keep track of how many times we actually are evaluating
        float previousPercent = 0.0f; // Keeps track of the percentage comlete
        int totalSteps = (int)( (1.0 / STEP_SIZE) * (1.0 / STEP_SIZE)); // Total number of steps, for calculating percent complete
        
        while(userList.size() > 0) { // While there are still folds to perform
            HashMap<Integer, User> users = userList.remove(0);  // Get the users
            int foldNumber = (numFolds - userList.size());
            
            resultArray = new ArrayList<>();
            
            for(double newCat = 0.0; Math.abs(1.0 - newCat) > 0.0005; newCat += STEP_SIZE) {
                Settings.setMttNewCatThreshold(newCat);
                
                for(double delCat = 0.0; Math.abs(1.0 - delCat) > 0.0005; delCat += STEP_SIZE) {
                    Settings.setMttDelCatThreshold(delCat);
                    
                    Result currentResult = new Result();
                     
                    MultipleTopicTracker mtt = new MultipleTopicTracker(users);
                    
                    HashMap<User, LinkedList<Prediction>> predictionScores = new HashMap<>(mtt.predict(users)); 
                
                    // EVALUATION
                    NDCG dcg = new NDCG();
                    double DCGSuccessRate = dcg.evaluate(predictionScores,k);//,k);

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
                    HashMap<User, LinkedList<Prediction>> predictionRatings = new HashMap<>(str.scoreToRating(mtt, predictionScores));

                    MAE mae = new MAE();
                    double MAESuccessRate = mae.evaluate(predictionRatings, k); 

                    RMSE rmse = new RMSE();
                    double RMSESuccessRate = rmse.evaluate(predictionRatings, k);

                    MRR mrr = new MRR();
                    double MRRSuccessRate = mrr.evaluate(predictionRatings, k);

                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());

                    // Set the currentResult values
                    currentResult.set(Result.EvalType.NDCG, DCGSuccessRate);
                    currentResult.set(Result.EvalType.MAE,MAESuccessRate);
                    currentResult.set(Result.EvalType.RMSE,RMSESuccessRate);
                    currentResult.set(Result.EvalType.MAP,MAPSuccessRate);
                    currentResult.set(Result.EvalType.PREC, PrecisionSuccessRate);
                    currentResult.set(Result.EvalType.RECALL, RecallSuccessRate);
                    currentResult.set(Result.EvalType.F1, F1ScoreSuccessRate);
                    currentResult.set(Result.EvalType.MRR, MRRSuccessRate);
                    currentResult.set(timestamp);
                    
                    MTTParameterFinder.MTTResult currentMTTResult = new MTTResult(currentResult,newCat,delCat);
                    resultArray.add(currentMTTResult);
                    
                    
                    loopCount++;
                    
                    // Now output our total percentage completed for debugging
                    float percent = (float)Math.floor(loopCount *100f / totalSteps);
                    if(percent != previousPercent && percent % 10 == 0 ) {
                        Timestamp ts = new Timestamp(new Date().getTime());
                        System.out.println("[" + ts + "]: " + percent + "% complete.");
                    }
                    previousPercent = percent;
                
                
                } // end for del cat
                
            } // end for new cat
            
            // Add the fold's results to the Hash to be output later, if desired
            resultsHash.put(foldNumber, resultArray);
            
        } // end for all folds
        
        // Now we have our resultHash and can output the results to a file
        if(Settings.getWriteEvalToFile()) {
            System.out.println("Writing results to file!");
            
            try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(new File(printer.getDirFile(),printer.getFileName()),true)))) {
                out.write("Total Loop Count: " + loopCount + "\n");
                for(Map.Entry<Integer, ArrayList<MTTParameterFinder.MTTResult>> currentEntry : resultsHash.entrySet()) {
                    out.write("\nFold " + currentEntry.getKey() + "...\n");
                    out.write(OUTPUT_HEADERS + "\n");

                    for(MTTParameterFinder.MTTResult currentPWResult :  currentEntry.getValue()) {
                        out.write(currentPWResult.toString() + "\n");
                        out.flush();
                    }

                }
                
                
            }catch(IOException e) {
                System.out.println("Error writing to file." + e);
            }
            
            
        } // end if write to file
        
        // Now output the number of ocurrences for each value from the evaluation
        ArrayList<MTTResult> mttResultArray = resultsHash.get(1);
        ArrayList<Result> tmpResults = new ArrayList<>();
        for(MTTResult mttRes : mttResultArray) {
            tmpResults.add(mttRes.result);
        }
        MTTResult.printCounts(tmpResults,loopCount);
        
    }
    
    /**
     * This class is an extension of the Result class which has extra data members
     * for the MTT class.
     */
    public static class MTTResult extends Result {
        final static DecimalFormat TWOFORMAT = new DecimalFormat("0.00"); // For formatting the output to have two decimal places
        public double newCatParam; // The value of the newCat parameter for this run
        public double delCatParam; // The value of the delCat parameter for this run
        public Result result; // To store the general Result (evaluator) data for this run
        
        /**
         * Standard constructor for an MTTResult object
         * 
         * @param res The general Result (with all evaluators)
         * @param newCat The newCat parameter of the MTT evaluator
         * @param delCat The delCat parameter of the MTT evaluator
         */
        public MTTResult(Result res, double newCat, double delCat) {
            this.result = res;
            this.newCatParam = newCat;
            this.delCatParam = delCat;
        }
        
        /**
         * Generates a String representation of this MTTResult.
         * @return A String representation of this MTTResult
         */
        @Override
        public String toString() {
            String ret = TWOFORMAT.format(newCatParam) + "\t\t" + TWOFORMAT.format(delCatParam)  + "\t\t\t" + result.toString();
            
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
        public double getNewCat() {
            return newCatParam;
        }
        
        @Override
        public double getDelCat() {
            return delCatParam;
        }
        
        /**
         * Overrides the Object.equals method for comparing objects with MTTResult objects
         * @param obj The object to compare to this MTTResult object
         * @return Returns whether the two objects are equal
         */
        @Override
        public boolean equals(Object obj)  {
            if(obj == null)
                return false;
            if(getClass() != obj.getClass())
                return false;
            if(this == obj)
                return true; 

            MTTParameterFinder.MTTResult rhs = (MTTParameterFinder.MTTResult)obj;
            
            return new EqualsBuilder()
                    .append(newCatParam, rhs.newCatParam)
                    .append(delCatParam, rhs.delCatParam)
                    .append(result, rhs.result)
                    .isEquals();
            
        }
        
        /**
         * Generate a hashCode for MTTResult objects
         * @return A hashCode for this MTTResult
         */
        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 31)
                    .append(newCatParam)
                    .append(delCatParam)
                    .append(result)
                    .toHashCode();
        }
        
    }
}
