package utils;
import datastructures.Rating;
import datastructures.User;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

/**
 * Groups and splits the information to be used by a recommender and an
 * evaluator.
 * @author Juan David Mendez
 */
public class DataPreparer { 
    private final DBManager db = DBManager.getInstance(); // DBManager
    
    private final Random randomGenerator = new Random(); // To randomize the splitting of ratings
    double trainingSetSize = Settings.getTrainingSetSize(); //Size of training set
    private final List<HashMap<Integer, User>> userLists =  new ArrayList<>(); //List of hashmap of modified users users 
    
    /*
     * Requests required information from the database, and creates k lists of hashmaps
     * of users to use to evaluate recommenders
    */
    public DataPreparer() {
        //db.getDataWithEnoughRatings();
        //For each user
        for(Entry<Integer, User> entry : db.getUsers().entrySet()) {
            //Get the ratings of user
            ArrayList<Rating> userRating = entry.getValue().getTrainingRatings();
            ArrayList<Integer> wasInTestSet = new ArrayList<>(); 
            // Find the size of the user's test set by multiplying all ratings 
            // of the user by the percentage that should be in training training set.
            int testSetSize = (int) Math.floor(userRating.size() * (1-trainingSetSize));    
            //Creates K variation of the user to be used as fold in the 
            //evaluation of a recommender.
            int k = Settings.getNumFolds();
            //Check if the amount of requested folds is possible with size of 
            //of the training set.
            if(testSetSize==0) {
                System.out.println("User " + entry.getValue().getId() + " does not have enough ratings to have a test set");
                continue;
            }
            if(userRating.size()/testSetSize<k) {
                System.out.println("User " + entry.getValue().getId() + " doesnt support " + k + " folds");
                continue;
            }
            //Counts the folds done for this user
            int folds = 0;
            //Copies the ratings of user X
            ArrayList UserRatingCopy = new ArrayList<>(userRating); 
            //For each possible fold of user X
            while(folds < k) {
                ArrayList<Rating> trainingRating = new ArrayList<>(UserRatingCopy); // Ratings to be used in training set
                ArrayList<Rating> testRatings = new ArrayList<>(); // Ratings to be used in test set
                // Number of ratings placed in the training set
                int count = 0 ; 
                // Randomly picks a ratings from the training set, and checks if it can be moved to the test set.
                while(count < testSetSize) {
                    int random = randomGenerator.nextInt(trainingRating.size());
                    Rating rating = trainingRating.remove(random);
                    
                    if(!wasInTestSet.contains(rating.getMovie().getId())) {  // Check if the rating has been used in the test set before
                        testRatings.add(rating); //Add rating to the test set
                        wasInTestSet.add(rating.getMovie().getId()); // Add rating to the list of ratings already in a test set
                        count++;
                    }
                }    
                //Creates a variation of user X with ratings divided uniquely. 
                Collections.sort(trainingRating, new TimestampSorter());
                User modifiedUser = new User(entry.getValue(), trainingRating, testRatings);
                //Check if a fold has been created else create a new fold
                if(userLists.size()< folds + 1)
                    userLists.add(new HashMap<Integer, User>());
                //Adds a user to a fold.
                userLists.get(folds).put(modifiedUser.getId(), modifiedUser);
                folds++;
            }
        }
                
    }

    /**
     * Returns a list to run k-fold evaluations.
     * @return list of uses prepared for k-fold evaluation.
     */
    public List<HashMap<Integer, User>> getUserLists() {
        return userLists;
    }
    
}
