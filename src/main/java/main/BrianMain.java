package main;

import datastructures.Prediction;
import datastructures.User;
import evaluators.F1Score;
import evaluators.MAE;
import evaluators.MAP;
import evaluators.MRR;
import evaluators.Precision;
import evaluators.RMSE;
import evaluators.Recall;
import evaluators.NDCG;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import recommenders.RecommenderInterface;
import recommenders.SimRating;
import scoreToRating.SimpleScoreToRating;
import utils.DBManager;
import utils.DataPreparer;
import utils.Printer;
import utils.Settings;

/**
 *
 * @author Brian Davis
 */
public class BrianMain {
    final static double NANO_TO_SEC = 1000000000;
    public static void main(String[] args) {
        
        long dbStartTime;// = System.nanoTime();
        long dbStopTime = System.nanoTime();
        long totalStartTime = System.nanoTime();
        long predStartTime = System.nanoTime();
        long predStopTime = System.nanoTime();
        long evalStartTime = System.nanoTime();
        long evalStopTime = System.nanoTime();
        long profileStartTime = System.nanoTime();
        long profileStopTime = System.nanoTime();
        long simToRatingStart = System.nanoTime();
        long simToRatingStop = System.nanoTime();
        long singleFoldStart = System.nanoTime();
        long singleFoldStop = System.nanoTime();
        
        //Settings.loadNewSetting("test_settings.cfg"); // Not needed when not debugging
        Printer printer = Printer.getFileAndConsoleInstance();
        printer.print(3,"BrianMain.java");
        
        // Get K from the Settings
        int k = Settings.getTopK();
        
        printer.print(3, Settings.SettingsAsString() + "\n");
        
        dbStartTime = System.nanoTime();
        // Initialize our database and generate the k-folds
        DBManager dbManager = DBManager.getInstance();
        dbManager.getData();
        DataPreparer datapreparer = new DataPreparer();
        dbStopTime = System.nanoTime();
        
        printer.print(3, "\n" + Settings.getNumUsers() + " users retrieved in " + ((dbStopTime - dbStartTime)/ NANO_TO_SEC) + " seconds.");
        // Retrieve the List of Users (i.e. k-folds) from the DataPreparer
        List<HashMap<Integer, User>> userList = datapreparer.getUserLists();
        
        
        // Begin! 
        if(Settings.getPrintEachFoldEvaluation()) { // If we should print out the results for each fold
            
            printer.print(3,"Beginning K-folds evaluation for " + userList.size() + " folds of " + Settings.getNumUsers() + " users...");
            
            while(userList.size() > 0) { // While there are still folds to perform
                printer.print(3, "Beginning fold " + userList.size() + "...");
                singleFoldStart = System.nanoTime();
                HashMap<Integer, User> users = userList.remove(0);  // Get the first fold
                
                profileStartTime = System.nanoTime(); // To time the process of profile creation when creating a new Recommender
                
                // Create a Recommender from these Users
                //RecommenderInterface recommender = new QueryZone(users);
                //RecommenderInterface recommender = new Rocchio(users);
                //RecommenderInterface recommender = new Pairwise(users);
                //RecommenderInterface recommender = new ShortLong(users);
                //RecommenderInterface recommender = new MultipleTopicTracker(users);
                RecommenderInterface recommender = new SimRating(users);
                
                profileStopTime = System.nanoTime();
                
                printer.print(3,"Profiles built in " + ((profileStopTime - profileStartTime)/ NANO_TO_SEC) + " seconds.");
                
                printer.print(3,"\nMaking predictions with " + recommender.getClass().getName());
                predStartTime = System.nanoTime();
                HashMap<User, LinkedList<Prediction>> predictionScores = new HashMap<>(recommender.predict(users)); 
                predStopTime = System.nanoTime();
                
                int numPredictions = countTotalPredictions(predictionScores);
                
                printer.print(3, numPredictions + " predictions completed in " + ((predStopTime - predStartTime)/NANO_TO_SEC) + " seconds.");
                
                
                printer.print(3, "\nConverting similarity scores to Ratings...");
                simToRatingStart = System.nanoTime();
                SimpleScoreToRating str = new SimpleScoreToRating();
                HashMap<User, LinkedList<Prediction>> predictionRatings = new HashMap<>(str.scoreToRating(recommender, predictionScores));
                simToRatingStop = System.nanoTime();
                printer.print(3, "Conversion completed in " + ((simToRatingStop - simToRatingStart)/NANO_TO_SEC) + " seconds.");
                
                
                evalStartTime = System.nanoTime();
                printer.print(3,"\n======= EVALUATION OF FOLD " + (userList.size()+1) + " =======\n");
                
                printer.print(3, "NDCG: ");
                NDCG dcg = new NDCG();
                double dcgSuccessRate = dcg.evaluate(predictionScores, k);
                printer.print(3, dcgSuccessRate + "\n");
                
                printer.print(3,"F1 Score: ");
                F1Score f1score = new F1Score();
                double F1ScoreSuccessRate = f1score.evaluate(predictionScores,k);
                printer.print(3,F1ScoreSuccessRate + "\n");
                                
                printer.print(3,"Mean Absolute Error: ");
                MAE mae = new MAE();
                double MAESuccessRate = mae.evaluate(predictionRatings, k); 
                printer.print(3,MAESuccessRate + "\n");
                
                printer.print(3,"Mean Average Precision: ");
                MAP map = new MAP();
                double MAPSuccessRate = map.evaluate(predictionScores, k);
                printer.print(3,MAPSuccessRate + "\n");
                
                printer.print(3, "Mean Reciprocal Rank");
                MRR mrr = new MRR();
                double MRRSuccessRate = mrr.evaluate(predictionScores, k);
                printer.print(3, MRRSuccessRate + "\n");
                
                printer.print(3,"Precision: ");
                Precision prec = new Precision();
                double PrecisionSuccessRate = prec.evaluate(predictionScores, k);
                printer.print(3,PrecisionSuccessRate + "\n");

                printer.print(3,"Root Mean Squared Error: ");
                RMSE rmse = new RMSE();
                double RMSESuccessRate = rmse.evaluate(predictionRatings, k);
                printer.print(3,RMSESuccessRate + "\n");

                printer.print(3,"Recall: ");
                Recall recall = new Recall();
                double RecallSuccessRate = recall.evaluate(predictionScores,k);
                printer.print(3,RecallSuccessRate + "\n");

                evalStopTime = System.nanoTime();
                printer.print(3, "Evaluation completed in " + ((evalStopTime - evalStartTime) / NANO_TO_SEC) + " seconds.");
                singleFoldStop = System.nanoTime();
                printer.print(3, "\n\nFold " + (userList.size()+1) + " completed in " + ((singleFoldStop - singleFoldStart)/NANO_TO_SEC) + " seconds.");
                
                if(userList.size() > 0) {
                    printer.print(3, "\n\n====BEGINNING NEXT FOLD...====\n\n");
                }
                

            }  // end while folds remain
        } // end printing for each fold
          
        long totalStopTime = System.nanoTime();
        
        printer.print(3, "\n\n====All folds completed!====");
        printer.print(3, "Total run time: " + ((totalStopTime - totalStartTime) / NANO_TO_SEC) + " seconds.");
        
    }

    private static int countTotalPredictions(HashMap<User, LinkedList<Prediction>> predictionScores) {
        int ret = 0;
        
        for(Map.Entry<User, LinkedList<Prediction>> mapEntry : predictionScores.entrySet()) {
            ret += mapEntry.getValue().size();
        }
        
        return ret;
    }
    
}
