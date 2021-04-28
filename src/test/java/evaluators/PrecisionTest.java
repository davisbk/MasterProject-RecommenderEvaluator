package evaluators;

import datastructures.Movie;
import datastructures.Prediction;
import datastructures.PropertiesHash;
import datastructures.Property;
import datastructures.Rating;
import datastructures.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import utils.DBManager;
import utils.Settings;

/**
 * This class tests the Precision evaluator class. 
 * 
 * @author Juan David Mendez
 */
public class PrecisionTest {
    private static HashMap<User, LinkedList<Prediction>> list1;
    private final static double ERROR_DELTA = 0.001;
    
    /**
     *
     */
    public PrecisionTest() {
    }
    
    /**
     *
     */
    @BeforeClass
    public static void setUpClass() {
        Settings.loadNewSetting("test_settings.cfg");
        list1 = new HashMap<>();
        PropertiesHash properties1 = new PropertiesHash();
        properties1.add(new Property("genre", "comedy"), 1.0);
        properties1.add(new Property("country", "united states"), 1.0);
        properties1.add(new Property("director", "john c. walsh"), 1.0);
        PropertiesHash properties2 = new PropertiesHash();
        properties2.add(new Property("genre", "comedy"), 1.0);
        properties2.add(new Property("genre", "horror"), 1.0);
        properties2.add(new Property("director", "john c. walsh"), 1.0);        
        properties2.add(new Property("country", "france"), 1.0);
        PropertiesHash properties3 = new PropertiesHash();
        properties3.add(new Property("country", "italy"), 1.0);
        properties3.add(new Property("director", "john hough"), 1.0);
        properties3.add(new Property("genre", "boo"), 1.0);
        properties3.add(new Property("genre", "french"), 1.0);
        PropertiesHash properties4 = new PropertiesHash();
        properties4.add(new Property("genre", "thriller"), 1.0);
        PropertiesHash properties5 = new PropertiesHash();
        properties5.add(new Property("genre", "american"), 1.0);
        properties5.add(new Property("genre", "comedy"), 1.0);      
        Movie movie1 = new Movie(1, "url1", "Toy Story", properties1);
        Movie movie2 = new Movie(2, "url2", "Toy Story 2", properties2);
        Movie movie3 = new Movie(3, "url3", "Trainspotting", properties3);
        Movie movie4 = new Movie(4, "url4", "Superman", properties4);
        Movie movie5 = new Movie(5, "url5", "Superman 2", properties5);
        ArrayList<Rating> trainingRatings1 = new ArrayList<>();
        trainingRatings1.add(new Rating(movie1, 3, 213));
        trainingRatings1.add(new Rating(movie2, 1, 1231));
        trainingRatings1.add(new Rating(movie3, 5, 21312));
        ArrayList<Rating> user1TestRatings = new ArrayList<>();  
        user1TestRatings.add(new Rating(movie4, 2, 123213));
        user1TestRatings.add(new Rating(movie5, 4, 1232133));
        User user1 = new User(1, "male", 1, 1, 1, trainingRatings1, user1TestRatings);
        LinkedList<Prediction> prediction = new LinkedList<>();
        prediction.add(new Prediction(movie4, 3));
        prediction.add(new Prediction(movie5, 3));
        list1.put(user1, prediction);
    }
    
    /**
     *
     */
    @AfterClass
    public static void tearDownClass() {
    }
    
    /**
     *
     */
    @Before
    public void setUp() {
    }
    
    /**
     *
     */
    @After
    public void tearDown() {
    }

    @Test
    public void testEvaluateUser() {
        System.out.println("PrecisionTest.evaluate(User usr, LinkedList<Prediction> predictions, int k)");
        
        // Get Movies from the database
        DBManager dbmgr = DBManager.getInstance(); // Make sure it's initialized
        dbmgr.getDataWithEnoughRatings();
        HashMap<Integer,Movie> movies = DBManager.getMovies(); // Get the Movies
        
        // User 2's Movies
        Movie movie1 = movies.get(2194); // Rating 4
        Movie movie2 = movies.get(2628); // Rating 3
        Movie movie3 = movies.get(2916); // Rating 3
        Movie movie4 = movies.get(1210); // Rating 4
        Movie movie5 = movies.get(1213); // Rating 2
        Movie movie6 = movies.get(368); // Rating 4
        Movie movie7 = movies.get(3578); // Rating 5
        Movie movie8 = movies.get(3334); // Rating 4
        
        // Set up the User (uid = 2)
        ArrayList<Rating> trainingRatings1 = new ArrayList<>();
        trainingRatings1.add(new Rating(movie1,4,987654));
        trainingRatings1.add(new Rating(movie2,3,987655));
        trainingRatings1.add(new Rating(movie3,3,987656));
        
        ArrayList<Rating> testRatings1 = new ArrayList<>();
        testRatings1.add(new Rating(movie4,4,12345));
        testRatings1.add(new Rating(movie5,2,12343));
        testRatings1.add(new Rating(movie6,2,15910));
        testRatings1.add(new Rating(movie7,5,23311));
        testRatings1.add(new Rating(movie8,4,12345));
        
        User user2 = new User(2,"M",10,2,90210,trainingRatings1,testRatings1);
        
        LinkedList<Prediction> predictions = new LinkedList<>();
        predictions.add(new Prediction(movie4,4)); // This is correct
        predictions.add(new Prediction(movie5,2)); // This one doesn't count (below user average)
        predictions.add(new Prediction(movie6,4)); // This will be a false positive
        predictions.add(new Prediction(movie7,5)); // This is correct
        predictions.add(new Prediction(movie8,4)); // Also correct
        
        
        Precision precision = new Precision();
        final double expectedPrecision = 0.5; // movie4 is correct, movie5 is not relevant, movie6 is a false positive, and movie7 correct
        double actualPrecision = precision.evaluate(user2, predictions,4);
        
        assertEquals(expectedPrecision, actualPrecision, ERROR_DELTA);
        
    }
    
