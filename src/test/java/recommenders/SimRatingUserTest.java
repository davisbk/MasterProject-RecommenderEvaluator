package recommenders;

import datastructures.Movie;
import datastructures.PropertiesHash;
import datastructures.Property;
import datastructures.Rating;
import datastructures.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import utils.DBManager;
import utils.Settings;

/**
 * This class tests the SimRating recommender class. 
 * 
 * @author Juan David Mendez
 */
public class SimRatingUserTest {    
    private static User user;
    private final static double ERROR_DELTA = 0.0001;
    private static HashMap<Integer, Movie> movies;
    
    /**
     *
     */
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
        
        ArrayList<Rating> ratings1 = new ArrayList<>();
        ratings1.add(new Rating(movie1, 3, 213));
        ratings1.add(new Rating(movie2, 1, 1231));
        ratings1.add(new Rating(movie3, 5, 21312));
        ArrayList<Rating> user1TestRatings = new ArrayList<>();  
        user1TestRatings.add(new Rating(movie4, 2, 123213));
        user1TestRatings.add(new Rating(movie5, 4, 1232133));
        user = new User(1, "male", 1, 1, 1, ratings1, user1TestRatings);
        movies = new HashMap<>();
        movies.put(movie1.getId(), movie1);
        movies.put(movie2.getId(), movie2);
        movies.put(movie3.getId(), movie3);
        movies.put(movie4.getId(), movie4);
        movies.put(movie5.getId(), movie5);
        
        DBManager.setMovies(movies);
    }

    /**
     * Test of getUser method, of class SimRatingRocchioUser.
     */
    @Test
    public void testGetUser() {
        System.out.println("SimRatingRocchioUserTest.getUser");
        SimUser rocchio = new SimUser(user);
        SimUser instance = rocchio;
        User expResult = user;
        User result = instance.getUser();
        assertEquals(expResult, result);
    }

    /**
     * Test of getProfile method, of class SimRatingRocchioUser.
     */
    @Test
    public void testGetProfile() {
        System.out.println("SimRatingRocchioUserTest.getProfile");
        SimUser instance = new SimUser(user);
        PropertiesHash expResult = new PropertiesHash();
        int relCount = 0;
        int notrelCount = 0;
        int i =0;
        PropertiesHash rel = new PropertiesHash();
        PropertiesHash notrel = new PropertiesHash();
        //for each rating of the user
        for(Rating rating : user.getTrainingRatings())  {
            PropertiesHash propertyList = rating.getMovie().getProperties();
            if(rating.getRating() <= user.getAvgRating()) {
                notrelCount++;
                if(i>4) {
                    propertyList.multiply(expResult.cosSimilarity(propertyList));
                    propertyList.multiply(rating.getRating() - (1 + 5)/2);
                }
                notrel.add(propertyList);
            }
            else{
                relCount++;
                if(i>4) {
                    propertyList.multiply(expResult.cosSimilarity(propertyList));
                    propertyList.multiply(rating.getRating() - (1 + 5)/2);
                }
                rel.add(propertyList);
            }
            i++;
        }
        
        if(relCount>0)
            rel.divide(relCount);
        rel.multiply(Settings.getRocchioParams().get("beta"));
        if(notrelCount>0)
            notrel.divide(notrelCount);
        notrel.multiply(Settings.getRocchioParams().get("gamma"));
        expResult.multiply(Settings.getRocchioParams().get("alpha"));
        expResult.add(rel);
        expResult.subtract(notrel);
        PropertiesHash result = instance.getProfile();
        //System.out.println("PROFILE: " + instance.getProfile());
        for(Map.Entry<Property, Double> mapEntry : expResult.getProperties().entrySet()) {
            assertTrue(result.getProperties().containsKey(mapEntry.getKey())); // each Property in the expected result is in the actual result
            assertEquals(mapEntry.getValue(), result.getPropValue(mapEntry.getKey()), ERROR_DELTA);
        }
                
        assertEquals(expResult.getProperties().size(), result.getProperties().size());
    }

    /**
     * Test of getId method, of class SimRatingRocchioUser.
     */
    @Test
    public void testGetId() {
        System.out.println("SimRatingRocchioUserTest.getId");
        SimUser instance = new SimUser(new User(2, "male", 1, 5, 555, new ArrayList<Rating>(), new ArrayList<Rating>()));
        Integer expResult = 2;
        Integer result = instance.getId();
        assertEquals(expResult, result);
    }    
}
