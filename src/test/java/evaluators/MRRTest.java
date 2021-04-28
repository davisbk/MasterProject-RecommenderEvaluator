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
 * This class tests the MRR evaluator class. 
 * 
 * @author Brian Davis
 */
public class MRRTest {
    public static HashMap<Integer, Movie> movies;
    final static double ERROR_DELTA = 0.001;
    
    public MRRTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        Settings.loadNewSetting("test_settings.cfg");
        movies = DBManager.getMovies();
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
     * Test of evaluate method, of class MRR.
     */
    @Test
    public void testEvaluateUser() {
        System.out.println("MRRTest.evaluate(User usr, LinkedList<Prediction> predictionList, int k)");
        User usr = null;
        LinkedList<Prediction> predictionList = null;
        final int k = 2;
        MRR mrrInstance = new MRR();
        
        // Set up the User. We need Movies in their training and test sets.
        
        // Set up some Movies for the User
        Movie movie1 = movies.get(1357); // Rating 5
        Movie movie2 = movies.get(3068); // Rating 4
        Movie movie3 = movies.get(647); // Rating 3
        Movie movie4 = movies.get(2194); // Rating 4
        Movie movie5 = movies.get(648); // Rating 4
        
        ArrayList<Rating> trainingSet = new ArrayList<>();
        trainingSet.add(new Rating(movie1,5,12345));
        trainingSet.add(new Rating(movie2,4,12346));
        trainingSet.add(new Rating(movie3,3,12347));
        
        ArrayList<Rating> testSet = new ArrayList<>();
        testSet.add(new Rating(movie4,1,12348));
        testSet.add(new Rating(movie5,1,12349));
        
        //     public User(int id, String gender, int age_range, int occupation, int zipCode, ArrayList<Rating> ratings, ArrayList<Rating> testRatings)  {

        usr = new User(2,"M",25,3,83301,trainingSet,testSet);
        
        // Now create the List of Predictions
        predictionList = new LinkedList<>();
        predictionList.add(new Prediction(movie4,3));
        predictionList.add(new Prediction(movie5,1));
        
        double expResult1 = 0.0; // With no relevant movies ( rating >= 4, the userAverage) the result should be zero.
        double actualResult1 = mrrInstance.evaluate(usr, predictionList, k);
        assertEquals(expResult1, actualResult1, ERROR_DELTA);
        
        // Now create a new predictionList and put a relevant movie in spot 1
        predictionList = new LinkedList<>();
        predictionList.add(new Prediction(movie4,4));
        predictionList.add(new Prediction(movie5,1));
        
        // Fix the test set from before
        testSet = new ArrayList<>();
        testSet.add(new Rating(movie4,5,12348));
        testSet.add(new Rating(movie5,2,12349));
        usr = new User(2,"M",25,3,83301,trainingSet,testSet);
        
        
        double expResult2 = 1.0; // Since the first relevant movie is in spot 1, it should be 1.0
        double actualResult2 = mrrInstance.evaluate(usr, predictionList, k);
        assertEquals(expResult2, actualResult2, ERROR_DELTA);
        
        // Create a new predictionList and put the relevant movie in spot 2
        predictionList = new LinkedList<>();
        predictionList.add(new Prediction(movie4,5));
        predictionList.add(new Prediction(movie5,4));
        
        testSet = new ArrayList<>();
        testSet.add(new Rating(movie4,2,12348));
        testSet.add(new Rating(movie5,4,12349));
        usr = new User(2,"M",25,3,83301,trainingSet,testSet);
        
        double expResult3 = 0.5; // If the first relevant result is in position 2/2, MRR should be 0.5
        double actualResult3 = mrrInstance.evaluate(usr,predictionList, k);
        assertEquals(expResult3, actualResult3, ERROR_DELTA);
        
        // Lastly make sure that method handles an empty List of Predictions correctly (i.e. returns 0)
        predictionList = new LinkedList<>();
        
        double expResult4 = 0.0;
        double actualResult4 = mrrInstance.evaluate(usr, predictionList, k);
        assertEquals(expResult4, actualResult4, ERROR_DELTA);
       
    }

