package evaluators;

import datastructures.Movie;
import datastructures.Prediction;
import datastructures.PropertiesHash;
import datastructures.Rating;
import datastructures.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import utils.Printer;
import utils.Settings;

/**
 * This class tests the NDCG evaluator class.
 * 
 * @author Juan David Mendez
 */

public class NDCGTest {
    final double ERROR_DELTA = 0.00001;
    
    static Movie movie1,movie2,movie3,movie4,movie5,movie6,movie7,movie8,movie9,movie10;
    static ArrayList<Rating> user1Ratings, user1TestRatings;
    static User user1;
    static LinkedList<Prediction> user1Predictions;
    
    public NDCGTest() {
    }    

    @BeforeClass
    public static void setUpClass() throws Exception {
        Printer printer = Printer.getConsolePrinterInstance();
        Settings.resetSettingsToDefaultValues();
        //Movies
        movie1 = new Movie(1, "testurl", "Test Movie Title 1", new PropertiesHash());
        movie2 = new Movie(2, "testurl", "Test Movie Title 2", new PropertiesHash());
        movie3 = new Movie(3, "testurl", "Test Movie Title 3", new PropertiesHash());
        movie4 = new Movie(4, "testurl", "Test Movie Title 4", new PropertiesHash());
        movie5 = new Movie(5, "testurl", "Test Movie Title 5", new PropertiesHash());
        movie6 = new Movie(6, "testurl", "Test Movie Title 6", new PropertiesHash());
        movie7 = new Movie(7, "testurl", "Test Movie Title 7", new PropertiesHash());
        movie8 = new Movie(8, "testurl", "Test Movie Title 8", new PropertiesHash());
        movie9 = new Movie(9, "testurl", "Test Movie Title 9", new PropertiesHash());
        movie10 = new Movie(10, "testurl", "Test Movie Title 10", new PropertiesHash());
        
        // Add some training ratings for this User
        user1Ratings  = new ArrayList<>();
        user1Ratings.add(new Rating(movie1, 5, 12345));
        user1Ratings.add(new Rating(movie2, 4, 12346));
        user1Ratings.add(new Rating(movie3, 3, 12347));
        user1Ratings.add(new Rating(movie4, 4, 12348));
        user1Ratings.add(new Rating(movie5, 4, 12349));
        
        user1TestRatings = new ArrayList<>();
        
        user1TestRatings.add(new Rating(movie6, 3, 12350));
        user1TestRatings.add(new Rating(movie7, 3, 12351));
        user1TestRatings.add(new Rating(movie8, 5, 12352));
        user1TestRatings.add(new Rating(movie9, 5, 12353));
        user1TestRatings.add(new Rating(movie10, 4, 12354));
        
       user1 = new User(2, "M",20,3,83301,user1Ratings, user1TestRatings);
        
        user1Predictions = new LinkedList<>();
        user1Predictions.add(new Prediction(movie10, 4));
        user1Predictions.add(new Prediction(movie6, 3));
        user1Predictions.add(new Prediction(movie7, 3));
        user1Predictions.add(new Prediction(movie8, 3));
        user1Predictions.add(new Prediction(movie9, 2));
        
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }


    /**
     * Test of evaluate method, of class NDCG.
     */
    @Test
    public void testEvaluate_User() {
        System.out.println("DCGTest.evaluate(User usr, LinkedList<Prediction> userPreds, int k)");
        
        // We will use user1 from the SetUpClass method
        
        //DCG = 1.0 + (1.0 / (log10(4) / log10(2))) + (1.0 / (log10(5) / log10(2))) = 1.9307
       //maxDCG = 1.0 + (1.0 / (log10(2) / log10(2))) + (1.0 / (log10(3) / log10(2))) = 2.6309
       //NDCG = DCG / maxDCG = 1.9307 / 2.6309 = 0.73384
        final int k = 5;
        NDCG instance = new NDCG();
        double expResult = 0.73384;
        double actualResult = instance.evaluate(user1, user1Predictions, k);
        assertEquals(expResult, actualResult, ERROR_DELTA);
        
        
    }

