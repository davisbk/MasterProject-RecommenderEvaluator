package evaluators;

import datastructures.Prediction;
import datastructures.User;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Interface for evaluators. 
 * @author Juan David Mendez
 * 
 */
public interface EvaluatorInterface {
    
    /**
     * For evaluating a collection of Users and their Predictions
     * 
     * @param userHash A HashMap of Users and their Predictions
     * @param k The number of Predictions to evaluate
     * @return The evaluated value for the k Users
     */
    
    public double evaluate(HashMap<User, LinkedList<Prediction>> userHash, int k);
    
    /**
     * For evaluating a single User
     * @param usr The User whose Predictions are to be evaluated
     * @param userPreds The Predictions for the User
     * @param k The number of the top-k list to evaluate
     * @return The evaluated value for the k Users
     */
    public double evaluate(User usr, LinkedList<Prediction> userPreds, int k);
}
