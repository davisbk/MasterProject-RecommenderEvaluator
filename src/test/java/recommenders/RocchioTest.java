package recommenders;


import datastructures.Movie;
import datastructures.Prediction;
import datastructures.PropertiesHash;
import datastructures.Property;
import datastructures.Rating;
import datastructures.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import utils.DBManager;
import utils.Settings;

/**
 * This class tests the Rocchio recommender class. 
 * 
 * @author Juan David Mendez
 */
public class RocchioTest {
    
    User user1;
    Movie movie4;
    Movie movie5;
    private static final double ERROR_DELTA = 0.0001;
    private static HashMap<Integer, User> users;
    private static HashMap<Integer, Movie> movies;
    /**
     *
     */
    public RocchioTest() {
        Settings.loadNewSetting("test_settings.cfg");
        users = new HashMap<>();
        movies = new HashMap<>();
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
        movie4 = new Movie(4, "url4", "Superman", properties4);
        movie5 = new Movie(5, "url5", "Superman 2", properties5);
        ArrayList<Rating> ratings1 = new ArrayList<>();
        ratings1.add(new Rating(movie1, 3, 213));
        ratings1.add(new Rating(movie2, 1, 1231));
        ratings1.add(new Rating(movie3, 5, 21312));
        ArrayList<Rating> user1TestRatings = new ArrayList<>();  
        user1TestRatings.add(new Rating(movie4, 2, 123213));
        user1TestRatings.add(new Rating(movie5, 4, 1232133));
        user1 = new User(1, "male", 1, 1, 1, ratings1, user1TestRatings);
        users.put(1, user1);
        movies.put(1, movie1);
        movies.put(2, movie2);
        movies.put(3, movie3);
        movies.put(4, movie4);
        movies.put(5, movie5);
        
        DBManager.setMovies(movies);
        
    }
    
    /**
     *
     */
    @BeforeClass
    public static void setUpClass() {
        
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
     * Test of predict method, of class Rocchio.
     */
    @Test
    public void testPredict() {
       
        System.out.println("RocchioTest.predict");
        Settings.setAlpha(1.0);
        Settings.setBeta(1.0);
        Settings.setGamma(1.0);
        
        assertTrue(user1 != null);
        
        HashMap<Integer, User> userHash = new HashMap<>();
        userHash.put(user1.getId(), user1);
        
        LinkedList<Prediction> expPredictions = new LinkedList<>();
        expPredictions.add(new Prediction(movie4, 0.0)); 
        expPredictions.add(new Prediction(movie5, 0.0)); 
        HashMap<User, LinkedList<Prediction>> expResult = new HashMap<>();
        expResult.put(user1, expPredictions);
        Rocchio instance = new Rocchio(userHash);
        HashMap<User, LinkedList<Prediction>> result = instance.predict(users);
        
        assertTrue(result.size() == 1); // One User
        assertTrue(result.get(user1).size() == 2); // Two Predictions
        
        assertEquals(expPredictions.get(0).getValue(), result.get(user1).get(0).getValue(), ERROR_DELTA);
        assertEquals(expPredictions.get(1).getValue(), result.get(user1).get(1).getValue(), ERROR_DELTA);
        
    }

    /**
     * Test of predictMovie method, of class Rocchio.
     */
    @Test
    public void testPredictMovie() {
        System.out.println("RocchioTest.predictMovie");
        User user = user1;
        Movie movie = movie4;
        
        Settings.setAlpha(1.0);
        Settings.setBeta(1.0);
        Settings.setGamma(1.0);
        assertTrue(movie != null);
        assertTrue(user != null);
        
        HashMap<Integer, User> userHash = new HashMap<>();
        userHash.put(user.getId(),user);
        
        Rocchio instance = new Rocchio(userHash);
        double expResult = 0.0; // 
        double result = instance.predictMovie(user, movie);
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of getUser method, of class Rocchio.
     */
    @Test
    public void testGetUser() {
        System.out.println("RocchioTest.getUser");
        int userID = user1.getId();
        HashMap<Integer, User> userHash = new HashMap<>();
        userHash.put(user1.getId(),user1);
        Rocchio instance = new Rocchio(userHash);
        User expResult = user1;
        User result = instance.getUser(userID);
        assertEquals(expResult, result);
        
    }

    /**
     * Test of getRocchioUserProfile method, of class Rocchio.
     */
    @Test
    public void testGetRocchioUserProfile() {
        System.out.println("RocchioTest.getRocchioUserProfile");
        User user = user1;
        HashMap<Integer, User> userHash = new HashMap<>();
        userHash.put(user1.getId(), user1);
        Rocchio instance = new Rocchio(userHash);
        
        PropertiesHash expResult = new PropertiesHash();
        expResult.add(new Property("genre","comedy"), -0.4045);
        expResult.add(new Property("country", "united states"), 0.1348);
        expResult.add(new Property("director", "john c. walsh"), -0.4045);
        expResult.add(new Property("country", "italy"), 0.1348);
        expResult.add(new Property("director", "john hough"), 0.1348);
        expResult.add(new Property("genre", "boo"), 0.1348);
        expResult.add(new Property("genre", "french"), 0.1348);
        expResult.add(new Property("genre", "horror"), -0.5394);
        expResult.add(new Property("country", "france"), -0.5394);
        
        PropertiesHash actualResult = instance.getRocchioUserProfile(user);
        assertEquals(expResult.getSize(), actualResult.getSize());
        
        for(Map.Entry<Property, Double> mapEntry : expResult.getProperties().entrySet()) {
            assertTrue(actualResult.getProperties().containsKey(mapEntry.getKey())); // Our actual result contains all of the same entries
            double expVal = mapEntry.getValue();
            double actualVal = actualResult.getPropValue(mapEntry.getKey());
            assertEquals(expVal, actualVal, ERROR_DELTA);
        }
        
        
    }
}
