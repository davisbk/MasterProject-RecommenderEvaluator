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
 * This class tests the F1Score evaluator class
 * 
 * @author Brian Davis
 */
public class F1ScoreTest {
    final double ERROR_DELTA = 0.001;
    static DBManager dbmgr;
    public F1ScoreTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        Settings.loadNewSetting("test_settings.cfg");
        dbmgr = DBManager.getInstance();
        dbmgr.getDataWithEnoughRatings();
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
     * Test of evaluate method, of class F1Score.
     */
    @Test
    public void testEvaluateUser() {
        System.out.println("F1ScoreTest.evaluate(User,LinkedList<Prediction>, int)");
        final int k = 5;
        
        //Create a User to calculate their F1 Score. First create Movies for this User.
        Movie movie1 = DBManager.getMovies().get(1357); // Rating 5
        Movie movie2 = DBManager.getMovies().get(3068); // Rating 4
        Movie movie3 = DBManager.getMovies().get(647); // Rating 3
        Movie movie4 = DBManager.getMovies().get(2194); // Rating 4
        Movie movie5 = DBManager.getMovies().get(648); // Rating 4
        Movie movie6 = DBManager.getMovies().get(2628); // Rating 3
        Movie movie7 = DBManager.getMovies().get(1103); // Rating 3
        Movie movie8 = DBManager.getMovies().get(3468); // Rating 5
        Movie movie9 = DBManager.getMovies().get(3578); // Rating 5
        Movie movie10 = DBManager.getMovies().get(1210); //Rating 4
        // Add some training Ratings for this User
        ArrayList<Rating> user1Ratings = new ArrayList<>();
        user1Ratings.add(new Rating(movie1, 5, 12345));
        user1Ratings.add(new Rating(movie2, 4, 12346));
        user1Ratings.add(new Rating(movie3, 3, 12347));
        user1Ratings.add(new Rating(movie4, 4, 12348));
        user1Ratings.add(new Rating(movie5, 4, 12349));
        
        ArrayList<Rating> user1TestRatings = new ArrayList<>();
        
        user1TestRatings.add(new Rating(movie6,3, 12350));
        user1TestRatings.add(new Rating(movie7,3, 12351));
        user1TestRatings.add(new Rating(movie8,5, 12352));
        user1TestRatings.add(new Rating(movie9,5, 12353));
        user1TestRatings.add(new Rating(movie10,4, 12354));
        
        User user1 = new User(2, "M",20,3,83301,user1Ratings, user1TestRatings);
        
        // Now create some Predictions for this User
        LinkedList<Prediction> userPredictions = new LinkedList<>();
        userPredictions.add(new Prediction(movie6,3));
        userPredictions.add(new Prediction(movie7,3));
        userPredictions.add(new Prediction(movie8,3));
        userPredictions.add(new Prediction(movie9,2));
        userPredictions.add(new Prediction(movie10,4));
        
        // The Recall should be 1.0. Of the 3 Movies w/ rating >= 4 (the user average), we included all of them on the top-k list. 
        Recall recall = new Recall();
        final double expectedRecall = 1.0;
        double actualRecall = recall.evaluate(user1, userPredictions, k);
        assertEquals(expectedRecall, actualRecall, ERROR_DELTA);
        
        // The Precision should be 0.6... Of the 5 Movies we have on the top-k list, 3 have a rating >= 4 (the user average)
        // and 2 do not, so our precision is 3 / (3+2) = 0.6
        Precision precision = new Precision();
        final double expectedPrecision = 0.6;
        double actualPrecision = precision.evaluate(user1, userPredictions, k);
        assertEquals(expectedPrecision, actualPrecision, ERROR_DELTA);
        
        // F1 score = 2 * ((precision*recall) / (precision + recall)) = 2 * ( 0.333 / 1.333 ) = 0.5
        F1Score f1score = new F1Score();
        final double expectedF1Score = 0.75;
        double actualF1Score = f1score.evaluate(user1, userPredictions, k);
        assertEquals(expectedF1Score, actualF1Score, ERROR_DELTA);
        
    }