    /**
     * Test of evaluate method, of class MRR.
     */
    @Test
    public void testEvaluateHashMap() {
        System.out.println("MRRTest.evaluate(HashMap<User, LinkedList<Prediction>> userHash, int k)");
        HashMap<User, LinkedList<Prediction>> userHash = null;
        User user1 = null;
        User user2 = null;
        final int k = 2;
        MRR instance = new MRR();
        
        // Create our Users with their respective Movies
        
        // Create Movies for user1
        Movie movie1 = movies.get(1357); // Rating 5
        Movie movie2 = movies.get(3068); // Rating 4
        Movie movie3 = movies.get(647); // Rating 3
        Movie movie4 = movies.get(2194); // Rating 4
        Movie movie5 = movies.get(648); // Rating 4
        
        // Create Movies for user2
        Movie movie6 = movies.get(1641); // Rating 2
        Movie movie7 = movies.get(648); // Rating 3
        Movie movie8 = movies.get(1394); // Rating 4
        Movie movie9 = movies.get(3534); // Rating 3
        Movie movie10 = movies.get(104); // Rating 4
        
        // Create training set for user1 (average = 4)
        ArrayList<Rating> trainingSet1 = new ArrayList<>();
        trainingSet1.add(new Rating(movie1,5,12345));
        trainingSet1.add(new Rating(movie2,4,12346));
        trainingSet1.add(new Rating(movie3,3,12347));
        
        // Create test set for user1
        ArrayList<Rating> testSet1 = new ArrayList<>();
        testSet1.add(new Rating(movie4,4,12348));
        testSet1.add(new Rating(movie5,4,12349));
        
        // Create training set for user2 (average 3)
        ArrayList<Rating> trainingSet2 = new ArrayList<>();
        trainingSet2.add(new Rating(movie6, 2,12350));
        trainingSet2.add(new Rating(movie7, 3,12351));
        trainingSet2.add(new Rating(movie8, 4,12352));
        
        // Create test set for user2
        ArrayList<Rating> testSet2 = new ArrayList<>();
        testSet2.add(new Rating(movie9, 3, 12353));
        testSet2.add(new Rating(movie10, 4, 12354));
        
        // Create our two Users with their respective training and test sets
        user1 = new User(1, "M", 20, 25, 83301, trainingSet1, testSet1);
        user2 = new User(2, "F", 20, 25, 83301, trainingSet2, testSet2);
        
        // Create Predictions for user1
        LinkedList<Prediction> predList1 = new LinkedList<>();
        predList1.add(new Prediction(movie4,4)); // right
        predList1.add(new Prediction(movie5,4)); // right
        
        // Create Predictions for user2
        LinkedList<Prediction> predList2 = new LinkedList<>();
        predList2.add(new Prediction(movie10, 4)); // right
        predList2.add(new Prediction(movie9,2)); // wrong
        
        
        // Add our users and their Predictions to our HashMap
        userHash = new HashMap<>();
        userHash.put(user1, predList1);
        userHash.put(user2, predList2);
        
        double expResult1 = 1.0; // MRR(user1) = 1, MRR(user2)= 1.0. So 2.0 / 2 = 1.0
        double result1 = instance.evaluate(userHash, k);
        assertEquals(expResult1, result1, ERROR_DELTA);
        
        // Make sure the method handles it properly when our list of Predictions is shorter than the desired k-value.
        // Since none of the Users have 3 or more predictions, this should return zero.
        double expResult2 = 0.0; // Should still be the same result as before since we're only changing k
        double result2 = instance.evaluate(userHash,3);
        assertEquals(expResult2, result2, ERROR_DELTA);
        
        // Make sure that if k is less than the length of the Predictions, Movies which are further down the list than this are not counted.
        // Run the evaluation again but with k=1, meaning that the Predictions for user2 should 
        // return an MRR of 0. And thus ( MRR(user1) + MRR(user2) ) / 2 = (1.0 + 0.0 ) / 2 = 0.5
        double expResult3 = 1.0;
        double result3 = instance.evaluate(userHash,1);
        assertEquals(expResult3, result3, ERROR_DELTA);
        
    }    
}
