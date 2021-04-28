
package evaluators;

import datastructures.Prediction;
import datastructures.User;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Implementation of the Root Mean Square Error (RMSE) evaluation metric.
 * RMSE = sqrt( ( sum (y[t] - x[t])^2 ) / N ) where y[t] is the predicted and x[t] is the actual score.
 * See https://en.wikipedia.org/wiki/Root-mean-square_deviation 
 * 
 * @author Brian Davis
 */
public class RMSE implements EvaluatorInterface {
    /**
     * This method calculates the Root Mean Square Error (RMSE) for a single User using up to the k Predictions of the user.
     * @param usr The User for which we wish to calculate the RMSE
     * @param userPreds Predictions for this User
     * @param k The number of predictions to be used, in case of -1 it means all users and if k is greater than the amount of
     * predictions a user has then use all the predictions of the user.
     * @return The RMSE for the k values
     */
    @Override
    public double evaluate(User usr, LinkedList<Prediction> userPreds, int k) {
        if(k < 0 || k > userPreds.size()) {
            k = userPreds.size();
        }        
        double RMSE = 0.0;
        
        for(Prediction pred : userPreds.subList(0, k)) { //Limit the predictions by k
            // Make sure our Predictions are in the valid [1-5] range
            if(pred.getValue() < 1.0 || pred.getValue() > 5.0) { 
                    String errorString = "Please call the RMSE evaluator AFTER running a scoreToRating method on the ";
                    errorString += "Predictions. Value of " + pred.getValue() + " found for User ";
                    errorString += usr.getId() + ", not in valid 1-5 range.";
                    throw new RuntimeException(errorString);
            }
            RMSE += Math.pow(pred.getValue() - usr.getTestRatingByID(pred.getMovie().getId()), 2);
            
        }
        
        
        // Make sure we have some Predictions and that k != 0
        if(userPreds.isEmpty() || k == 0) {
            RMSE = 0.0;
        }
        else {
            RMSE /= Math.min(userPreds.size(), k);
        }
        
        RMSE = Math.sqrt(RMSE);
        
        return RMSE;
    }
    
    /**
     * Evaluates the recommendations using RMSE
     * @param userHash List of users and predictions to evaluate.
     * @param k 
     * @return 
     */
    @Override
    public double evaluate(HashMap<User, LinkedList<Prediction>> userHash, int k) {
        double RMSE = 0.0;
        
        if(userHash.isEmpty()) {
            return RMSE;
        }
        int validUserCount = 0;
        // Add up the individual Users' RMSE
        for(Map.Entry<User, LinkedList<Prediction>> mapEntry : userHash.entrySet()) {
            User usr = mapEntry.getKey();
            LinkedList<Prediction> preds = mapEntry.getValue();
            
            if(preds.size() >= k) {
                RMSE += this.evaluate(mapEntry.getKey(), mapEntry.getValue(),k);
                validUserCount++;
            }
            else{
                // If there are not enough Predictions for this User, do not count them towards the average
            }
        }
        RMSE /= validUserCount; // Divide by number of (valid) Users
        // Make sure we didn't divide by zero
        if(Double.isNaN(RMSE)) {
            RMSE = 0;
        }
        return RMSE;
    }
        
}
