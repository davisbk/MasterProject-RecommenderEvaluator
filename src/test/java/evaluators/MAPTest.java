package evaluators;

import datastructures.Movie;
import datastructures.Prediction;
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
 * This class tests the MAP evaluator class.
 * 
 * @author Brian Davis
 */
public class MAPTest {
    
    private final static double ERROR_DELTA = 0.001; // Margin of error for assertEquals comparisons
    private final static DBManager dbManager = DBManager.getInstance();
    
    
    private final HashMap<Integer, Movie> movies = DBManager.getMovies();
    
    public MAPTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        Settings.loadNewSetting("test_settings.cfg");
        dbManager.getDataWithEnoughRatings();
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of evaluate method, of class MAP.
     */
    @Test
    public void testEvaluateUser() {
        System.out.println("MAPTest.evaluate(User usr, LinkedList<Prediction> predictions, int k)");
        final int k = 4;
        
        // Make a new User
        
        // Make some Movies. Get them from the database
        Movie movie1 = movies.get(1357); // Rating 5
        Movie movie2 = movies.get(3068); // Rating 4
        Movie movie3 = movies.get(647); // Rating 3
        Movie movie4 = movies.get(2194); // Rating 4
        Movie movie5 = movies.get(648); // Rating 4
        Movie movie6 = movies.get(2628); // Rating 3
        Movie movie7 = movies.get(1103); // Rating 3
        Movie movie8 = movies.get(2916); // Rating 3
        Movie movie9 = movies.get(3468); // Rating 5
        Movie movie10 = movies.get(1210); // Rating 4
       
       // Put the training Ratings into the required ArrayList (user average = 3)
        ArrayList<Rating> trainingRatings = new ArrayList<>();
        trainingRatings.add(new Rating(movie1,5,981897151));
        trainingRatings.add(new Rating(movie2,4,982374981));
        trainingRatings.add(new Rating(movie3,3,989011922));
        trainingRatings.add(new Rating(movie4,4,982831919));
        trainingRatings.add(new Rating(movie5,4,981082310));
        trainingRatings.add(new Rating(movie6,3,910232101));
       
        
        // Put the test Ratings into the required ArrayList        
        ArrayList<Rating> testRatings = new ArrayList<>();
        testRatings.add(new Rating(movie7,2,982394181));
        testRatings.add(new Rating(movie8,3,981010101));
        testRatings.add(new Rating(movie9,5,981038417));
        testRatings.add(new Rating(movie10,2,98010101));
        
        // Create the User
        User user2 = new User(2,"F",30,3,83301,trainingRatings, testRatings);
        
        // Create some Predictions for the User
        LinkedList<Prediction> user2Preds = new LinkedList<>();
        user2Preds.add(new Prediction(movie7,5)); // Wrong, was rated 2
        user2Preds.add(new Prediction(movie8,3)); // Correct
        user2Preds.add(new Prediction(movie9,1)); // Wrong, was rated 5
        user2Preds.add(new Prediction(movie10,3)); // Wrong, was rated 2
        
        MAP map = new MAP();
        
        final double expectedUser2MAP = 0.292; // (0 + 0.5 + 0.666 + 0 ) / 4 = 0.292
        double actualUser2MAP = map.evaluate(user2, user2Preds, k);
        assertEquals(expectedUser2MAP, actualUser2MAP, ERROR_DELTA);
        
    }

    /**
     * Test of evaluate method, of class MAP.
     */
    @Test
    public void testEvaluateHashMap() {
        System.out.println("MAPTest.evaluate(HashMap<User, LinkedList<Prediction>> userHash, int k)");
        
        HashMap<User, LinkedList<Prediction>> userHash;
        final int k = 2;
        
        // Make some new Users
        
        // Make some Movies. Get them from the database
        Movie movie1 = movies.get(1357); // Rating 5
        Movie movie2 = movies.get(3068); // Rating 4
        Movie movie3 = movies.get(647); // Rating 3
        Movie movie4 = movies.get(2194); // Rating 4
        Movie movie5 = movies.get(648); // Rating 4
        Movie movie6 = movies.get(2628); // Rating 3
        Movie movie7 = movies.get(1103); // Rating 3
        Movie movie8 = movies.get(2916); // Rating 3
        Movie movie9 = movies.get(3468); // Rating 5
        Movie movie10 = movies.get(1210); // Rating 4
        
        // Make some Ratings for these Movies for two Users.
        Rating rating1 = new Rating(movie1,5,981897151);
        Rating rating2 = new Rating(movie2,4,982374981);
        Rating rating3 = new Rating(movie3,3,989011922);
        Rating rating4 = new Rating(movie4,4,982831919);
        Rating rating5 = new Rating(movie5,4,981082310);
        Rating rating6 = new Rating(movie6,3,910232101);
        Rating rating7 = new Rating(movie7,4,982394181);
        Rating rating8 = new Rating(movie8,3,981010101);
        Rating rating9 = new Rating(movie9,5,981038417);
        Rating rating10 = new Rating(movie10,2,98010101);
        
        // Put the training Ratings into the required ArrayList
        ArrayList<Rating> trainingRatings1 = new ArrayList<>();
        trainingRatings1.add(rating1);
        trainingRatings1.add(rating2);
        trainingRatings1.add(rating3);
                
        // Put the test Ratings into the required ArrayList        
        ArrayList<Rating> testRatings1 = new ArrayList<>();
        testRatings1.add(rating7);
        testRatings1.add(rating8);
        
        
        // Create the first User
        User user2 = new User(2,"F",30,3,83301,trainingRatings1, testRatings1);
        
        // Put the training Ratings for the second User into the required ArrayList
        ArrayList<Rating> trainingRatings2 = new ArrayList<>();
        trainingRatings2.add(rating4);
        trainingRatings2.add(rating5);
        trainingRatings2.add(rating6);
        
        // Put the test Ratings for the second user into the required ArrayList
        ArrayList<Rating> testRatings2 = new ArrayList<>();
        testRatings2.add(rating9);
        testRatings2.add(rating10);
        
        // Create the second User
        User user3 = new User(3,"M",30,3,90210, trainingRatings2, testRatings2);
        
        // Now create some predictions for the two Users
        LinkedList<Prediction> user2Preds = new LinkedList<>();
        user2Preds.add(new Prediction(movie7,4));
        user2Preds.add(new Prediction(movie8,3));
        
        LinkedList<Prediction> user3Preds = new LinkedList<>();
        user3Preds.add(new Prediction(movie9,1));
        user3Preds.add(new Prediction(movie10,2));
        
        // Populate our HashMap with our Users and Predictions
        userHash = new HashMap<>();
        userHash.put(user2,user2Preds);
        userHash.put(user3,user3Preds);
        
        // Evaluate
        MAP map = new MAP();
        
        // Our MAP should be the average MAP for both Users.
        
        final double expectedMAP1 = 0.5;
        double actualMAP1 = map.evaluate(user2, user2Preds, k);
        assertEquals(expectedMAP1, actualMAP1, ERROR_DELTA);
        
        final double expectedMAP2 = 0.5;
        double actualMAP2 = map.evaluate(user3, user3Preds, k);
        assertEquals(expectedMAP2, actualMAP2, ERROR_DELTA);
        
        final double expectedMAP = 0.5; // (0.5 + 0.5) / 2 = 0.5
        double actualMAP = map.evaluate(userHash, k);
        assertEquals(expectedMAP, actualMAP, ERROR_DELTA);
    }
}