    /**
     * Test of Precision.evaluate(HashMap<User,LinkedList<<Prediction>> list).
     */
    @Test
    public void testEvaluateHashMap() {
        System.out.println("PrecisionTest.evaluate(HashMap<User,LinkedList<<Prediction>> list)");
                
        HashMap<User, LinkedList<Prediction>> userHash;
        Precision precision = new Precision();
        DBManager dbmgr = DBManager.getInstance();
        HashMap<Integer, Movie> movies = DBManager.getMovies();
        
        
        // User 2's Movies
        Movie movie1 = movies.get(2194); // Rating 4
        Movie movie2 = movies.get(2628); // Rating 3
        Movie movie3 = movies.get(2916); // Rating 3
        Movie movie4 = movies.get(1210); // Rating 4
        Movie movie5 = movies.get(1213); // Rating 2
        
        // User 3's Movies
        Movie movie6 = movies.get(1641); // Rating 2
        Movie movie7 = movies.get(648); // Rating 3
        Movie movie8 = movies.get(1394); // Rating 4
        Movie movie9 = movies.get(3534); // Rating 3
        Movie movie10 = movies.get(104); // Rating 4
        
        // Create the first User's ratings (average rating = 3)
        ArrayList<Rating> ratings1 = new ArrayList<>();
        ratings1.add(new Rating(movie1,4,988971));
        ratings1.add(new Rating(movie2,3,984811));
        ratings1.add(new Rating(movie3,3,981012));
        
        
        // And test Ratings
        ArrayList<Rating> testRatings1 = new ArrayList<>();
        testRatings1.add(new Rating(movie4,4,987654)); 
        testRatings1.add(new Rating(movie5,2,987655));
        
        
        // Create User from this data
        User user1 = new User(2,"male", 2, 5, 83301,ratings1,testRatings1);
        
        // Create some Predictions for this User
        LinkedList<Prediction> userPredictions1 = new LinkedList<>();
        
        userPredictions1.add(new Prediction(movie4,4));
        userPredictions1.add(new Prediction(movie5,2));
        
        // Create our second User's training Ratings (average rating = 3)
        ArrayList<Rating> ratings2 = new ArrayList<>();
        ratings2.add(new Rating(movie6,2,988971));
        ratings2.add(new Rating(movie7,3,984811));
        ratings2.add(new Rating(movie8,4,981012));
        
        
        // And test Ratings
        ArrayList<Rating> testRatings2 = new ArrayList<>();
        testRatings2.add(new Rating(movie9,3,987654));
        testRatings2.add(new Rating(movie10,4,987655));
        
        // Create some Predictions for this User also
        LinkedList<Prediction> userPredictions2 = new LinkedList<>();
        userPredictions2.add(new Prediction(movie9,2));
        userPredictions2.add(new Prediction(movie10,2));
        
        User user2 = new User(3,"F", 2, 5, 90210 ,ratings2, testRatings2);
        
        // Add our two Users to the list
        userHash = new HashMap<>();
        userHash.put(user1,userPredictions1);
        userHash.put(user2,userPredictions2);
        
        
        // Now evaluate our overall Precision (evaluate(HashMap))
        
        // The Precision of the HashMap should be the average of the two individuals, so we can find out what
        // these two values are and then take the average.
        
        // User1
        final double expectedUser1Prec = 0.5;
        double actualUser1Prec = precision.evaluate(user1, userPredictions1,2);
        assertEquals(expectedUser1Prec, actualUser1Prec, ERROR_DELTA);
        
        // User2
        final double expectedUser2Prec = 1.0;
        double actualUser2Prec = precision.evaluate(user2, userPredictions2, 2);
        assertEquals(expectedUser2Prec, actualUser2Prec, ERROR_DELTA);
        
        final double expectedOverallPrecision = 0.75; // (1.0 + 0.5) / 2 = 0.75
        double actualOverallPrecision = precision.evaluate(userHash,2);
        
        assertEquals(expectedOverallPrecision, actualOverallPrecision, ERROR_DELTA);
                
    }
  
}
