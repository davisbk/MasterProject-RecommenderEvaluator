package evaluators;

import datastructures.Prediction;
import datastructures.User;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * This class calculates the F1 Score for the Predictions of a User. 
 * F1 Score = 2 * ( precision * recall) / ( precision + recall) )
 * 
 * @author Brian Davis
 */
public class F1Score implements EvaluatorInterface{
    /**
     * This method calculates the F1 Score for a single User and their Predictions. It is assumed that 
     * there are enough Predictions for a User (this is checked in the HashMap version of evaluate)
     * 
     * @param usr The User for whom we wish to calculate the F1 Score
     * @param userPreds The User's list of Predictions
     * @param k The accuracy of our predictions (i.e. prec@k, rec@k)
     * @return F1Score The F1 Score for this particular user
     */
    @Override
    public double evaluate(User usr, LinkedList<Prediction> userPreds, int k) {
        
        // F1 Score relies on Precision and Recall, so first calculate these
        Precision prec = new Precision();
        Recall rec = new Recall();
        double precision = prec.evaluate(usr, userPreds, k);
        double recall = rec.evaluate(usr, userPreds, k);
        
        // F1 Score = 2 * ( precision * recall) / ( precision + recall) )
        double F1Score = 2 * ((precision * recall) / (precision + recall));
        
        // Make sure that we don't divide by zero and such
        if(Double.isNaN(F1Score)) {
            F1Score = 0.0;
        }
        return F1Score;
    }
    
    /**
     * This method calculates the average F1 score for all Users in a given HashMap. If a User
     * does not have enough Predictions we don't count this. 
     * 
     * @param userHash The Users for which we wish to calculate the F1 Score
     * @param k The accuracy of the calculation (i.e. prec@k, rec@k)
     * @return totalF1Score The average F1 Score for all Users w/ enough Predictions
     */
    @Override
    public double evaluate(HashMap<User, LinkedList<Prediction>> userHash, int k) {
        double totalF1Score = 0.0;
        int userCount = 0;
        for(Map.Entry<User, LinkedList<Prediction>> mapEntry : userHash.entrySet()) { // For each User
            // Make sure the User has enough Predictions
            if(mapEntry.getValue().size() >= k) {
                // Calculate this User's F1 Score and add it to the total
                totalF1Score += evaluate(mapEntry.getKey(), mapEntry.getValue(), k);
                userCount++;
            }
        }
        
        totalF1Score /= userCount; // Find the average
        
        if(userCount == 0 || totalF1Score == Double.NaN) { // Make sure we don't divide by zero
            totalF1Score = 0;
        }
        
        return totalF1Score;
    }
         
}
