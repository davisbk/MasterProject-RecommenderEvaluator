
package evaluators;

import datastructures.Movie;
import datastructures.Prediction;
import datastructures.Rating;
import datastructures.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import utils.Printer;
import utils.RatingsSorter;

/**
 * Normalized Discounted Cumulative Gain  
 * Gives a value to a recommended list by adding the ratings of each moving using
 * a logarithmic reduction function that depends on the position in the list.
 * The normalized value is obtained by comparing the recommended value to the maximum
 * value a list could have if all movies were rated in the proper order. 
 * https://en.wikipedia.org/wiki/Discounted_cumulative_gain
 * 
 * @author Brian Davis
 */

public class NDCG implements EvaluatorInterface {
    Printer printer = Printer.getCurrentPrinter();
    
    @Override
    public double evaluate(User usr, LinkedList<Prediction> userPreds, int k) {
        
        double dcg = 0.0;
        double idcg = 0.0;
        
        // If k is a flag (i.e. < 0, likely -1 ) or is larger than the total size, 
        // set k to the numbe of predictions.
        if(k < 0 || k > userPreds.size()) {
            k = userPreds.size();
        }
        
        // First we need to calculate the maximum possible DCG score
        // (i.e. the IDCG, ideal DCG) for the provided top-k list.
        ArrayList<Rating> idealDCGList = new ArrayList<>();
                
        // Since we're using the Rating scores for all predicted movies,
        // put all of the test ratings into a HashMap for faster retrieval later. 
        // This avoids having to repeatedly iterate over an ArrayList of Ratings. 
        HashMap<Movie, Rating> testRatingHash = new HashMap<>();
        for(Rating currentRating : usr.getTestRatings()) {
            testRatingHash.put(currentRating.getMovie(), currentRating);
        }
        
        // Now add all the predictions from the top-k list into our "ideal" ArrayList
        for(Prediction temp : userPreds.subList(0,k)) {
            idealDCGList.add(testRatingHash.get(temp.getMovie()));
        }
        
        // Now sort the list based on the rating score first and title second
        idealDCGList.sort(new RatingsSorter());
        
        // Now calculate the DCG of this ideal list, which is the maximum possible, if this
        // top-k list were given in the perfect order
        int rank = 1;
        double userAverage = usr.getAvgRating();
        for(Rating currentRating : idealDCGList.subList(0, k)) {
            int ratingScore = currentRating.getRating();
            
            if(rank < 2) {
                if(ratingScore >= userAverage) {
                    idcg = 1.0;
                }
            }
            else {
                if(ratingScore >= userAverage) {
                    idcg += ( 1.0 / (Math.log10(rank) / Math.log10(2)));
                }  
            }
            rank++;
            
        }
        
        
        // Finally, calculate the actual DCG of the predicted top-k list
        rank = 1;
        
        for (Prediction temp : userPreds.subList(0, k)) {
            
            int movieID = temp.getMovie().getId();
            int rating = usr.getTestRatingByID(movieID);
            if(rank < 2) {
                if(rating >= userAverage) {
                    dcg = 1.0;
                }
            } else {
                
            
                if(rating >= userAverage) {
                    dcg += (1.0 / (Math.log10(rank) / Math.log10(2)));
                }
            }
            rank++;
           
	}
        
        double nDCG = dcg/idcg;
        
        return nDCG;
        
        
    }
    
    /**
     * Compute the average NDCG value across a group of Users. If a User does
     * not have enough Predictions in their test set, they are not counted towards
     * the average NDCG. 
     * 
     * @param userPredHash HashMap of users and their respective recommendation list
     * @return the average normalize value of all lists. 
     */
    @Override
    public double evaluate(HashMap<User, LinkedList<Prediction>> userPredHash, int k) {
        double finalResult = 0.0;
        int userCounter = 0;
        
        for(Map.Entry<User, LinkedList<Prediction>> mapEntry : userPredHash.entrySet()) {
            User usr = mapEntry.getKey();
            LinkedList<Prediction> userPreds = mapEntry.getValue();
            
            if(userPreds.size() >= k) {
                finalResult += this.evaluate(usr, userPreds, k);
                userCounter++;
            }
                        
        }
        
        finalResult /= userCounter;
        
        if(Double.isNaN(finalResult)) {
            finalResult = 0.0;
        }
        
        return finalResult;
    }   
}
