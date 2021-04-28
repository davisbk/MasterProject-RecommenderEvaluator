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
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import utils.DBManager;
import utils.Settings;

/**
 * This class tests the RMSE Evaluator class. 
 * 
 * @author Brian Davis
 */
public class RMSETest {
    final static double ERROR_DELTA = 0.001;
    
    private static HashMap<User, LinkedList<Prediction>> userHash;
    
    /**
     *
     */
    public RMSETest() {
    }
    
    /**
     *
     */
    @BeforeClass
    public static void setUpClass() {
        Settings.loadNewSetting("test_settings.cfg");
        
        // Get Movies from the database
        DBManager dbmgr = DBManager.getInstance(); // Make sure it's initialized
        dbmgr.getDataWithEnoughRatings();
                
        userHash = new HashMap<>();
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
        Movie movie6 = new Movie(6, "url6", "Lord of the Rings", properties1);
        Movie movie7 = new Movie(7, "url7", "Star Wars", properties2);
        ArrayList<Rating> user1Ratings = new ArrayList<>();
        user1Ratings.add(new Rating(movie1, 3, 213));
        user1Ratings.add(new Rating(movie2, 1, 1231));
        user1Ratings.add(new Rating(movie3, 5, 21312));
        ArrayList<Rating> user1TestRatings = new ArrayList<>();  
        user1TestRatings.add(new Rating(movie4, 2, 123213));
        user1TestRatings.add(new Rating(movie5, 4, 1232133));
        user1TestRatings.add(new Rating(movie6, 3, 12345));
        user1TestRatings.add(new Rating(movie7, 2, 95810));
        User user1 = new User(1, "male", 1, 1, 1, user1Ratings, user1TestRatings);
        LinkedList<Prediction> prediction = new LinkedList<>();
        prediction.add(new Prediction(movie4, 3));
        prediction.add(new Prediction(movie5, 3));
        prediction.add(new Prediction(movie6, 5));
        prediction.add(new Prediction(movie7, 2));
        userHash.put(user1, prediction);
    }
    
    /**
     * Test of evaluate method, of class RMSE.
     */
    @Test
    public void testEvaluateUser() {
        System.out.println("RMSETest.evaluate(User usr, LinkedList<Prediction> preds, int k)");
        final int k = 5;
        
        HashMap<Integer,Movie> movies = DBManager.getMovies(); // Get the Movies
        
        // User 2's Movies
        Movie movie1 = movies.get(2194); // Rating 4
        Movie movie2 = movies.get(2628); // Rating 3
        Movie movie3 = movies.get(2916); // Rating 3
        Movie movie4 = movies.get(1210); // Rating 4
        Movie movie5 = movies.get(1213); // Rating 2
        Movie movie6 = movies.get(368);  // Rating 4
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
        predictions.add(new Prediction(movie4,4)); 
        predictions.add(new Prediction(movie5,2)); 
        predictions.add(new Prediction(movie6,4)); 
        predictions.add(new Prediction(movie7,5));
        predictions.add(new Prediction(movie8,4));
        
        RMSE rmse = new RMSE();
        
        final double expectedRMSE = 0.8944;
        double actualRMSE = rmse.evaluate(user2, predictions,k);
        
        assertEquals(expectedRMSE, actualRMSE, ERROR_DELTA);
    }
    
    @Test
    public void testEvaluateHashMap() {
        System.out.println("RMSETest.evaluate(HashMap<User, LinkedList<Prediction>> userHash, int k)");
        final int k = 4;
        RMSE rmseInstance = new RMSE();
        
        // First we simply use the data from the @BeforeClass section above. This only has one User
        // however. But first we'll make sure that this provides the expected result with just one User.
        assertEquals(userHash.size(), 1);
        
        final double expResult = Math.sqrt(1.5);
        double result = rmseInstance.evaluate(userHash, k);
        assertEquals(expResult, result, ERROR_DELTA);
        
        // Now we can add a second User and test again!
        
        
        // Get Movies from the database
        DBManager dbmgr = DBManager.getInstance(); // Make sure it's initialized
        
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
        
        // user2's training Movies
        ArrayList<Rating> user2Training = new ArrayList<>();
        user2Training.add(new Rating(movie1,4,1234));
        user2Training.add(new Rating(movie2,3,1235));
        user2Training.add(new Rating(movie3,3,1236));
        user2Training.add(new Rating(movie4,4,1237));
        
        // user2's test Movies
        ArrayList<Rating> user2Test = new ArrayList<>();
        user2Test.add(new Rating(movie5,2,1238));
        user2Test.add(new Rating(movie6,4,1239));
        user2Test.add(new Rating(movie7,5,1240));
        user2Test.add(new Rating(movie8,4,1241));
        
        // Create the new User
        User user2 = new User(2,"M",25,7,83301,user2Training,user2Test);
        
        // user2's Predictions
        LinkedList<Prediction> user2Preds = new LinkedList<>();
        user2Preds.add(new Prediction(movie5,3));
        user2Preds.add(new Prediction(movie6,4));
        user2Preds.add(new Prediction(movie7,2));
        user2Preds.add(new Prediction(movie8,5));
        
        userHash.put(user2,user2Preds);
        
        final double expResult2 = Math.sqrt(2.75);
        double actualResult2 = rmseInstance.evaluate(user2, user2Preds, k);
        assertEquals(expResult2, actualResult2, ERROR_DELTA);
        
        final double expResult3 = 1.442; // ( sqrt(1.5) + sqrt(2.75)) / 2 = 1.442
        double actualResult3 = rmseInstance.evaluate(userHash, k);
        assertEquals(expResult3,actualResult3, ERROR_DELTA);
        
       // Now test with a different k value
       
       final double expResult4 = 0.854; // ( sqrt(0.5) + sqrt(1.0) ) /2 = 0.854
       double actualResult4 = rmseInstance.evaluate(userHash,2);
       assertEquals(expResult4, actualResult4, ERROR_DELTA);
       
       // Now test with a k-value which is greater than the number of Predictions. 
       // This should give zero, because none of the users have 8 items on their top-k list
       
       final double expResult5 = 0.0;
       double actualResult5 = rmseInstance.evaluate(userHash,8);
       assertEquals(expResult5, actualResult5, ERROR_DELTA);
       
       // Next make sure that k=0 is handled properly. We should get 0 as a result
       
       final double expResult6 = 0.0;
       double actualResult6 = rmseInstance.evaluate(userHash,0);
       assertEquals(expResult6, actualResult6, ERROR_DELTA);
       
       // Make sure we also get 0 when we are working with an empty HashMap.
       userHash = new HashMap<>(); // Wipe the HashMap
       final double expResult7 = 0.0;
       double actualResult7 = rmseInstance.evaluate(userHash,k);
       assertEquals(expResult7, actualResult7, ERROR_DELTA);
       
       // Lastly, make sure that, if the userHash is empty and k = 0 it doesn't cause some bug.
       final double expResult8 = 0.0;
       double actualResult8 = rmseInstance.evaluate(userHash,0);
       assertEquals(expResult8,actualResult8, ERROR_DELTA);
       
       
    }
    
}
