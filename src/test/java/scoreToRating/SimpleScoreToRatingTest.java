package scoreToRating;


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
import java.util.Set;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import recommenders.RecommenderInterface;
import recommenders.Rocchio;
import utils.DBManager;
import utils.Printer;

import utils.Settings;

/**
 * This class tests the SimpleScoreToRating class. 
 * 
 * @author Juan David Mendez
 */
public class SimpleScoreToRatingTest {
   
    private static User user1;
    private static Movie movie4;
    private static Movie movie5;
    private static HashMap<Integer, User> users;
    private static HashMap<Integer, Movie> movies;
    
    /**
     *
     */
    public SimpleScoreToRatingTest() {
    }
    
    /**
     *
     */
    @BeforeClass
    public static void setUpClass() {
       
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
        ratings1.add(new Rating(movie2, 3, 1231));
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
    @AfterClass
    public static void tearDownClass() {
    }
    
    /**
     *
     */
    @Before
    public void setUp() {
        Printer printer = Printer.getConsolePrinterInstance();
    }
    
    /**
     *
     */
    @After
    public void tearDown() {
    }

    /**
     * Test of scoreToRating method, of class SimpleScoreToRating.
     */
    @Test
    public void testScoreToRating_RecommenderInterface_HashMap() {
       
        System.out.println("SimpleScoreToRatingTest.scoreToRating");
        RecommenderInterface recommender = new Rocchio(users);
        HashMap<User, LinkedList<Prediction>> prediction = recommender.predict(users);
        SimpleScoreToRating instance = new SimpleScoreToRating();
        HashMap<User, ArrayList<Prediction>> expResult = new HashMap<>();
        ArrayList<Prediction> predictions = new ArrayList<>();
        predictions.add(new Prediction(movie5, 3.0));
        predictions.add(new Prediction(movie4, 3.0));
        expResult.put(user1, predictions);
        HashMap<User, LinkedList<Prediction>> result = instance.scoreToRating(recommender, prediction);
        Set<User> userKeys = result.keySet();
        Set<User> expUserKeys = expResult.keySet();
        assertEquals(userKeys.size(), expUserKeys.size());
        assertEquals(expResult, result);
        
    }

    /**
     * Test of scoreToRating method, of class SimpleScoreToRating.
     */
    @Test
    public void testScoreToRating_3args() {
        
        System.out.println("SimpleScoreToRatingTest.scoreToRating");
        
        RecommenderInterface recommender = new Rocchio(users);
        HashMap<User, LinkedList<Prediction>> userPredictions = recommender.predict(users);
        LinkedList<Prediction> predictionList = new LinkedList<>();
        for(Map.Entry<User, LinkedList<Prediction>> predictions : userPredictions.entrySet())    {
            for(Prediction prediction : predictions.getValue())    {
                predictionList.add(prediction);
            }
        }
        SimpleScoreToRating instance = new SimpleScoreToRating();        
        LinkedList<Prediction> result = instance.scoreToRating(recommender, user1, predictionList);
        ArrayList<Prediction> expResult = new ArrayList<>();
        expResult.add(new Prediction(movie5, 3.0));
        expResult.add(new Prediction(movie4, 3.0));
        assertEquals(expResult, result);
        
    }
        
}
