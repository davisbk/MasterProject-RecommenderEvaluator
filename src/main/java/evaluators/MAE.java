
package evaluators;

import datastructures.Prediction;
import datastructures.User;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Mean Absolute Error 
 * Difference between each predicted value and the real rating for that movie, for each prediction of a User.
 * For additional information see: https://en.wikipedia.org/wiki/Mean_absolute_error
 * @author Brian Davis
 */
public class MAE implements EvaluatorInterface {
    
    /**
     * Calculates the MAE for one User
     * 
     * @param usr for whom to calculate the MAE
     * @param userPreds to use for calculating MAE
     * @param k amount of predictions to use for calculating
     * @return The Mean Absolute Error of the Predictions
     */
    @Override
    public double evaluate(User usr, LinkedList<Prediction> userPreds, int k) {
        // If it has been flagged that all Predictions should be used, set k to the proper size. 
        // For the second condition, i.e. if this method was called and the requested k is greater 
        // than the size, return the MAE for the full list. This should not happen in 
        // normal practice since it is unlikely that MAE would be calculated for a single User,
        // independently of any others. Normal practice is that the HashMap method would call this
        // method for each User, and there it is checked whether the User has enough Predictions.
        if(k < 0 || k > userPreds.size()) {
            k = userPreds.size();
        }
        
        
        double MAE = 0.0;
        
        
        int loopCounter = 0;
        for(Prediction pred : userPreds) {
            if(loopCounter >= k) {
                break;
            }
            MAE += Math.abs(pred.getValue() - usr.getTestRatingByID(pred.getMovie().getId()));
            loopCounter++;
        }
        
        MAE /= k;
        
        // Check to make sure we didn't divide by zero
        if(Double.isNaN(MAE)) {
            MAE = 0.0;
        }
        
        return MAE;
        
    }
        
    /**
     * This method calculates the average MAE of a set of Users. Takes the MAE for
     * each User, adds it up, and divides by the number of valid Users (with >= k Predictions).
     * 
     * @param userHash to calculate the MAE for
     * @param k amount of predictions to use for calculating
     * @return The average MAE across all Users in the HashMap
     */
    @Override
    public double evaluate(HashMap<User, LinkedList<Prediction>> userHash, int k) {
        double MAE = 0.0;
        int validUserCount = 0;
        //for each user
        for(Map.Entry<User, LinkedList<Prediction>> userEntry : userHash.entrySet())  {
            User usr = userEntry.getKey();
            LinkedList<Prediction> userPreds = userEntry.getValue();
            
            if(userPreds.size() >= k) {
                MAE += this.evaluate(usr, userPreds, k);
                validUserCount++;
            }
            else {
                // If they don't have enough Predictions, do not calculate the MAE for this User
            }
        }
        
        MAE /= validUserCount;
        
        // Make sure we didn't divide by zero (i.e. no valid Users)
        if(Double.isNaN(MAE)) {
            MAE = 0.0;
        }
        
        return MAE;
    }
        
}
