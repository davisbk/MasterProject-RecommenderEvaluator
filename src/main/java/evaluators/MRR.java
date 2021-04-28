
package evaluators;

import datastructures.Prediction;
import datastructures.Rating;
import datastructures.User;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Mean Reciprocal Rank (MRR) Evaluator. Normally MRR is calculated as:
 * 
 * 1/|Q| * sum(1/rank[i]) for i = 1 to |Q|.
 * 
 * However because multiple movies might have the same score and might be put into a (somewhat) random
 * order (though possibly all relevant), we adjust MRR to be the reciprocal position of the 
 * first relevant item in the top-k list. 
 * 
 * E.g. if k = 5, if the first relevant item appears at position 3, the MRR would be 1/3
* 
 * See also: https://en.wikipedia.org/wiki/Mean_reciprocal_rank
 * 
 * @author Juan David Mendez
 */
public class MRR implements EvaluatorInterface {    
        
    /**
     * Calculates the MRR for a single User. It is assumed that the Predictions passed
     * to this method are already sorted. 
     * 
     * @param usr The User whose MRR we wish to calculate
     * @param userPreds A (sorted) list of Predictions for this User
     * @param k The top-k value amount for the User's Predictions
     * @return The MRR for this User's top-k predictions
     */
    @Override
    public double evaluate(User usr, LinkedList<Prediction> userPreds, int k) {
        // If it has been flagged that all Predictions should be used, set k to the proper size. 
        // For the second condition, i.e. if this method was called and the requested k is greater 
        // than the size, return the MRR for the full list. This should not happen in 
        // normal practice since it is unlikely that MRR would be calculated for a single User,
        // independently of any others. Normal practice is that the HashMap method would call this
        // method for each User, and there it is checked whether the User has enough Predictions.
        if(k < 0 || k > userPreds.size()) {
            k = userPreds.size();
        }
        
        
        double total = 0.0; // The final MRR
        
        int count = 1; // For making sure we don't exceed k Predictions
        double userAvg = usr.getAvgRating();
        
        // Get the predicted and actual Ratings for this User
        LinkedList<Prediction> preds = userPreds;
        
        int rank = 1; // The position of the Prediction in the top-k list
        
        // For faster retrieval of the actual Ratings, we put these in a HashMap
        // with the Movie ID as the key.
        HashMap<Integer, Integer> ratingHash = new HashMap<>();
        for(Rating rating : usr.getTestRatings()) {
            ratingHash.put(rating.getMovie().getId(), rating.getRating());
        }
        
        boolean MRRExists = false; // For keeping track of whether we found a "hit" in the top-k list (to properly keep track of rank)
        
        // For each Prediction for the User....
        for(Prediction currentPred : preds) {
            if(count > k) {
                break;
            }
            
            // If the predicted Movie's actual score is above the User's average, we 
            // note its rank. Else we continue down the top-k list of Predictions
            if(ratingHash.containsKey(currentPred.getMovie().getId())) {
                if(ratingHash.get(currentPred.getMovie().getId()) >= userAvg) {
                    MRRExists = true;
                    break;
                }
            }
            rank++;         
            count++;
        }
        
        // If we found any predicted Movie from the top-k list in the rated movies, we
        // return the reciprocal rank of this prediction. Else, none of the predicted
        // movies were above the user's average, thus MRR = 0.
        if(MRRExists) {
            total = 1.0 / rank;
        }
        else {
            total = 0.0;
        }
        return total;
    }
    /**
     * This method calculates the MRR for a HashMap of users. It calculates the average
     * MRR for all Users in the HashMap. The individual MRRs are calculated and summed
     * and the result is divided by the number of Users.
     * 
     * @param userHash The HashMap containing the Users and Predictions
     * @param k The number of Predictions to use
     * @return The average MRR across all Users in the passed HashMap
     */
    @Override
    public double evaluate(HashMap<User, LinkedList<Prediction>> userHash, int k) {
        double avg = 0.0;
        int validUserCount = 0;
        
        for(Map.Entry<User, LinkedList<Prediction>> currentUser : userHash.entrySet()) {
            User usr = currentUser.getKey(); // Get the User object
            LinkedList<Prediction> preds = currentUser.getValue(); // Get the LinkedList of Predictions for this User
            
            if(preds.size() >= k) {
                avg += this.evaluate(usr, preds, k);
                validUserCount++;
            }
            else {
                // If the User does not have enough Predictions then we do not calculate MRR for them
            }
        }
        
        avg /= validUserCount;
        
        // Make sure that we didn't divide by zero
        if(Double.isNaN(avg)) {
            avg = 0.0;
        }
        return avg;
    }
   
}
