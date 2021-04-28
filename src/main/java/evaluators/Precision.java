
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
 * Calculates the precision of the Predictions for a User or the average Precision of all Users
 * Precision =  true positives / (true positives + false positives)
 * 
 * true positive = Movie whose predicted score is greater than the User's average and was rated as such by the User
 * false positive = Movie whose predicted score is greater than the User's average but was not actually rated as such by the User
 * 
 * @author Brian Davis
 */
public class Precision implements EvaluatorInterface {
    
    /**
     * This method calculates the Precision for a single User. It is assumed that when this method is called
     * the user has enough Predictions in predictionList for this to be calculated; this was checked for
     * in the evaluate(<HashMap>, int k) method.
     * 
     * @param usr The User whose Precision should be calculated
     * @param userPreds The Predictions of this User which are used for the calculations
     * @param k The number of Movies for which to calculate the precision (i.e. precision@k)
     * @return precision The Precision for the top-k Movies for this User
     */
    @Override
    public double evaluate(User usr, LinkedList<Prediction> userPreds, int k) {
        // If it has been flagged that all Predictions should be used, set k to the proper size. 
        // For the second condition, i.e. if this method was called and the requested k is greater 
        // than the size, return the Precision for the full list. This should not happen in 
        // normal practice since it is unlikely that Precision would be calculated for a single User,
        // independently of any others. Normal practice is that the HashMap method would call this
        // method for each User, and there it is checked whether the User has enough Predictions.
        if(k < 0 || k > userPreds.size()) {
            k = userPreds.size();
        }
        
        double precision; // The final precision returned
        double truePositiveCount = 0; // Counts the number of hits (Movies appearing on the list whose actual rating is higher than the User's average)
        double falsePositiveCount = 0; // Counts the number of misses (Movies appearingon the list whose actual rating is less than the User's average)
        double userAvgRating = usr.getAvgRating();  // The average rating of the User
        
        // Create a HashMap of the predicted Movies to be able to efficiently check whether a Movie is in the top-k list
        HashMap<Movie, Integer> topKRatings = new HashMap<>();
        ArrayList<Rating> userRatings = usr.getTestRatings();
        userRatings.sort(null);
        
        for(Rating rating  : userRatings.subList(0, k)) { // We only want the top-k list 
            topKRatings.put(rating.getMovie(), rating.getRating());
        }
        
        for(Prediction pred : userPreds.subList(0, k)) {
            
            // If the actual Rating of the Movie was greater than the User's average and we correctly predicted this, this is a true positive
            if(topKRatings.containsKey(pred.getMovie())) {
                if(topKRatings.get(pred.getMovie()) >= userAvgRating) {
                    truePositiveCount++;
                }
                else { // If the Movie was one of the best-rated movies but it was not relevant, it shouldn't have appeared on the tok-k list
                    falsePositiveCount++;
                }
            }
            else { // We predicted it to be relevant but it is not. This is a false positive
                falsePositiveCount++;
            }
            
          
        }
       
        // Precision =  true positives / (true positives + false positives)
        precision = truePositiveCount / (truePositiveCount + falsePositiveCount); 
        
        // Check to make sure we're not dividing by zero or some such
        if(Double.isNaN(precision)) {
            precision = 0.0;
        }
        
        return precision;
    }
    /**
     * This method calculates the average Precision for all Users in a passed HashMap. If a User
     * does not have enough Predictions (i.e. #preds is less than k) then don't count the User towards the average. 
     * 
     * @param userHash A HashMap containing the Predictions for the User(s).
     * @param k The top k number of items to calculate Precision for each User
     * @return precision The average Precision for all Users from the HashMap userHash
     */
    @Override
    public double evaluate(HashMap<User, LinkedList<Prediction>> userHash, int k)    {
        double precision = 0.0; // The final Precision to be returned
        int userCount = 0; // The number of Users with enough Predictions (to find the average per User)
        
        for(Map.Entry<User, LinkedList<Prediction>> mapEntry : userHash.entrySet()){ // For each User
            
            User usr = mapEntry.getKey(); // Get the User object
            LinkedList<Prediction> preds = mapEntry.getValue(); // Get the LinkedList of Predictions for this User
            
            if(preds.size() >= k) { // If this User has at least k Predictions
                double userPrec = this.evaluate(usr, preds, k); // Precision for single User
                precision += userPrec;
                userCount++;
            }
            else {
                // Don't count them towards the Precision
            }
           
        } // end for Map.Entry<User, LinkedList<Prediction>>
        
       
        precision /= userCount; // Calculate average by dividing by number of Users
        
        if(Double.isNaN(precision)) { // If we didn't have any Users with enough Predictions
            precision = 0.0;
        }
        
        return precision;
        
    }
}
