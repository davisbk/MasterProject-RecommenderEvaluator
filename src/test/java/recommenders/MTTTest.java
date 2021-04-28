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
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import utils.DBManager;
import utils.Settings;

/**
 * This class tests the MTT recommender class. 
 * 
 * @author Juan David Mendez
 */
public class MTTTest {
    private final static double ERROR_DELTA = 0.001;
    private static DBManager dbMan;
    private static PropertiesHash movieProp1,movieProp2,movieProp3,movieProp4;
    private static Movie movie1, movie2, movie3, movie4;
    private static Rating rating1, rating2, rating3, rating4;
    private static User user1;
    private static HashMap<Integer,User> users;
    
    @BeforeClass
    public static void setUpClass() {
        dbMan = DBManager.getInstance();
        Settings.setMttDelCatThreshold(.8);
        Settings.setMttNewCatThreshold(.2);
        
        // Make some Movies. The first and the third movies are similar, but the User
        // rates the first one highly and the third one poorly. The fourth movie
        // is similar but will be put in the test set.
        movieProp1 = new PropertiesHash();
        movieProp1.add(new Property("genre","action"), 1.0);
        movieProp1.add(new Property("director","peter jackson"), 1.0);
        movieProp1.add(new Property("starring","elijah wood"), 1.0);
        movieProp1.add(new Property("starring","cate blanchett"), 1.0);
        movie1 = new Movie(1,"http://dbpedia.org/resource/Movie_1","Movie 1",movieProp1);
        
        movieProp2 = new PropertiesHash();
        movieProp2.add(new Property("genre","comedy"), 1.0);
        movieProp2.add(new Property("genre","action"), 1.0);
        movieProp2.add(new Property("director","jj abrahams"), 1.0);
        movieProp2.add(new Property("year","1994"), 1.0);
        movie2 = new Movie(2,"http://dbpedia.org/resource/Movie_2","Movie 2",movieProp2);
        
        movieProp3 = new PropertiesHash();
        movieProp3.add(new Property("genre","action"), 1.0);
        movieProp3.add(new Property("director","peter jackson"), 1.0);
        movieProp3.add(new Property("starring","elijah wood"), 1.0);
        movie3 = new Movie(3,"http://dbpedia.org/resource/Movie_3","Movie 3",movieProp3);
        
        movieProp4 = new PropertiesHash();
        movieProp4.add(new Property("genre","action"), 1.0);
        movieProp4.add(new Property("director","peter jackson"), 1.0);
        movieProp4.add(new Property("starring","robin williams"), 1.0);
        movieProp4.add(new Property("starring","cate blanchett"), 1.0);
        movie4 = new Movie(4,"http://dbpedia.org/resources/Movie_4","Movie 4",movieProp4);
        
        // Add the Movies to DBManager.movies so that the Recommenders can find them
        DBManager.movies.put(movie1.getId(), movie1);
        DBManager.movies.put(movie2.getId(), movie2);
        DBManager.movies.put(movie3.getId(), movie3);
        DBManager.movies.put(movie4.getId(), movie4);
        
        // Make some Ratings for these Movies for our single User
        rating1 = new Rating(movie1,4,981897151);
        rating2 = new Rating(movie2,1,982374981);
        rating3 = new Rating(movie3,2,989011922);
        rating4 = new Rating(movie4,4,982831919);
        
        // Put the training Ratings into the required ArrayList
        ArrayList<Rating> ratings = new ArrayList<>();
        ratings.add(rating1);
        ratings.add(rating2);
        ratings.add(rating3);
        
        // And put the test Rating into the required LinkedList
        ArrayList<Rating> testRatings = new ArrayList<>();
        testRatings.add(rating4);

        // Create our User object
        user1 = new User(1,"female",3,2,83301,ratings,testRatings);
        
        // Now create our User HashMap to be passed to the QueryZoneRecommender constructor and add the User
        users = new HashMap<>();
        users.put(user1.getId(), user1);
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
     * Test of predict method, of class MultipleTopicTracker.
     */
    @Test
    public void testPredict() {
        System.out.println("MTTTest.predict");
        MultipleTopicTracker instance = new MultipleTopicTracker();
        HashMap<User, LinkedList<Prediction>> expResult = new HashMap<>();
        LinkedList<Prediction> expPrediction = new LinkedList<>();
        Prediction expPred = new Prediction(movie4, movie3.getProperties().cosSimilarity(movie4.getProperties()));
        expPrediction.add(expPred);
        expResult.put(user1, expPrediction);
        HashMap<User, LinkedList<Prediction>> result = instance.predict(users);
        assertEquals(expResult, result);
    }

    /**
     * Test of predictMovie method, of class MultipleTopicTracker.
     */
    @Test
    public void testPredictMovie() {
        System.out.println("MTTTest.predictMovie");
        User user = user1;
        Movie movie = movie4;
        MultipleTopicTracker instance = new MultipleTopicTracker();
        double expResult = movie3.getProperties().cosSimilarity(movie4.getProperties());
        double result = instance.predictMovie(user, movie);
        assertEquals(expResult, result, ERROR_DELTA);
    }
    
}
