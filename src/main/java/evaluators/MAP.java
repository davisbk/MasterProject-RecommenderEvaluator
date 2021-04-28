
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
 * This class calculates the Mean Average Precision for a User, a set of Users, or a List of sets of Users. 
 * For more information, see: 
 * https://en.wikipedia.org/wiki/Evaluation_measures_(information_retrieval)#Mean_average_precision 
 *  
 * @author Brian Davis
 */
public class MAP implements EvaluatorInterface {
    
    /**
     * This method calculates the Mean Average Precision for a single User. In this method we
     * can assume that there are enough Predictions in the passed LinkedList "predictions"
     * to be able to calculate the MAP.
     * 
     * @param usr The User for which we wish to calculate the MAP
     * @param userPreds The Predictions for this User
     * @param k The number of Predictions to take into account
     * @return prec The Mean Average Precision for this User
     */
    @Override
    public double evaluate(User usr, LinkedList<Prediction> userPreds, int k) {
        
        // If it has been flagged that all Predictions should be used, set k to the proper size. 
        // For the second condition, i.e. if this method was called and the requested k is greater 
        // than the size, return the MAP for the full list. This should not happen in 
        // normal practice since it is unlikely that MAP would be calculated for a single User,
        // independently of any others. Normal practice is that the HashMap method would call this
        // method for each User, and there it is checked whether the User has enough Predictions.
        if(k < 0 || k > userPreds.size()) {
            k = userPreds.size();
        }
        
            
        double prec = 0.0; // The final returned MAP
        
        double userAverage = usr.getAvgRating(); // The average Rating value of the User
        HashMap<Movie, Integer> ratingHash = new HashMap<>();
        ArrayList<Rating> userRatings = usr.getTestRatings();
        userRatings.sort(null);
        
        for(Rating rating : userRatings.subList(0, k)) {
            ratingHash.put(rating.getMovie(), rating.getRating());
        }
       
        // MAP = sum(aveP) / |N| for i= 1 to N , where aveP = sum(prec@k)/ k for k = 1 to n
        Precision precision = new Precision();
        for(int i = 1; i <= k; i++) {
            int indicator = 0;
            
                        
            if(ratingHash.containsKey(userPreds.get(i-1).getMovie())) {
                if(ratingHash.get(userPreds.get(i-1).getMovie()) >= userAverage) {
                    indicator = 1;
                }
            }
            
            prec += (precision.evaluate(usr,userPreds,i) * indicator);
            
        }
        
       prec /= k;
       
       if(Double.isNaN(prec)) {
           prec = 0;
       }
        
        return prec;
    }
    
    /**
     * This method calculates the average Mean Average Precision (MAP) for a set of Users. If a User
     * does not have at least k predictions, we don't count it towards the MAP.
     * 
     * @param userHash The HashMap containing the Users for whom we wish to calculate the MAP
     * @param k How many Predictions to take into account
     * @return MAP The MAP for all Users 
     */
    @Override
    public double evaluate(HashMap<User, LinkedList<Prediction>> userHash, int k) {
        double MAP = 0.0;
        int userCount = 0;
        
        for(Map.Entry<User, LinkedList<Prediction>> mapEntry : userHash.entrySet()) { // For each User
            if(mapEntry.getValue().size() >= k) {
                MAP += this.evaluate(mapEntry.getKey(), mapEntry.getValue(), k);
                userCount++;
            }
            else {
                // If the User does not have enough Predictions, we don't calculate MAP for them
            }
        }
        
        MAP /= userCount;
        
        if(Double.isNaN(MAP)) {
            MAP = 0.0;
        }
        
        return MAP;
    }
    
}