    /**
     * Test of evaluate method, of class F1Score.
     */
    @Test
    public void testEvaluateHashMap() {
       
        System.out.println("F1ScoreTest.evaluate(HashMap<User, LinkedList<Prediction>> userMap, int k)");
        HashMap<User, LinkedList<Prediction>> userMap;
        final int k = 2;
        F1Score F1ScoreInstance; 
        
        //Create a couple Users to calculate their F1 Score. First create Movies for the Users.
        
        // User2's Movies
        Movie movie1 = DBManager.getMovies().get(1357); // Rating 5
        Movie movie2 = DBManager.getMovies().get(3068); // Rating 4
        Movie movie3 = DBManager.getMovies().get(647); // Rating 3
        Movie movie4 = DBManager.getMovies().get(2194); // Rating 4
        Movie movie5 = DBManager.getMovies().get(648); // Rating 4
        
        // User3's Movies
        Movie movie6 = DBManager.getMovies().get(1641); // Rating 2
        Movie movie7 = DBManager.getMovies().get(648); // Rating 3
        Movie movie8 = DBManager.getMovies().get(1394); // Rating 4
        Movie movie9 = DBManager.getMovies().get(3534); // Rating 3
        Movie movie10 = DBManager.getMovies().get(104); // Rating 4
        
        // User2's training Ratings
        ArrayList<Rating> user2Ratings = new ArrayList<>();
        user2Ratings.add(new Rating(movie1,5,12345));
        user2Ratings.add(new Rating(movie2,4,12346));
        user2Ratings.add(new Rating(movie3,3,12347));
        
        // User2's test Ratings
        ArrayList<Rating> user2TestRatings = new ArrayList<>();
        user2TestRatings.add(new Rating(movie4,4,12348));
        user2TestRatings.add(new Rating(movie5,4,12349));
        
        //    public User(int id, String gender, int age_range, int occupation, int zipCode, ArrayList<Rating> ratings, ArrayList<Rating> testRatings)  {
        // Create user2
        User user2 = new User(2, "M", 20, 3, 83301, user2Ratings, user2TestRatings);
        
        // User3's training Ratings
        ArrayList<Rating> user3Ratings = new ArrayList<>();
        user3Ratings.add(new Rating(movie6,2, 12350));
        user3Ratings.add(new Rating(movie7,3, 12351));
        user3Ratings.add(new Rating(movie8,4, 12352));
        
        // User3's test Ratings
        ArrayList<Rating> user3TestRatings = new ArrayList<>();
        user3TestRatings.add(new Rating(movie9,3, 12353));
        user3TestRatings.add(new Rating(movie10,1, 12354));
        
        User user3 = new User(3, "F", 20, 3, 90210, user3Ratings, user3TestRatings);
        
        // Now create Predictions for both Users
        
        // User2's Predictions
        LinkedList<Prediction> user2Preds = new LinkedList<>();
        user2Preds.add(new Prediction(movie4,4));
        user2Preds.add(new Prediction(movie5,4));
        
        // User3's Predictions
        LinkedList<Prediction> user3Preds = new LinkedList<>();
        user3Preds.add(new Prediction(movie9,2));
        user3Preds.add(new Prediction(movie10,5));
        
        // Now create our User HashMap to pass on to evaluate
        userMap = new HashMap<>();
        userMap.put(user2, user2Preds);
        userMap.put(user3, user3Preds);
        
        // Now evaluate!
        F1ScoreInstance  = new F1Score();
        final double expectedOverallF1Score = 0.833;
        double actualOverallF1Score = F1ScoreInstance.evaluate(userMap, k);
        assertEquals(expectedOverallF1Score, actualOverallF1Score, ERROR_DELTA);
        
        final double expectedUser2F1Score = 1.0;
        double actualUser2F1Score = F1ScoreInstance.evaluate(user2,user2Preds, k);
        assertEquals(expectedUser2F1Score, actualUser2F1Score, ERROR_DELTA);
        
        final double expectedUser3F1Score = 0.666;
        double actualUser3F1Score = F1ScoreInstance.evaluate(user3,user3Preds, k);
        assertEquals(expectedUser3F1Score, actualUser3F1Score, ERROR_DELTA);
        
        
    }
    
}
