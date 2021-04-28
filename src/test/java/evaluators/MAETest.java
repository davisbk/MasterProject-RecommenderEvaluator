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
import utils.Printer;
import utils.Settings;

/**
 * This class tests the MAE evaluator class.
 * 
 * @author Juan David Mendez
 */
public class MAETest {
    private static HashMap<User, LinkedList<Prediction>> list;
    private final double ERROR_DELTA = 0.001;
    
    /**
     *
     */
    public MAETest() {
    }
    
    /**
     *
     */
    @BeforeClass
    public static void setUpClass() {
        Settings.resetSettingsToDefaultValues();
        Printer printer = Printer.getConsolePrinterInstance();
        list = new HashMap<>();
        
        // Set up some Movies for testing
        PropertiesHash properties1 = new PropertiesHash();
        properties1.add(new Property("genre", "comedy"),1.0); 
        properties1.add(new Property("country", "united states"),1.0);
        properties1.add(new Property("director", "john c. walsh"),1.0);
        PropertiesHash properties2 = new PropertiesHash();
        properties2.add(new Property("genre", "comedy"),1.0);
        properties2.add(new Property("genre", "horror"),1.0);
        properties2.add(new Property("director", "john c. walsh"),1.0);        
        properties2.add(new Property("country", "france"),1.0);
        PropertiesHash properties3 = new PropertiesHash();
        properties3.add(new Property("country", "italy"),1.0);
        properties3.add(new Property("director", "john hough"),1.0);
        properties3.add(new Property("genre", "boo"),1.0);
        properties3.add(new Property("genre", "french"),1.0);
        PropertiesHash properties4 = new PropertiesHash();
        properties4.add(new Property("genre", "thriller"),1.0);
        PropertiesHash properties5 = new PropertiesHash();
        properties5.add(new Property("genre", "american"),1.0);
        properties5.add(new Property("genre", "comedy"),1.0);      
        Movie movie1 = new Movie(1, "url1", "Toy Story", properties1);
        Movie movie2 = new Movie(2, "url2", "Toy Story 2", properties2);
        Movie movie3 = new Movie(3, "url3", "Trainspotting", properties3);
        Movie movie4 = new Movie(4, "url4", "Superman", properties4);
        Movie movie5 = new Movie(5, "url5", "Superman 2", properties5);
        ArrayList<Rating> ratings1 = new ArrayList<>();
        ratings1.add(new Rating(movie1, 3, 213));
        ratings1.add(new Rating(movie2, 1, 1231));
        ratings1.add(new Rating(movie3, 5, 21312));
        ArrayList<Rating> user1TestRatings = new ArrayList<>();  
        user1TestRatings.add(new Rating(movie4, 2, 123213));
        user1TestRatings.add(new Rating(movie5, 4, 1232133));
        User user1 = new User(1, "male", 1, 1, 1, ratings1, user1TestRatings);
        LinkedList<Prediction> prediction = new LinkedList<>();
        prediction.add(new Prediction(movie4, 3));
        prediction.add(new Prediction(movie5, 3));
        list.put(user1, prediction);
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
        System.out.println("MAETest.evaluate(User,LinkedList<Prediction>,int)");
        // Make sure we still have just the one User from the SetUp method
        assertTrue(list.size() == 1);
        
        // Get this User and its Predictions
        User tmpUser = (User)list.keySet().toArray()[0];
        LinkedList<Prediction> tmpPreds = new LinkedList<>((LinkedList<Prediction>)list.get(tmpUser));
        
        MAE mae = new MAE();
        final double expResult1 = 1.0;
        double actualResult1 = mae.evaluate(tmpUser, tmpPreds, tmpPreds.size());
        assertEquals(expResult1, actualResult1, ERROR_DELTA);
        
        // We can also run this with just one Prediction, but the result won't change
        final double expResult2 = 1.0;
        double actualResult2 = mae.evaluate(tmpUser, tmpPreds, 1);
        assertEquals(expResult2, actualResult2, ERROR_DELTA);
        
        // Now we can add a couple more Predictions for the User and it should change the result. We'll need
        // to add some new Movies to the test set to do so.
        
        ArrayList<Rating> user1TestRatings = new ArrayList<>(tmpUser.getTestRatings());
        
        Movie movie6 = new Movie(6,"uri6","title6",new PropertiesHash());
        Movie movie7 = new Movie(7,"uri7","title7", new PropertiesHash());
        user1TestRatings.add(new Rating(movie6,1,12345));
        user1TestRatings.add(new Rating(movie7,3,12346));
        
        tmpPreds.add(new Prediction(movie6,5)); // Off by 4
        tmpPreds.add(new Prediction(movie7,5)); // Off by 2
        
        // Now our MAE should be 2.
        //  (1+1+4+2)/ 4 = 2
        tmpUser = new User(tmpUser,tmpUser.getTrainingRatings(),user1TestRatings);
        final double expResult3 = 2.0;
        double actualResult3 = mae.evaluate(tmpUser, tmpPreds, tmpPreds.size());
        assertEquals(expResult3, actualResult3, ERROR_DELTA);
        
        // Now test with a k-value of 0. Should return zero!
        final double expResult4 = 0.0;
        double actualResult4 = mae.evaluate(tmpUser,tmpPreds, 0);
        assertEquals(expResult4, actualResult4, ERROR_DELTA);
        
        // Test with a k-value greater than the number of predictions; should produce same result as k = tmpPreds.size()
        final double expResult5 = 2.0;
        double actualResult5 = mae.evaluate(tmpUser, tmpPreds, 10);
        assertEquals(expResult5, actualResult5, ERROR_DELTA);
        
    }
    /**
     * Test of evaluate method, of class MAE.
     */
    @Test
    public void testEvaluateHashMap() {
        System.out.println("MAETest.evaluate(HashMap<User,LinkedList<Prediction>>,int)");
        MAE instance = new MAE();
        double expResult1 = 1.0;
        double actualResult1 = instance.evaluate(list, 2);
        assertEquals(expResult1, actualResult1, ERROR_DELTA);
        
        // Mske sure that if the evaluate method is called with k being too large for a User, that
        // User is not counted. Since we only have one User, here that will mean the MAE is 0.0.
        double expResult2 = 0.0;
        double actualResult2 = instance.evaluate(list, 3);
        assertEquals(expResult2, actualResult2, ERROR_DELTA);
    }
    
}
