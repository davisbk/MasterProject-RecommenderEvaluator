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
 * This class tests the Recall evaluator class. 
 * 
 * @author Brian Davis
 */
public class RecallTest {
    private static final double ERROR_DELTA = 0.001;
    
    /**
     *
     */
    public RecallTest() {
    }
    
    /**
     * Load the test_settings.cfg file to ensure that Movies are loaded, the various Settings (Rocchio parameters, etc.) are loaded
     */
    @BeforeClass
    public static void setUpClass() {
        Settings.loadNewSetting("test_settings.cfg");
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
    
    /**
     *  Tests Recall.evaluate(Use usr, LinkedList<Prediction> predictions, int k) method. 
     */
    @Test
    public void testEvaluateUser() {
        System.out.println("RecallTest.evaluate(User usr, LinkedList<Prediction> predictions, int k)");
        
        // Get Movies from the database
        DBManager dbmgr = DBManager.getInstance(); // Make sure it's initialized
        HashMap<Integer,Movie> movies = DBManager.getMovies(); // Get the Movies
        
        // User 2's Movies
        Movie movie1 = movies.get(2194); // Rating 4
        Movie movie2 = movies.get(2628); // Rating 3
        Movie movie3 = movies.get(2916); // Rating 3
        Movie movie4 = movies.get(1210); // Rating 4
        Movie movie5 = movies.get(1213); // Rating 2
        Movie movie6 = movies.get(3468); // Rating 5
        
        // Set up the User (uid = 2)
        ArrayList<Rating> trainingRatings1 = new ArrayList<>();
        trainingRatings1.add(new Rating(movie1,4,987654));
        trainingRatings1.add(new Rating(movie2,3,987655));
        trainingRatings1.add(new Rating(movie3,3,987656));
        
        ArrayList<Rating> testRatings1 = new ArrayList<>();
        testRatings1.add(new Rating(movie4,4,12345));
        testRatings1.add(new Rating(movie5,2,12343));
        testRatings1.add(new Rating(movie6,5,12342));
        
        User user2 = new User(2,"M",10,2,90210,trainingRatings1,testRatings1);
        
        LinkedList<Prediction> predictions = new LinkedList<>();
        predictions.add(new Prediction(movie4,4)); // Correct
        predictions.add(new Prediction(movie5,2)); // Correct
        predictions.add(new Prediction(movie6,2)); // Was actually rated a 5
        predictions.add(new Prediction(movie4,4)); // Should not influence the score because k=3. (Normally predictions would be sorted, but we're testing)
        
        Recall recall = new Recall();
        final double expectedRecall = 1.0;
        double actualRecall = recall.evaluate(user2, predictions, 3);
        
        assertEquals(expectedRecall, actualRecall, ERROR_DELTA);
    }

    /**
     * Test of Recall.evaluate(HashMap<User, LinkedList<Prediction>> userHash, int k) method
     */
    @Test
    public void testEvaluateHashMap() {
        System.out.println("RecallTest.evaluate(HashMap<User, LinkedList<Prediction>> userHash, int k)");
        
        HashMap<User, LinkedList<Prediction>> userHash; // A HashMap to store all Users
        
        // Get Movies from the database
        DBManager dbmgr = DBManager.getInstance(); // Make sure it's initialized
        dbmgr.getDataWithEnoughRatings();
        HashMap<Integer,Movie> movies = DBManager.getMovies(); // Get the Movies
        
        // Make some movies. These particular Movies are chosen because they are loaded by our test_settings.cfg
        Movie movie1 = movies.get(2194); // Rating 4
        Movie movie2 = movies.get(2628); // Rating 3
        Movie movie3 = movies.get(2916); // Rating 3
        Movie movie4 = movies.get(1210); // Rating 4
        Movie movie5 = movies.get(1213); // Rating 2
        Movie movie6 = movies.get(1641); // Rating 2
        Movie movie7 = movies.get(648); // Rating 3
        Movie movie8 = movies.get(1394); // Rating 4
        Movie movie9 = movies.get(3534); // Rating 3
        Movie movie10 = movies.get(104); // Rating 4
        
        // Now to make some Users
        
        // Give the first User Ratings
        ArrayList<Rating> ratings1 = new ArrayList<>();
        ratings1.add(new Rating(movie1,1,988971));
        ratings1.add(new Rating(movie2,2,984811));
        ratings1.add(new Rating(movie3,3,981012));
        
        
        // And test Ratings
        ArrayList<Rating> testRatings1 = new ArrayList<>();
        testRatings1.add(new Rating(movie7,3,987654));
        testRatings1.add(new Rating(movie8,2,931111));
        testRatings1.add(new Rating(movie9,1,822011));
        testRatings1.add(new Rating(movie10,4,940105));
        
        // Create User 1 from this data
        User user1 = new User(1,"male", 2, 5, 83301,ratings1,testRatings1);
        
        // Create some Predictions for this User
        LinkedList<Prediction> user1Predictions = new LinkedList<>();
        // movie7 will not be added. 
        user1Predictions.add(new Prediction(movie8,4));
        user1Predictions.add(new Prediction(movie9,2));
        user1Predictions.add(new Prediction(movie10,3));
        
        // Create the second User
        
        // Create Ratings for this User
        ArrayList<Rating> ratings2 = new ArrayList<>();
        ratings2.add(new Rating(movie4,4,916210));
        ratings2.add(new Rating(movie5,5,902100));
        ratings2.add(new Rating(movie6,4,867530));
        
        // And test Ratinsg
        ArrayList<Rating> testRatings2 = new ArrayList<>();
        testRatings2.add(new Rating(movie8,3, 12345));
        testRatings2.add(new Rating(movie9,5, 12346));
        testRatings2.add(new Rating(movie10,2, 12347));
        
        // Create User 2 from this data
        User user2 = new User(2,"female", 3, 5, 90210 ,ratings2,testRatings2);
        
        // Create some Predictions for this User
        LinkedList<Prediction> user2Predictions = new LinkedList<>();
       
        user2Predictions.add(new Prediction(movie8,4));
        user2Predictions.add(new Prediction(movie9,5));
        user2Predictions.add(new Prediction(movie10,3));
        
        // Add the Predictions to the userHash
        userHash = new HashMap<>();
        userHash.put(user1,user1Predictions);
        userHash.put(user2,user2Predictions);
        
        // Calculate recall
        Recall recall = new Recall();
        final double expectedUser1Result = 0.666; 
        double actualUser1Result = recall.evaluate(user1, user1Predictions, 3);
        assertEquals(expectedUser1Result, actualUser1Result, ERROR_DELTA);
        
        final double expectedUser2Result = 1.0; 
        double actualUser2Result = recall.evaluate(user2, user2Predictions, 3);
        assertEquals(expectedUser2Result, actualUser2Result, ERROR_DELTA);
        
        final double expTotalResult = 0.833; // (0.666 + 1.0) / 2 = 0.833.  2 Users!
        double result = recall.evaluate(userHash,3);
        assertEquals(expTotalResult, result, ERROR_DELTA);
        
    }
}
