package recommenders;

import datastructures.Movie;
import datastructures.PropertiesHash;
import datastructures.Property;
import datastructures.Rating;
import datastructures.User;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import utils.Settings;

/**
 * This class tests the ShortLongUser class. 
 * 
 * @author Brian Davis
 */

public class ShortLongUserTest {    
    private static User user;
    private final static double ERROR_DELTA = 0.0001;
    private static HashMap<Integer, Movie> movies;
    
    public ShortLongUserTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        Settings.loadNewSetting("test_settings.cfg");
        Settings.resetSettingsToDefaultValues();
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
        
        ArrayList<Rating> userTrainRatings = new ArrayList<>();
        userTrainRatings.add(new Rating(movie1, 3, 213));
        userTrainRatings.add(new Rating(movie2, 1, 1231));
        userTrainRatings.add(new Rating(movie3, 5, 21312));
        ArrayList<Rating> user1TestRatings = new ArrayList<>();  
        user1TestRatings.add(new Rating(movie4, 2, 123213));
        user1TestRatings.add(new Rating(movie5, 4, 1232133));
        user = new User(1, "male", 1, 1, 1, userTrainRatings, user1TestRatings);
        movies = new HashMap<>();
        movies.put(movie1.getId(), movie1);
        movies.put(movie2.getId(), movie2);
        movies.put(movie3.getId(), movie3);
        movies.put(movie4.getId(), movie4);
        movies.put(movie5.getId(), movie5);
    }
    
    /**
     * Test of getUser method, of class ShortLongUser.
     */
    @Test
    public void testGetUser() {
        System.out.println("ShortLongUserTest.getUser");
        ShortUser instance = new ShortUser(user);
        User expResult = user;
        User result = instance.getUser();
        assertEquals(expResult, result);
    }

    /**
     * Test of getLongTerm method, of class ShortLongUser.
     */
    @Test
    public void testGetLongTerm() {
        System.out.println("ShortLongUserTest.getLongTerm");
        ShortUser instance = new ShortUser(user);
        PropertiesHash expResult = new PropertiesHash();  
        PropertiesHash properties1 = movies.get(1).getProperties();      
        PropertiesHash properties2 = movies.get(2).getProperties();
        PropertiesHash properties3 = movies.get(3).getProperties();   
        expResult.add(properties1);
        properties2.divide(2);
        expResult.add(properties2);        
        properties3.divide(3);
        expResult.add(properties3);
        PropertiesHash result = instance.getLongTerm();
        System.out.println("expResult: " + expResult);        
        System.out.println("result: " + result);
        assertEquals(expResult, result);
    }

    /**
     * Test of getShortPositive method, of class ShortLongUser.
     */
    @Test
    public void testGetShortPositive() {
        System.out.println("ShortLongUserTest.getShortPositive");
        ShortUser instance = new ShortUser(user);
        PropertiesHash expResult = new PropertiesHash();    
        PropertiesHash properties3 = new PropertiesHash();
        properties3.add(new Property("country", "italy"), 1.0);
        properties3.add(new Property("director", "john hough"), 1.0);
        properties3.add(new Property("genre", "boo"), 1.0);
        properties3.add(new Property("genre", "french"), 1.0);
        expResult.add(properties3);
        expResult.multiply(1-Settings.getRocchioParams().get("alpha"));
        PropertiesHash result = instance.getShortPositive();
        assertEquals(expResult, result);
    }

    /**
     * Test of getShortNegative method, of class ShortLongUser.
     */
    @Test
    public void testGetShortNegative() {
        System.out.println("ShortLongUserTest.getShortNegative");
        ShortUser instance = new ShortUser(user);
        PropertiesHash expResult = new PropertiesHash();
        PropertiesHash properties1 = new PropertiesHash();
        properties1.add(new Property("genre", "comedy"), 1.0);
        properties1.add(new Property("country", "united states"), 1.0);
        properties1.add(new Property("director", "john c. walsh"), 1.0);
        expResult.add(properties1);            
        PropertiesHash properties2 = new PropertiesHash();
        properties2.add(new Property("genre", "comedy"), 1.0);
        properties2.add(new Property("genre", "horror"), 1.0);
        properties2.add(new Property("director", "john c. walsh"), 1.0);        
        properties2.add(new Property("country", "france"), 1.0);
        expResult.add(properties2);        
        expResult.multiply(Settings.getRocchioParams().get("alpha"));
        PropertiesHash result = instance.getShortNegative();
        assertEquals(expResult, result);
    }

    /**
     * Test of getId method, of class ShortLongUser.
     */
    @Test
    public void testGetId() {
        System.out.println("ShortLongUserTest.getId");
        ShortUser instance = new ShortUser(user);
        Integer expResult = 1;
        Integer result = instance.getId();
        assertEquals(expResult, result);
    }
    
}
