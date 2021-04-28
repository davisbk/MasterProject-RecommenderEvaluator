package datastructures;

import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import utils.Printer;
import utils.RatingsSorter;

/**
 * This class tests the Rating class. 
 * 
 * @author Juan David Mendez
 */
public class RatingTest {
    private static Movie movie;
    private static Printer printer;
    private static PropertiesHash movieProp1,movieProp2,movieProp3,movieProp4;
    private static Movie movie1, movie2, movie3, movie4;
    private static Rating rating1, rating2, rating3, rating4;
    
    
    /**
     *
     */
    public RatingTest() {
    }
    
    /**
     *
     */
    @BeforeClass
    public static void setUpClass() {
        printer = Printer.getConsolePrinterInstance();
        
        Property prop1 = new Property("action","genre"); 
        PropertiesHash properties = new PropertiesHash();
        properties.add(prop1,1.0);
        movie = new Movie(1, "testurl", "Test Movie Title", properties);
        
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
        
        // Make some Ratings for these Movies for our single User
        rating1 = new Rating(movie1,4,981897151);
        rating2 = new Rating(movie2,1,982374981);
        rating3 = new Rating(movie3,2,989011922);
        rating4 = new Rating(movie4,4,982831919);
        
        
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
     * Test of getMovie method, of class Rating.
     */
    @Test
    public void testGetMovie() {
        System.out.println("RatingTest.getMovie");
        Rating instance = new Rating(movie, 3, 232231);
        Movie expResult = movie;
        Movie result = instance.getMovie();
        assertEquals(expResult, result);
    }

    /**
     * Test of getRating method, of class Rating.
     */
    @Test
    public void testGetRating() {
        System.out.println("RatingTest.getRating");
        Rating instance = new Rating(movie, 3, 232231);
        int expResult = 3;
        int result = instance.getRating();
        assertEquals(expResult, result);
    }

    /**
     * Test of getRating method, of class Rating.
     */
    @Test
    public void testComparable() {
        System.out.println("RatingTest.comparable");
        
        ArrayList<Rating> test1 = new ArrayList<>();
        test1.add(rating1);
        test1.add(rating2);
        test1.add(rating3);
        test1.add(rating4);
        ArrayList<Rating> test2 = new ArrayList<>();
        test2.add(rating3);
        test2.add(rating4);
        test2.add(rating2);
        test2.add(rating1);
        assertNotEquals(test1, test2);
        test1.sort(new RatingsSorter());
        test2.sort(new RatingsSorter());
        assertEquals(test1, test2);
    }
    
    /**
     * Test of getTimestamp method, of class Rating.
     */
    @Test
    public void testGetTimestamp() {
        System.out.println("RatingTest.getTimestamp");
        Rating instance = new Rating(movie, 3, 232231);
        int expResult = 232231;
        int result = instance.getTimestamp();
        assertEquals(expResult, result);
    }

    /**
     * Test of compareTo method, of class Rating.
     */
    @Test
    public void testCompareTo() {
        System.out.println("RatingTest.compareTo");
        Object t = new Rating(movie, 4, 232231);
        Rating instance = new Rating(movie, 3, 232231);
        int expResult = 1;
        int result = instance.compareTo(t);
        assertEquals(expResult, result);
    }

    /**
     * Test of toString method, of class Rating.
     */
    @Test
    public void testToString() {
        System.out.println("RatingTest.toString");
        
        // Create a Movie object for our Rating
        Movie movie1 = new Movie(123,"http://derp.com","Revenge of the Nerds",new PropertiesHash());
        
        // Create the Rating
        Rating instance = new Rating(movie1,3,12345);
        
        // Build up the expected result
        StringBuilder strBldr = new StringBuilder();
        strBldr.append("datastructures.Rating").append(" {");
        strBldr.append("Movie: ").append(movie1);
        strBldr.append(" Rating: ").append(3);
        strBldr.append(" Timestamp: ").append(12345);
        strBldr.append("}");
        
        String expResult = strBldr.toString();
        String actualResult = instance.toString();
        assertEquals(expResult, actualResult);
        
    }

    /**
     * Test of equals method, of class Rating.
     */
    @Test
    public void testEquals() {
        System.out.println("RatingTest.equals");
        Object obj = new Object();
        
        //     public Movie(int id, String uri, String title, PropertiesHash properties) {

        Movie movie1 = new Movie(1, "uri1", "title1", new PropertiesHash());
        Rating instance = new Rating(movie1,3,90120);
        
        // The new Rating object is not equal to the random Object created before
        final boolean expResult1 = false;
        boolean result1 = instance.equals(obj);
        assertEquals(expResult1, result1);
        
        // The Rating object should be equal to itself
        final boolean expResults2 = true;
        boolean result2 = instance.equals(instance);
        assertEquals(expResults2, result2);
    }

    /**
     * Test of hashCode method, of class Rating.
     */
    @Test
    public void testHashCode() {
        System.out.println("RatingTest.hashCode");          
        Movie movie1 = new Movie(1, "uri1", "title1", new PropertiesHash());
        Rating instance = new Rating(movie1,3,90120);
        Rating instance2 = new Rating(movie1,3,90120);        
        Assert.assertTrue(instance.equals(instance2) && instance2.equals(instance));
        Assert.assertTrue(instance2.hashCode() == instance.hashCode());
    }
    
}