    /**
     * Test of evaluate method, of class NDCG.
     */
    @Test
    public void testEvaluate_HashMap_int() {
        System.out.println("DCGTest.evaluate(HashMap<User, LinkedList<Prediction>> userHash, int k)");
               
        // Create our HashMap for the Users and their Predictions
        HashMap<User, LinkedList<Prediction>> userPredHash = new HashMap<>();
        userPredHash.put(user1, user1Predictions); // Add user1 from the SetUpClass method.
        
        // Create another User to test the HashMap evaluator with multiple users
        
        
        NDCG instance = new NDCG();
        
        // Make sure that before we add another User, our first User is still the same.         
        double expResult1 = 0.73384;
        double actualResult1 = instance.evaluate(user1, user1Predictions, user1Predictions.size());
        assertEquals(expResult1, actualResult1, ERROR_DELTA);
        
        // Now check to make sure that the HashMap evaluator method handles at least one user correctly
        final int k = 5; 
        double expResult2 = 0.73384;
        double actualResult2 = instance.evaluate(userPredHash, k);
        assertEquals(expResult2, actualResult2, ERROR_DELTA);
        
        // Now create a new User with different ordering of the Predictions and we should get a different
        // result for this User. 
        
        // First create test ratings
        ArrayList<Rating> user2TrainingRatings = new ArrayList<>();
        user2TrainingRatings.add(new Rating(movie1, 4, 12340));
        user2TrainingRatings.add(new Rating(movie2, 4, 12341));
        user2TrainingRatings.add(new Rating(movie3, 4, 12342));
        user2TrainingRatings.add(new Rating(movie4, 4, 12343));
        user2TrainingRatings.add(new Rating(movie5, 4, 12344));
        
        // Test Ratings for the new User
        ArrayList<Rating> user2TestRatings = new ArrayList<>();
        user2TestRatings.add(new Rating(movie6, 3, 12350));
        user2TestRatings.add(new Rating(movie7, 3, 12351));
        user2TestRatings.add(new Rating(movie8, 5, 12352));
        user2TestRatings.add(new Rating(movie9, 5, 12353));
        user2TestRatings.add(new Rating(movie10, 4, 12354));
        
        // Predictions for the new User. 
        LinkedList<Prediction> user2Predictions = new LinkedList<>();
        
        user2Predictions.add(new Prediction(movie7, 3.0));
        user2Predictions.add(new Prediction(movie10, 2.0));
        user2Predictions.add(new Prediction(movie8, 1.0));
        user2Predictions.add(new Prediction(movie9, 1.0));
        user2Predictions.add(new Prediction(movie6, 3.0));
        
        User user2 = new User(2,"M",25,3,83301,user2TrainingRatings, user2TestRatings);
                
        // Now we make sure that the NDCG for this User is what we expect. For this user,
        // DCG = 0.0 + (1.0 / (log10(2) / log10(2))) + (1.0 / (log10(3) / log10(2))) + (1.0 / (log10(4) / log10(2))) = 2.1309
        // maxDCG = 1.0 + (1.0 / (log10(2) / log10(2))) + (1.0 / (log10(3) / log10(2))) = 2.6309
        // NDCG = DCG / maxDCG = 2.1309 / 2.6309 = 0.80995
        
        final double expResult3 = 0.80995;
        double actualResult3 = instance.evaluate(user2, user2Predictions, k);
        assertEquals(expResult3, actualResult3, ERROR_DELTA);
        
        
        // Lastly, add the new User to the User HashMap and then the final result should be 
        // the average NDCG of the two Users
        userPredHash.put(user2, user2Predictions);

        double expOverallResult = 0.77189; // (0.73384 + 0.80995) / 2 = 1.5438 / 2 = 0.77189
        double overallActualResult = instance.evaluate(userPredHash,k);        
        assertEquals(expOverallResult, overallActualResult, ERROR_DELTA);
    }
}
