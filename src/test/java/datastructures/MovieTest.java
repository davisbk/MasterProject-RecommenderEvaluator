package datastructures;

import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import utils.Printer;

/**
 * This class tests the Movie class.
 * @author Brian Davis
 */
public class MovieTest {
    private static Movie testMovie1, testMovie2;                    // The Movies for testing
    private static PropertiesHash propVec1, propVec2;             // The properties of the (respective) Movies
    private static Property prop1,prop2,prop3,prop4,prop5,prop6;    // Properties for the Movies
    
    /**
     *
     */
    @BeforeClass
    public static void setUpClass() {
        Printer printer = Printer.getFileAndConsoleInstance();
        // Set up the Properties for the first Movie
        prop1 = new Property("comedy","genre");
        prop2 = new Property("thriller","genre"); 
        prop4 = new Property("United States", "country");
        
        
        // Create a new PropertiesHash for the first movie, and add the Properties to it
        propVec1 = new PropertiesHash();
        propVec1.add(prop1,1.0);
        propVec1.add(prop2,1.0);
        propVec1.add(prop4,1.0);
        
        // Create the Movie with the previously-created Properties
        testMovie1 = new Movie(1, "http://dbpedia.org/resource/Toy_Story","Toy Story",propVec1); // Here, Toy Story is an American comedy thriller
        
        // Set up the Properties for the second Movie
        prop3 = new Property("horror","genre"); 
        prop5 = new Property("action", "genre");
        prop6 = new Property("France","country");
        
        // Create a new PropertiesHash for the second mvoie, and add the Properties to it
        propVec2 = new PropertiesHash();
        propVec2.add(prop3,1.0);
        propVec2.add(prop5,1.0);
        propVec2.add(prop6,1.0);
        
        // Create the Movie with the previously-created Properties
        testMovie2 = new Movie(2, "http://dbpedia.org/resource/Superman_(film)", "Superman", propVec2); // Here Superman is a French action horror film
    }

    /**
     *
     * @throws Exception
     */
    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     *
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     *
     */
    @Test
    public void testEquals() {
        System.out.println("MovieTest.equals");
        Assert.assertFalse(testMovie1.equals(testMovie2));
        
        // Reflexivity
        Assert.assertTrue(testMovie1.equals(testMovie1));
        
        
        // Transitivity
        Movie movieA = new Movie(testMovie2);
        Movie movieB = new Movie(testMovie2);
        Movie movieC = new Movie(testMovie2);
        Assert.assertTrue(movieA.equals(movieB));
        Assert.assertTrue(movieB.equals(movieC));
        Assert.assertTrue(movieA.equals(movieC));
        
        // Symmetry
        Movie movieX = new Movie(testMovie1);
        Movie movieY = new Movie(testMovie1);
        Assert.assertTrue(movieX.equals(movieY));
        Assert.assertTrue(movieY.equals(movieX));
        
        
        // Checking for null
        Movie nullMovie = null;
        Assert.assertFalse(testMovie1.equals(nullMovie));
        
    }

    /**
     *
     */
    @Test
    public void testGetID() {
        System.out.println("MovieTest.getId");
        Assert.assertTrue(testMovie1.getId() == 1);
    }

    /**
     *
     */
    @Test
    public void testGetProperties() {
        System.out.println("MovieTest.getProperties");
        PropertiesHash movieProps = testMovie1.getProperties();
        
        // Make sure that the original PropertiesHash has all of the properties in the Movie's PropertiesHash
        for(Map.Entry<Property,Double> mapEntry : movieProps.getProperties().entrySet()) {
            Property prop = mapEntry.getKey();
            Assert.assertTrue(propVec1.getProperties().containsKey(prop));
        }
        
        
        // Now check the other way, that all of the Movie's Properties are those from the original PropertiesHash
        for(Map.Entry<Property,Double> mapEntry : propVec1.getProperties().entrySet()) {
            Property prop = mapEntry.getKey();
            Assert.assertTrue(movieProps.getProperties().containsKey(prop));
        }
       
    }

    /**
     *
     */
    @Test
    public void testGetTitle() {
        System.out.println("MovieTest.getTitle");
        Assert.assertEquals("Toy Story", testMovie1.getTitle());
    }

    /**
     *
     */
    @Test
    public void testGetUri() {
        System.out.println("MovieTest.getUri");
        Assert.assertEquals("http://dbpedia.org/resource/Toy_Story", testMovie1.getUri());
    }

    /**
     * Test of getId method, of class Movie.
     */
    @Test
    public void testGetId() {
        System.out.println("MovieTest.getId");
        Movie instance = testMovie1;
        int expResult = 1;
        int result = instance.getId();
        assertEquals(expResult, result);
    }

    /**
     * Test of toString method, of class Movie.
     */
    @Test
    public void testToString() {
        System.out.println("MovieTest.toString");
        Movie instance = testMovie1;
        
        StringBuilder expResult = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");        
        expResult.append("datastructures.Movie {").append(NEW_LINE);
        expResult.append(" ID: ").append("1");
        expResult.append(", Title: ").append("Toy Story");
        expResult.append(", URI: ").append("http://dbpedia.org/resource/Toy_Story");
        expResult.append(", Properties: ").append(propVec1);
        expResult.append("}").append(NEW_LINE);         
        String result = instance.toString();
        assertEquals(expResult.toString(), result);
    }
    
    @Test
    public void testConstructors() {
        Movie aMovie = new Movie(testMovie1); // copy constructor
        Assert.assertFalse(aMovie == testMovie1); // Not just a reference, actually a new Movie
        
        // What happens if PropertiesHash is null? Should return new PropertiesHash
        Movie aMovie2 = new Movie(123,"","",null);
        Assert.assertFalse(aMovie2.getProperties() == null);
        
        
    }

    /**
     * Test of compareTo method, of class Movie.
     */
    @Test
    public void testCompareTo() {
        System.out.println("MovieTest.compareTo");
        Object t = testMovie1;
        Movie instance = testMovie2;
        int expResult = 1;
        int result = instance.compareTo(t);
        assertEquals(expResult, result);
        t = testMovie2;
        instance = testMovie1;
        expResult = -1;
        result = instance.compareTo(t);
        assertEquals(expResult, result);
        t = testMovie1;
        instance = testMovie1;
        expResult = 0;
        result = instance.compareTo(t);
        assertEquals(expResult, result);
    }

    /**
     * Test of hashCode method, of class Movie.
     */
    @Test
    public void testHashCode() {
        System.out.println("MovieTest.hashCode");        
        Movie instance = testMovie1;
        Movie instance2 = new Movie(1, "http://dbpedia.org/resource/Toy_Story","Toy Story",propVec1);
        Assert.assertTrue(instance.equals(instance2) && instance2.equals(instance));
        Assert.assertTrue(instance2.hashCode() == instance.hashCode());
    }
}
