package recommenders;

import datastructures.Movie;
import datastructures.PropertiesHash;
import datastructures.Property;
import datastructures.Rating;
import datastructures.User;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import utils.DBManager;

/**
 * Test file for the MTTData class. 
 * 
 * @author Juan David Mendez
 */
public class MTTDataTest {    
    private final static double ERROR_DELTA = 0.002;
    private static DBManager dbMan;
    private static PropertiesHash movieProp1,movieProp2,movieProp3,movieProp4;
    private static Movie movie1, movie2, movie3, movie4;
    private static Rating rating1, rating2, rating3, rating4;
    private static User user1;
    private static HashMap<Integer,User> users;
    
    public MTTDataTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        dbMan = DBManager.getInstance();
        
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
     * Test of getProfile method, of class MTTData.
     */
    @Test
    public void testGetProfile() {
        System.out.println("getProfile");
        MTTData instance = new MTTData(movieProp1);
        PropertiesHash expResult = movieProp1;
        PropertiesHash result = instance.getProfile();
        assertEquals(expResult, result);
    }

    /**
     * Test of addRelevant method, of class MTTData.
     */
    @Test
    public void testAddRelevant() {
        System.out.println("addRelevant");
        MTTData instance = new MTTData(movieProp1);
        instance.addRelevant();
        assertEquals(movieProp1, instance.getProfile());
        assertEquals(1.0, instance.getPrecision(), ERROR_DELTA);
        assertEquals(2, instance.getRelevant());
        assertEquals(2, instance.getTotal());
    }

    /**
     * Test of addNotRelevant method, of class MTTData.
     */
    @Test
    public void testAddNotRelevant() {
        System.out.println("addNotRelevant");
        MTTData instance = new MTTData(movieProp1);
        instance.addNotRelevant();
        assertEquals(movieProp1, instance.getProfile());
        assertEquals(0.5, instance.getPrecision(), ERROR_DELTA);
        assertEquals(1, instance.getRelevant());
        assertEquals(2, instance.getTotal());
    }

    /**
     * Test of getRelevant method, of class MTTData.
     */
    @Test
    public void testGetRelevant() {
        System.out.println("getRelevant");
        MTTData instance = new MTTData(movieProp1);
        instance.addRelevant();
        assertEquals(movieProp1, instance.getProfile());
        assertEquals(1.0, instance.getPrecision(), ERROR_DELTA);
        assertEquals(2, instance.getRelevant());
        assertEquals(2, instance.getTotal());
    }

    /**
     * Test of getTotal method, of class MTTData.
     */
    @Test
    public void testGetTotal() {
        System.out.println("getTotal");
        MTTData instance = new MTTData(movieProp1);
        int expResult = 1;
        int result = instance.getTotal();
        assertEquals(expResult, result);
        instance.addNotRelevant();
        expResult = 2;
        result = instance.getTotal();
        assertEquals(expResult, result);
        instance.addRelevant();
        expResult = 3;
        result = instance.getTotal();
        assertEquals(expResult, result);
    }

    /**
     * Test of getPrecision method, of class MTTData.
     */
    @Test
    public void testGetPrecision() {
        System.out.println("getPrecision");
        MTTData instance = new MTTData(movieProp1);
        double expResult = 1;
        double result = instance.getPrecision();
        assertEquals(expResult, result, ERROR_DELTA);
        instance.addNotRelevant();
        expResult = 0.5;
        result = instance.getPrecision();
        assertEquals(expResult, result, ERROR_DELTA);
        instance.addRelevant();
        expResult = .666;
        result = instance.getPrecision();
        assertEquals(expResult, result, ERROR_DELTA);
    }

    /**
     * Test of addToProfile method, of class MTTData.
     */
    @Test
    public void testAddToProfile() {
        System.out.println("addToProfile");
        PropertiesHash movie = movieProp1;
        MTTData instance = new MTTData(movieProp1);
        movie.add(movieProp2);
        instance.addToProfile(movie);        
        assertEquals(movie, instance.getProfile());
    }

    /**
     * Test of equals method, of class MTTData.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        Object obj = new MTTData(movieProp4);
        MTTData instance = new MTTData(movieProp4);
        boolean expResult = true;
        boolean result = instance.equals(obj);
        assertEquals(expResult, result);
        instance = new MTTData(movieProp3);
        expResult = false;
        result = instance.equals(obj);
        assertEquals(expResult, result);
    }
}
