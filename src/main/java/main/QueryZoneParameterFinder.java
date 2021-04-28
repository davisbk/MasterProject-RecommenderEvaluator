package main;

import datastructures.Prediction;
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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import recommenders.QueryZone;
import recommenders.RecommenderInterface;
import scoreToRating.SimpleScoreToRating;
import utils.DBManager;
import utils.DataPreparer;
import utils.Printer;
import datastructures.Result;
import datastructures.Result.EvalType;
import java.util.Date;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import utils.Settings;

/**
 * This class performs a grid search of the parameter space for the QueryZone
 * recommender for 100 users in order to find the best parameters.
 * 
 * @author Brian Davis
 */
public class QueryZoneParameterFinder {
    private final static String OUTPUT_HEADERS = "alpha\t\tbeta\t\tgamma\t\tsimilarity\t\tnumGenres\t\tNDCG\t\t\tMAE\t\t\tRMSE\t\tMAP\t\t\tPrec\t\tRecall\t\tF1\t\t\tMRR\t\t\ttimestamp";
    
    public static void main(String[] args) {
        Settings.loadNewSetting("test_settings.cfg");
        Printer printer = Printer.getFilePrinterInstance();
        
        printer.print(3,"QueryZoneParameterFinder.java");
        
        HashMap<Integer, ArrayList<QZRResult>> resultsHash = new HashMap<>(); // For storing multiple folds of Results
                
        printer.print(3, "Requested number of Users: " + Settings.getNumUsers());
        
        final double ROCCHIO_STEP_SIZE = 0.10; // The amount by which to increase the Rocchio parameters after each run
        final double SIMILARITY_STEP_SIZE = 0.05; // The amount by which to increase the QZR similarity threshold parameter after each run
        
        ArrayList<QZRResult> resultArray; // For storing the QZRResults of one run
        
        // Get K and numFolds from the Settings
        int k = Settings.getTopK();
        int numFolds = Settings.getNumFolds();
        
        // Initialize our database
        DBManager dbManager = DBManager.getInstance();
        dbManager.getDataWithEnoughRatings();
        DataPreparer datapreparer = new DataPreparer();
        
        List<HashMap<Integer, User>> userList = datapreparer.getUserLists();
        
        int loopCount = 0; // To keep track of how many times we actually are evaluating
        float previousPercent = 0.0f; // Keeps track of the percentage comlete
        int totalSteps = (int)((1.0 / ROCCHIO_STEP_SIZE) * (1.0 / ROCCHIO_STEP_SIZE) * (1.0 / ROCCHIO_STEP_SIZE) * (1.0 / SIMILARITY_STEP_SIZE)); // Total number of steps, for calculating percent complete
        
        while(userList.size() > 0) { // While there are still folds to perform
            HashMap<Integer, User> users = userList.remove(0);  // Get the users
            
            int foldNumber = (numFolds - userList.size());
            
            resultArray = new ArrayList<>();
            for(double alpha = 0.0; Math.abs(1.0 - alpha) > 0.0005; alpha += ROCCHIO_STEP_SIZE) {
            Settings.setAlpha(alpha);
                for(double beta = 0.0; Math.abs(1.0 - beta) > 0.0005; beta += ROCCHIO_STEP_SIZE) {
                    Settings.setBeta(beta);
                    for(double gamma = 0.0; Math.abs(1.0 - gamma) > 0.0005; gamma += ROCCHIO_STEP_SIZE) {
                        Settings.setGamma(gamma);
                        for(double similarity = 0.0; Math.abs(1.0 - similarity) > 0.0005; similarity += SIMILARITY_STEP_SIZE) {
                            Settings.setSimilarityThreshold(similarity);
                            
                            Result currentResult = new Result();
                            RecommenderInterface recommender = new QueryZone(users);

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
                            currentResult.set(EvalType.NDCG, NDCGSuccessRate);
                            currentResult.set(EvalType.MAE,MAESuccessRate);
                            currentResult.set(EvalType.RMSE,RMSESuccessRate);
                            currentResult.set(EvalType.MAP,MAPSuccessRate);
                            currentResult.set(EvalType.PREC, PrecisionSuccessRate);
                            currentResult.set(EvalType.RECALL, RecallSuccessRate);
                            currentResult.set(EvalType.F1, F1ScoreSuccessRate);
                            currentResult.set(EvalType.MRR, MRRSuccessRate);
                            currentResult.set(timestamp);

                            // Store the results
                            QueryZoneParameterFinder.QZRResult currentQZRResult = new QueryZoneParameterFinder.QZRResult(currentResult, alpha, beta, gamma, similarity);
                            resultArray.add(currentQZRResult);

                            loopCount++;

                            // Now output our total percentage completed for debugging
                            float percent = (float)Math.floor(loopCount *100f / totalSteps);

                            if(percent != previousPercent && percent % 10 == 0 ) {
                                Timestamp ts = new Timestamp(new Date().getTime());
                                System.out.println("[" + ts + "]: " + percent + "% complete.");
                            }
                            previousPercent = percent;
                           
                        } // end similarity
                    } // end gamma
                } // end beta
            } // end alpha
            
            
            // Add the fold's results to the Hash to be output later, if desired
            resultsHash.put(foldNumber, resultArray);
        } // end for all folds
        
        // Now we have our resultHash and can output the results to a file
        if(Settings.getWriteEvalToFile()) {
            System.out.println("Writing results to file!");
            
            try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(new File(printer.getDirFile(),printer.getFileName()),true)))) {
                out.write("Total Loop Count: " + loopCount + "\n");
                for(Map.Entry<Integer, ArrayList<QZRResult>> currentEntry : resultsHash.entrySet()) {
                    out.write("\nFold " + currentEntry.getKey() + "...\n");
                    out.write(OUTPUT_HEADERS + "\n");

                    for(QZRResult currentQZRResult :  currentEntry.getValue()) {
                        out.write(currentQZRResult.toString() + "\n");
                        out.flush();
                    }

                }
            }
            catch(IOException e) {
                System.out.println("Error writing to file." + e);
            }
           
        } // end if writeEvalToFile() 
        
        // Now output the number of ocurrences for each value from the evaluation
        ArrayList<QZRResult> qzrResultArray = resultsHash.get(1);
        ArrayList<Result> tmpResults = new ArrayList<>();
        for(QZRResult qzrRes : qzrResultArray) {
            tmpResults.add(qzrRes.result);
        }
        QZRResult.printCounts(tmpResults,loopCount);
                
    } // end main
    
    /**
     * This subclass of the Result class has data members for storing the parameter settings
     * for each run of the QueryZone recommender.
     */
    public static class QZRResult extends Result {
        final static DecimalFormat TWOFORMAT = new DecimalFormat("0.00"); // For formatting the output to have two decimal places
        
        // For storing the Rocchio parameter settings
        public double alpha;
        public double beta;
        public double gamma;
        
        public double similarity; // For storing the QZR similarity threshold setting
        public Result result; // For storing the evaluator results of a single run
        
        /**
         * Default constructor of a QZRResult object
         * 
         * @param res The evaluator results (NDCG, Recall, etc.)
         * @param al The setting for Rocchio alpha during this run of the QZR
         * @param bet The setting for Rocchio beta during this run of the QZR
         * @param gam The setting for Rocchio gamma during this run of the QZR
         * @param sim  The setting for the QZR similarity threshold for this run
         */
        public QZRResult(Result res, double al, double bet, double gam, double sim) {
            result = res;
            alpha = al;
            beta = bet;
            gamma = gam;
            similarity = sim;            
        }
        
        /**
         * Generates a String representation of a QZRResult object
         * @return  A String representation of a QZRResult object
         */
        @Override
        public String toString() {
            String ret = TWOFORMAT.format(alpha) + "\t\t" + TWOFORMAT.format(beta) + "\t\t" + TWOFORMAT.format(gamma) + "\t\t" + TWOFORMAT.format(similarity) + "\t\t\t" + result.toString();
            
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
        public double getSimilarity() {
            return this.similarity;
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
         * Overrides the Object.equals method for comparing objects to QZRResult objects
         * 
         * @param obj The object to compare to this QZRResult object
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
        
        QZRResult rhs = (QZRResult)obj;
        
        return new EqualsBuilder()
                .append(alpha, rhs.alpha)
                .append(beta, rhs.beta)
                .append(gamma, rhs.gamma)
                .append(similarity, rhs.similarity)
                .append(result, rhs.result)
                .isEquals();
        }
        
        /**
         * This method generates a hashCode for the QZRResult object
         * 
         * @return A hashCode for the QZRResult object
         */
        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 31)
                    .append(alpha)
                    .append(beta)
                    .append(gamma)
                    .append(similarity)
                    .append(result)
                    .toHashCode();
        }
    } // end QZRResult
    
} //  end QueryZoneParameterFinder
