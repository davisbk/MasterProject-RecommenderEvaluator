package recommenders;

import datastructures.Movie;
import datastructures.PropertiesHash;
import datastructures.Property;
import datastructures.Rating;
import datastructures.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import utils.DBManager;
import utils.Printer;
import utils.Settings;


/**
 * This class tests the Rocchio.RocchioUser subclass.
 * 
 * @author Juan David Mendez
 */
public class RocchioUserTest {
    private static User user;
    final static double ERROR_DELTA = 0.001;
    
    /**
     *
     */
    public RocchioUserTest() {
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
        Printer printer = Printer.getConsolePrinterInstance();
        Settings.loadNewSetting("test_settings.cfg");
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
        ArrayList<Rating> ratings1 = new ArrayList<>();
        ratings1.add(new Rating(movie1, 3, 213));
        ratings1.add(new Rating(movie2, 1, 1231));
        ratings1.add(new Rating(movie3, 5, 21312));
        ArrayList<Rating> user1TestRatings = new ArrayList<>();  
        user1TestRatings.add(new Rating(movie4, 2, 123213));
        user1TestRatings.add(new Rating(movie5, 4, 1232133));
        user = new User(1, "male", 1, 1, 1, ratings1, user1TestRatings);
    }
    
    /**
     *
     */
    @After
    public void tearDown() {
    }

    /**
     * Test of getUser method, of class RocchioUser.
     */
    @Test
    public void testGetUser() {
        System.out.println("RocchioUserTest.getUser");
        RocchioUser rocchio = new RocchioUser(user);
        RocchioUser instance = rocchio;
        User expResult = user;
        user.getAvgRating(); // We must do this because otherwise the average rating is not calculated and the default value is used
        User result = instance.getUser();
        assertEquals(expResult, result);
    }

    /**
     * Test of getProfile method, of class RocchioUser.
     */
    @Test
    public void testGetProfile() {
        System.out.println("RocchioUserTest.getProfile");
        
        // We need to set the alpha, beta, and gamma parameters to make sure that
        // the resulting profile values are calculated properly. 
        Settings.setAlpha(1.0);
        Settings.setBeta(1.0);
        Settings.setGamma(1.0);
        
        // Create our RocchioUser (which automatically builds the profile)
        RocchioUser instance = new RocchioUser(user);
        
        PropertiesHash expectedProfileResult = new PropertiesHash();
        expectedProfileResult.add(new Property("country", "italy"), 0.37796);
        expectedProfileResult.add(new Property("director", "john hough"), 0.37796);
        expectedProfileResult.add(new Property("genre", "boo"), 0.37796);
        expectedProfileResult.add(new Property("genre", "french"), 0.37796);
        expectedProfileResult.add(new Property("genre", "comedy"), 0.0);
        expectedProfileResult.add(new Property("country", "united states"), 0.37796);
        expectedProfileResult.add(new Property("director", "john c. walsh"), 0.0);
        expectedProfileResult.add(new Property("genre", "horror"), -0.37796);
        expectedProfileResult.add(new Property("country", "france"), -0.37796);
        
        PropertiesHash result = instance.getProfile();
        
        for(Map.Entry<Property, Double> mapEntry : expectedProfileResult.getProperties().entrySet()) {
            double expResult = mapEntry.getValue();
            double actualResult = result.getPropValue(mapEntry.getKey());
            assertEquals(expResult, actualResult, ERROR_DELTA);
        }
        
        assertEquals(expectedProfileResult.getSize(), result.getSize());
    }

    /**
     * Test of getId method, of class RocchioUser.
     */
    @Test
    public void testGetId() {
        System.out.println("RocchioUserTest.getId");
        RocchioUser instance = new RocchioUser(new User(2, "male", 1, 5, 555, new ArrayList<Rating>(), new ArrayList<Rating>()));
        Integer expResult = 2;
        Integer result = instance.getId();
        assertEquals(expResult, result);
    }

    /**
     * Test of hashCode method, of class RocchioUser.
     */
    @Test
    public void testHashCode() {
        System.out.println("RocchioUserTest.hashCode");
        User instance = new User(1, "M", 3, 4, 83301, new ArrayList<Rating>(), new ArrayList<Rating>());  
        User instance2 = new User(1, "M", 3, 4, 83301, new ArrayList<Rating>(), new ArrayList<Rating>());  
        Assert.assertTrue(instance.equals(instance2) && instance2.equals(instance));
        Assert.assertTrue(instance2.hashCode() == instance.hashCode());
    }

    /**
     * Test of equals method, of class RocchioUser.
     */
    @Test
    public void testEquals() {
        System.out.println("RocchioUserTest.equals");
        
        // Test against some other object
        Object obj = null;
        RocchioUser instance = new RocchioUser(user);
        final boolean expResult = false;
        boolean result = instance.equals(obj);
        assertEquals(expResult, result);
        
        // Test against the same object
        final boolean expResult2 = true;
        boolean result2 = instance.equals(instance);
        assertEquals(expResult2,result2);
        
    }

    /**
     * Test of toString method, of class RocchioUser.
     */
    @Test
    public void testToString() {
        System.out.println("RocchioUserTest.toString");
        
        
        User user1 = new User(user);
        HashMap<Integer, Movie> movieHash = new HashMap<>();
        for(Rating rating : user1.getTrainingRatings()) {
            movieHash.put(rating.getMovie().getId(), rating.getMovie());
        }
        for(Rating rating : user1.getTestRatings()) {
            movieHash.put(rating.getMovie().getId(), rating.getMovie());
        }
        
        DBManager.setMovies(movieHash);
        RocchioUser instance = new RocchioUser(user1);
        
        StringBuilder strBldr = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        strBldr.append("recommenders.RocchioUser").append(" {").append(NEW_LINE);
        strBldr.append(" User: ").append(user).append(NEW_LINE);
        strBldr.append(" RocchioProfile: ").append(instance.getProfile()).append(NEW_LINE);
        strBldr.append("}");
        
        String expResult = strBldr.toString();
        String actualResult = instance.toString();
        
        assertEquals(expResult, actualResult);
        
    }

    /**
     * Test of setRocchioProfile method, of class RocchioUser.
     */
    @Test
    public void testSetRocchioProfile() {
        System.out.println("RocchioUserTest.setRocchioProfile");
        
        // Create a new RocchioUser
        User usr = user;
        recommenders.RocchioUser instance = new recommenders.RocchioUser(usr);
        
        // Create a new profile for the RocchioUser
        PropertiesHash newProfile = new PropertiesHash();
        newProfile.add(new Property("genre", "action"),1.0);
        newProfile.add(new Property("director", "peter jackson"), 1.0);
        
        // Check to make sure that the existing profile is not empty
        assertTrue(instance.getProfile().getSize() != 0);
        
        // Before setting the new profile, make sure that the new profile and the existing profile are not the same
        assertNotEquals(instance.getProfile(), newProfile);
        
        // Now set the profile
        instance.setRocchioProfile(newProfile);
        
        // Now the two should be equal
        assertEquals(newProfile, instance.getProfile());
               
    }
    
}
