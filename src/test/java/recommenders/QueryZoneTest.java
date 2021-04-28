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
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import utils.DBManager;
import utils.Settings;

/**
 * This class tests the QueryZone recommender class. 
 * 
 * @author Brian Davis
 */
public class QueryZoneTest {
    private final static double ERROR_DELTA = 0.001;
    private static DBManager dbMan;
    private static PropertiesHash movieProp1,movieProp2,movieProp3,movieProp4;
    private static Movie movie1, movie2, movie3, movie4;
    private static Rating rating1, rating2, rating3, rating4;
    private static User user1;
    private static HashMap<Integer,User> users;
   
    /**
     *
     */
    @BeforeClass
    public static void setUpClass() {
        Settings.loadNewSetting("test_settings.cfg");
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
        
        // Now create our User HashMap to be passed to the QueryZone constructor and add the User
        users = new HashMap<>();
        users.put(user1.getId(), user1);
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
     * Test of constructor method
     */
    @Test
    public void testConstructor() {
        System.out.println("QueryZoneTest.QueryZone(HashMap<Integer, User> users)");
        Settings.resetSettingsToDefaultValues();
        // Make sure that Alpha, Beta, and Gamma are the same as expected and weren't changed elsewhere
        assertEquals(Settings.getRocchioParams().get("alpha"), 0.4, ERROR_DELTA);
        assertEquals(Settings.getRocchioParams().get("beta"), 0.2, ERROR_DELTA);
        assertEquals(Settings.getRocchioParams().get("gamma"), 0.8, ERROR_DELTA);
               
        // Set up the QueryZone 
        QueryZone qzrInstance = new QueryZone(users);
        
        // Now to make sure that the QZRUser Profiles were generated properly!
        
        // There is one User
        Assert.assertEquals(1, qzrInstance.qzrUsers.size());
        
        // Get the QZRUser from the QZR and make sure it's the User we expect
        QueryZone.QueryZoneUser qzrUser = qzrInstance.qzrUsers.get(1);
        Assert.assertEquals(qzrUser.getUser(),user1);
        
        // Check the profile. It should be different than any of the Movies alone!
        PropertiesHash actualQZRProfile = qzrUser.getProfile();
        Assert.assertNotEquals(actualQZRProfile,movieProp1.getProperties());
        Assert.assertNotEquals(actualQZRProfile,movieProp2.getProperties());
        Assert.assertNotEquals(actualQZRProfile,movieProp3.getProperties());
        Assert.assertNotEquals(actualQZRProfile,movieProp4.getProperties());
        
        //It should be equal to the following PropertiesHash:
        PropertiesHash expectedQZRProfile = new PropertiesHash();
        expectedQZRProfile.add(new Property("genre","action"), -0.256);
        expectedQZRProfile.add(new Property("director","peter jackson"), 0.256);
        expectedQZRProfile.add(new Property("starring", "elijah wood"), 0.256);
        expectedQZRProfile.add(new Property("starring", "cate blanchett") , 0.128);
        expectedQZRProfile.add(new Property("genre", "comedy"), -0.512);
        expectedQZRProfile.add(new Property("director", "jj abrahams"), -0.512);
        expectedQZRProfile.add(new Property("year", "1994"), -0.512);
                
        // The produced QZR profile and the expected one are the same size
        Assert.assertEquals(expectedQZRProfile.getProperties().size(),actualQZRProfile.getProperties().size());
        
        // All the Properties in the actual profile are found in the expected profile, and their values are the same
        for(Map.Entry<Property,Double> mapEntry : expectedQZRProfile.getProperties().entrySet()) {
            Assert.assertTrue(actualQZRProfile.getProperties().containsKey(mapEntry.getKey()));
            double expectedValue = expectedQZRProfile.getPropValue(mapEntry.getKey());
            double actualValue = actualQZRProfile.getPropValue(mapEntry.getKey());
            Assert.assertEquals(expectedValue, actualValue, ERROR_DELTA); 
            
        }
                
               
    }

    /**
     * Test of predict method, of class QueryZone.
     */
    @Test
    public void testPredict() {
        System.out.println("QueryZoneTest.predict");
        Settings.resetSettingsToDefaultValues();
        QueryZone qzrInstance;
        
        
        
        
        // Run the predictions and verify that they match up with our expectations. First, create
        // the HashMap of Predictions which we expect.
        HashMap<User, LinkedList<Prediction>> expResult = new HashMap<>();
        LinkedList<Prediction> user1ExpPredictions = new LinkedList<>();
        Prediction pred1 = new Prediction(movie4,0.064); 
        user1ExpPredictions.add(pred1);
        expResult.put(user1,user1ExpPredictions);
        
        // Now instantiate the QZR, which builds the profiles. 
        qzrInstance = new QueryZone(users);
        
        // Lastly, run the actual predictions. 
        HashMap<User, LinkedList<Prediction>> user1ActualPredictions = qzrInstance.predict(users);
        
        // Now verify that our predictions are accurate!
        
        // The Recommender only predicted one User (that's all it was given)
        Assert.assertTrue(user1ActualPredictions.containsKey(user1));
        Assert.assertEquals(1, user1ActualPredictions.size());
        
        // The Recommender only has one prediction for this movie, namely movie4 
        LinkedList<Prediction> actualPredictionsList = user1ActualPredictions.get(user1);
        Assert.assertEquals(1, actualPredictionsList.size());
        Assert.assertEquals(movie4, actualPredictionsList.get(0).getMovie()); 
        Assert.assertEquals(pred1.getValue(), actualPredictionsList.get(0).getValue(),ERROR_DELTA);
        
    }

    /**
     * Test of findTopKGenres method, of class QueryZone.
     */
    @Test
    public void testFindTopKGenres() {
        
        System.out.println("QueryZoneTest.findTopKGenres");
       
        PropertiesHash propVec = new PropertiesHash();
        
        ArrayList<String> expResult = new ArrayList<>();
        int k_genres = 2;
        
        Property prop1 = new Property("genre","comedy");
        Property prop2 = new Property("genre","horror");
        Property prop3 = new Property("title","Toy Story");
        Property prop4 = new Property("genre","action");
        
        propVec.add(prop1, 1.0);
        propVec.add(prop2, 2.0);
        propVec.add(prop3, 3.0);
        propVec.add(prop4, 3.0);
        
        // Since k_genres = 2 we should only find the top 2, namely action and horror. Toy Story should not appear at all (not a genre)
        ArrayList<String> result = QueryZone.findTopKGenres(propVec, k_genres);
        
        // First assert that both expected genres are there.
        assertTrue(result.contains("action"));
        assertTrue(result.contains("horror"));
        
        // Next assert that they are in the proper order
        assertEquals(result.indexOf("action"),0);
        assertEquals(result.indexOf("horror"),1);
        
        // And that is all that it contains
        assertEquals(result.size(), 2);
        
        // NOW INCREASE K TO 3
        k_genres = 3;
        
         // If we increase the number of genres to 3, we should now also find "comedy"
        ArrayList<String> result2 = QueryZone.findTopKGenres(propVec, k_genres);
        
        assertTrue(result2.contains("action"));
        assertTrue(result2.contains("horror"));
        assertTrue(result2.contains("comedy"));
        
        // Assert they are also in the correct order
        assertEquals(result2.indexOf("action"),0);
        assertEquals(result2.indexOf("horror"),1);
        assertEquals(result2.indexOf("comedy"),2);
        
        // And that this is all the result contains
        assertEquals(result2.size(),3);
        
    }

    /**
     * Test of predictMovie method, of class QueryZone.
     */
    @Test
    public void testPredictMovie() {
       
        System.out.println("QueryZoneTest.predictMovie");
               
        // Create our QZR instance
        QueryZone qzrInstance = new QueryZone(users);
        
        // Create a new Movie to test against
        
        PropertiesHash testMovieProps = new PropertiesHash();
        testMovieProps.add(new Property("genre","action"), 1.0);
        testMovieProps.add(new Property("starring", "elijah wood"), 1.0);
        testMovieProps.add(new Property("director", "peter jackson"), 1.0);
        Movie testMovie = new Movie(100,"uri","title",testMovieProps);
        
        final double expResult1 = 0.148;
        double actualResult1 = qzrInstance.predictMovie(user1, testMovie);
        assertEquals(expResult1, actualResult1,ERROR_DELTA);
        
        // Now we can add a new Property that the profile vector has and watch the similarity go up!
        testMovieProps.add(new Property("starring","cate blanchett"), 1.0);
        testMovie = new Movie(100, "uri", "title", testMovieProps);
        
        final double expResult2 = 0.192;
        double actualResult2 = qzrInstance.predictMovie(user1, testMovie);
        assertEquals(expResult2, actualResult2, ERROR_DELTA);
        
    }
    
    
}
