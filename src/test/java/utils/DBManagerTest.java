package utils;

import datastructures.Movie;
import datastructures.PropertiesHash;
import datastructures.Property;
import datastructures.User;
import java.util.HashMap;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This class tests the 
 * @author Brian
 */
public class DBManagerTest {
    
    private DBManager db;
    
    /**
     *
     */
    public DBManagerTest() {
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
        Settings.loadNewSetting("test_settings.cfg");
        db = DBManager.getInstance();
    }
    
    /**
     *
     */
    @After
    public void tearDown() {
    }

    /**
     * Test of getDataWithEnoughRatings method, of class DBManager.
     */
    @Test
    public void testGetDataWithEnoughRatings() {
        System.out.println("DBManagerTest.getData2");
        DBManager instance = DBManager.getInstance();
        DBManager.getMovies().clear(); // To clear out previous tests
        instance.getDataWithEnoughRatings();
        
        assertFalse(instance.getUsers().isEmpty());
        assertFalse(DBManager.movies.isEmpty());
        
        // Movie count per user: user 2 = 119 , user 3 = 47, user 4 = 18
        // but mids: 260, 480, 590, 593, 648, 1196, 1198, 1210, 1259, 1265, 1954, 1968, 2006, 2028, 2858, 3418, 3468
        // were rated by either two or all three of the users and thus were not loaded in multiple times.
        assertEquals(163, DBManager.movies.size());
        
        // Contains Users 2,3,4
        assertTrue(DBManager.getInstance().getUsers().containsKey(2));
        assertTrue(DBManager.getInstance().getUsers().containsKey(3));
        assertTrue(DBManager.getInstance().getUsers().containsKey(4));
        
        // Other variables set up
        assertTrue(instance.numToRetrieve == 0); // We specified Users 2,3,4 in the list and only requested 3 Users total
        assertTrue(DBManager.ratingCounts.isEmpty()); // This is only calculated if we need to find more users
    }

    /**
     * Test of getInstance method, of class DBManager.
     */
    @Test
    public void testGetInstance() {
        System.out.println("DBManagerTest.getInstance");
        DBManager dbmgr1 = DBManager.getInstance();
        DBManager dbmgr2 = DBManager.getInstance();
        
        // Our singleton pattern ensures that these two instances refer to the same object
        assertTrue(dbmgr1 == dbmgr2);
        
        // If we remove the reference and get it again, it should be the same.
        dbmgr1 = null;
        assertTrue(dbmgr2 != null); // We didn't mess up our other reference by making dbmgr1 null (obvious, but worth confirming)
        dbmgr1 = DBManager.getInstance();
        
        assertTrue(dbmgr1 == dbmgr2);
               
        
    }

    /**
     * Test of getUsers method, of class DBManager.
     */
    @Test
    public void testGetUsers() {
        System.out.println("DBManagerTest.getUsers");
        
        // Initialize the DBManager instance
        DBManager instance = DBManager.getInstance();
        instance.getDataWithEnoughRatings();
        // Now get the Users
        HashMap<Integer, User> result = instance.getUsers();
        assertTrue(!result.isEmpty());
        
        // Contains users 2, 3, 4
        assertTrue(result.containsKey(2));
        assertTrue(result.containsKey(3));
        assertTrue(result.containsKey(4));
        
        // The three User objects are not null
        assertTrue(null != result.get(2));
        assertTrue(null != result.get(3));
        assertTrue(null != result.get(4));
    }

    /**
     * Test of getMovies method, of class DBManager.
     */
    @Test
    public void testGetMovies() {
        System.out.println("DBManagerTest.getMovies");
        DBManager dbmgr = DBManager.getInstance();
        DBManager.movies.clear(); // reset from previous tests
        dbmgr.getDataWithEnoughRatings();
        
        HashMap<Integer, Movie> result = DBManager.getMovies();
        
        
        // Movie count per user: user 2 = 119 , user 3 = 47, user 4 = 18
        // but mids: 260, 480, 590, 593, 648, 1196, 1198, 1210, 1259, 1265, 1954, 1968, 2006, 2028, 2858, 3418, 3468
        // were rated by either two or all three of the users and thus were not loaded in multiple times.
        assertEquals(163, result.size());
    }

    /**
     * Test of setMovies method, of class DBManager.
     */
    @Test
    public void testSetMovies() {
        System.out.println("DBManagerTest.setMovies");
        DBManager dbmgr = DBManager.getInstance();
        
        
        assertTrue(!DBManager.getMovies().isEmpty());
        
        // First set DBManager.movies with an empty HashMap to show that it is actually set
        HashMap<Integer, Movie> movies = new HashMap<>();
        DBManager.setMovies(movies);
        
        assertTrue(DBManager.getMovies().isEmpty());
        
        // Now create a Movie to show that it is properly copying everything. 

        // Create a simple Property for the movie
        PropertiesHash moviePropVec1 = new PropertiesHash();
        moviePropVec1.add(new Property("genre","action"), 1.0);
        moviePropVec1.add(new Property("director", "steven spielberg"), 1.0);
        
        // Now create the Movie and add it to our HashMap
        Movie movie1 = new Movie(2,"uri","title",moviePropVec1);
        movies.put(movie1.getId(), movie1);
        
        // Now set it!
        DBManager.setMovies(movies);
        
        // Make sure they are the same
        assertEquals(1, DBManager.getMovies().size());
        assertTrue(DBManager.getMovies().containsKey(2));
        assertTrue(DBManager.getMovies().get(2).equals(movie1));
        
    }
}
