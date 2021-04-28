
package scoreToRating;

import datastructures.Movie;
import datastructures.Prediction;
import datastructures.Rating;
import datastructures.RatingRange;
import datastructures.User;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import recommenders.RecommenderInterface;
import utils.DBManager;

/**
 * Transforms scores to ratings based on users max and minimum ratings
 * @author Juan David Mendez
 * @date Jan 22, 2016
 */
public class SimpleScoreToRating {
    
    /**
     * Constructor
     */
    public SimpleScoreToRating()   {
    }

    /**
     * Takes A hashmap of predictions and transforms them into rating values
     * @param recommender used to create the predictions
     * @param prediction Hashmap of users and their predictions
     * @return Hashmap of users and predictions transformed into ratings.
     */
    public HashMap<User, LinkedList<Prediction>> scoreToRating(RecommenderInterface recommender, HashMap<User, LinkedList<Prediction>> prediction) {
        HashMap<User, LinkedList<Prediction>> newPredictions = new HashMap<>();
        for (Map.Entry<User, LinkedList<Prediction>> entry : prediction.entrySet()) {
            LinkedList<Prediction> predictionForUser = scoreToRating(recommender, entry.getKey(), entry.getValue());
            predictionForUser.sort(null);
            newPredictions.put(entry.getKey(), predictionForUser);
        }
        return newPredictions;
    }
    
    /**
     * Transforms predictions done by a recommender based in one user into rating scores.
     * @param recommender used to create the predictions
     * @param user that the predictions are based on
     * @param prediction created by the recommender
     * @return predictions transformed into ratings.
     */
    public LinkedList<Prediction> scoreToRating(RecommenderInterface recommender, User user, LinkedList<Prediction> prediction) {
        ArrayList<RatingRange> ratingRanges = getRatingRanges(user, recommender);
        LinkedList<Prediction> newPredictions = new LinkedList<>();     
        for(int j = 0; j < ratingRanges.size(); j++)    {
            if(j ==0)
                ratingRanges.get(0).setLowestValue(Double.NaN);
            if(j<(ratingRanges.size()-1)) {
                double difference = (ratingRanges.get(j +1).getLowestValue() - ratingRanges.get(j).getHighestValue())/2;
                ratingRanges.get(j).setHighestValue(ratingRanges.get(j).getHighestValue() + difference);
                ratingRanges.get(j+1).setLowestValue(ratingRanges.get(j+1).getLowestValue() - difference);
            }
            else {
                ratingRanges.get(j).setHighestValue(Double.NaN);
            }
        }
        
        for(Prediction moviePrediction : prediction)    {
            int i = 0;
            while(moviePrediction.getValue()>ratingRanges.get(i).getHighestValue()&&i<ratingRanges.size())   {
                i++;
            }
            newPredictions.add(new Prediction(moviePrediction.getMovie(), ratingRanges.get(i).getRating()));
        }
        return newPredictions;
    }
    
    /**
     * Uses the known ratings of a user to create a rating range based on.
     * @param user ratings are related to.
     * @param recommender used to create the predictions
     * @return Range of ratings the user has.
     */
    private ArrayList<RatingRange> getRatingRanges(User user, RecommenderInterface recommender)  {        
        /*
         * Uses the recommender to evaluate all movies rated by user, and then 
         * sort the values into rating buckets.
        */
        Map<Integer, ArrayList<Double>> moviesByRating = new HashMap<>();
        for(Rating currentRating : user.getTrainingRatings()) {
            Movie movie = currentRating.getMovie();
            double movieScore = recommender.predictMovie(user, movie);
            if(moviesByRating.containsKey(currentRating.getRating()))    {
                ArrayList<Double> list = moviesByRating.get(currentRating.getRating());
                list.add(movieScore);
            }
            else {
                ArrayList<Double> list = new ArrayList<>();
                list.add(movieScore);
                moviesByRating.put(currentRating.getRating(), list);
            }
        }
        /*
         * Sorts the values inside the array, and then creates a new RatingRange
         * with the min and max scores per rating
        */
        ArrayList<RatingRange> ratingRanges = new ArrayList<>();
        for (Map.Entry<Integer, ArrayList<Double>> entry : moviesByRating.entrySet()) {  
            ArrayList<Double> list = entry.getValue();
            Collections.sort(entry.getValue());
            RatingRange range = new RatingRange(entry.getKey(), list.get(0), list.get(list.size()-1));
            ratingRanges.add(range);
        } 
        
        return ratingRanges;
    }
}
