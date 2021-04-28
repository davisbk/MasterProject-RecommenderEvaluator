package scoreToRating;

import datastructures.User;
import recommenders.RecommenderInterface;

/**
 * Interface to follow to create new ways to change a prediction value into a rating.
 * @author Juan David Mendez
 */
public interface ScoreToRatingInterface {

    /**
     * Takes a prediction score and returns a prediction rating.
     * @param recommender to use to transform scores
     * @param user To transform scores for
     * @param score Score to transform
     * @return Rating
     */
    public double scoreToRating(RecommenderInterface recommender, User user, Double score); 
}
