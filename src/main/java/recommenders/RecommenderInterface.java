 package recommenders;

import datastructures.Movie;
import datastructures.Prediction;
import datastructures.User;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Interface for recommenders used in this framework.
 * @author Juan David Mendez
 */

public interface RecommenderInterface {
    
    /**
     * Receives a HashMap of Users and returns a Hashmap with the users and 
     * the predictions for movie in the user's test set.
     * @param users HashMap of users to predict for.
     * @return A HashMap of Users paired with predicted values for the movies in 
     * their test set.
     */
    public HashMap<User, LinkedList<Prediction>> predict(HashMap<Integer, User> users);
    
    /**
     * Predicts the score that a user would give to a movie.
     * @param user to use for predicting the scores of the movie.
     * @param movie to which a prediction based on user is going to be made
     * @return Score that user would give to movie
     */
    public double predictMovie(User user, Movie movie);
}
