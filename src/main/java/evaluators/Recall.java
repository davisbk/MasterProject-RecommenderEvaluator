
package evaluators;


import datastructures.Movie;
import datastructures.Prediction;
import datastructures.Rating;
import datastructures.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Calculates the recall of the Recommender. 
 * Recall = true positives / (true positives + false negatives )
 * 
 * @author Brian Davis
 */
public final class Recall implements EvaluatorInterface {
    
    /**
     * Calculates the Recall for one User. It is assumed that the User has enough Predictions in
     * their predictionList to be able to calculate recall@k. This is checked in the HashMap version of evaluate.
     * 
     * @param usr The User for whom we calculate Recall
     * @param userPreds The sorted LinkedList of Predictions for this User
     * @param k The level of desired accuracy (recall@k)
     * @return The Recall for this particular User
     */
    @Override
    public double evaluate(User usr, LinkedList<Prediction> userPreds, int k) {
        // If it has been flagged that all Predictions should be used, set k to the proper size. 
        // For the second condition, i.e. if this method was called and the requested k is greater 
        // than the size, return the Recall for the full list. This should not happen in 
        // normal practice since it is unlikely that Recall would be calculated for a single User,
        // independently of any others. Normal practice is that the HashMap method would call this
        // method for each User, and there it is checked whether the User has enough Predictions.
        if(k < 0 || k > userPreds.size()) {
            k = userPreds.size();
        }
       
                        
        double userAverage = usr.getAvgRating(); // The average Rating value of this User
        double truePositiveCount = 0; // Relevant Movies which were detected
        double falseNegativeCount = 0; // Relevant Movies which were not detected
              
        
        // Create a HashMap of the predicted Movies to be able to efficiently check whether a Movie is in the top-k list
        HashMap<Movie, Double> topKPredsHash = new HashMap<>();
        for(Prediction pred : userPreds.subList(0, k)) { // We only want the top-k list 
            topKPredsHash.put(pred.getMovie(), pred.getValue());
        }
        ArrayList<Rating> userTestRatings = usr.getTestRatings();
        userTestRatings.sort(null);
        
        // Now count up the hits!
        for(Rating rating : userTestRatings) { // For all Ratings in the test set
           
            if(rating.getRating() >= userAverage) { // If the rating score for this Movie is greater than the User's average (i.e. it's relevant)
                if(topKPredsHash.containsKey(rating.getMovie())) { // And the Movie is on the top-k list
                    truePositiveCount++;
                } 
                else{
                    falseNegativeCount++; // If the Movie doesn't even appear on the top-k list and it should, this is a false negative
                }
            }
        }
        
        double recall = truePositiveCount / (truePositiveCount + falseNegativeCount);
        
        // Check to make sure we didn't divide by zero.
        if(Double.isNaN(recall)) {
            recall = 0.0;
        }
        
        return recall;
    }
    
    /**
     * This method calculates the Recall for a HashMap of Users and Predictions, i.e. for several Users, and 
     * calculates the average Recall for all Users in the HashMap. 
     * 
     * @param userHash  The HashMap containing the Users for whom we wish to calculate Recall
     * @param k The desired "accuracy" of our Recall (recall@k)
     * @return The average Recall for all Users in the passed HashMap
     */
    @Override
    public double evaluate(HashMap<User, LinkedList<Prediction>> userHash, int k) {
       
        double total = 0; // The total Recall for all Users
        int userCount = 0; // To keep track of how many Users have enough Predictions (for finding average)
        for(Map.Entry<User, LinkedList<Prediction>> userEntry : userHash.entrySet())  { //for each user
            User usr = userEntry.getKey();
            LinkedList<Prediction> preds = userEntry.getValue();
            
            // Check to make sure the User has enough Predictions
            if(preds.size() >= k) {
                double userTotal = this.evaluate(usr, preds, k); // Calculate this User's Recall
                total += userTotal; // Add it to the total
                userCount++;
            }
            else{
                // Without enough Predictions, don't count the User towards the Recall
            }
        }
        
        double recall = total/userCount; // Divide by the number of Users in the HashMap
        
        // Make sure we didn't divide by zero
        if(Double.isNaN(recall)){
            recall = 0;
        }
        
        return recall; 
    }
 }
