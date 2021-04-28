package datastructures;


import java.util.HashMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import utils.Settings;

/**
 * This class tests the RatingRange class.
 * 
 * @author Juan David Mendez
 */
public class RatingRangeTest {
    static HashMap<Integer, User> users;
    static HashMap<Integer, Movie> movies;
    /**
     *
     */
    public RatingRangeTest() {
    }
    
    /**
     *
     */
    @BeforeClass
    public static void setUpClass() {
        Settings.loadNewSetting("test_settings.cfg");
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
     * Test of getRating method, of class RatingRange.
     */
    @Test
    public void testGetRating() {
        System.out.println("RatingRangeTest.getRating");
        RatingRange instance = new RatingRange(3, 2.1, 4.2);
        int expResult = 3;
        int result = instance.getRating();
        assertEquals(expResult, result);
    }

    /**
     * Test of getLowestValue method, of class RatingRange.
     */
    @Test
    public void testGetLowestValue() {
        System.out.println("RatingRangeTest.getLowestValue");
        RatingRange instance = new RatingRange(3, 2.1, 4.2);
        double expResult = 2.1;
        double result = instance.getLowestValue();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of getHighestValue method, of class RatingRange.
     */
    @Test
    public void testGetHighestValue() {
        System.out.println("RatingRangeTest.getHighestValue");
        RatingRange instance = new RatingRange(3, 2.1, 4.2);
        double expResult = 0.0;
        double result = instance.getHighestValue();
        assertEquals(expResult, result, 4.2);
    }

    /**
     * Test of setLowestValue method, of class RatingRange.
     */
    @Test
    public void testSetLowestValue() {
        System.out.println("RatingRangeTest.setLowestValue");
        double lowestValue = 1.0;
        RatingRange instance = new RatingRange(3, 2.1, 4.2);
        instance.setLowestValue(lowestValue);
        assertEquals(lowestValue, instance.lowestValue, 0.0);
    }

    /**
     * Test of setHighestValue method, of class RatingRange.
     */
    @Test
    public void testSetHighestValue() {
        System.out.println("RatingRangeTest.setHighestValue");
        double highestValue = 6.0;
        RatingRange instance = new RatingRange(3, 2.1, 4.2);
        instance.setHighestValue(highestValue);
        assertEquals(highestValue, instance.highestValue, 0.0);
    }

    /**
     * Test of equals method, of class RatingRange.
     */
    @Test
    public void testEquals() {
        System.out.println("RatingRangeTest.equals");
        Object obj = new RatingRange(3, 2.1, 4.2);
        RatingRange instance = new RatingRange(3, 2.1, 4.2);
        boolean expResult = true;
        boolean result = instance.equals(obj);
        assertEquals(expResult, result);
    }    

    /**
     * Test of hashCode method, of class RatingRange.
     */
    @Test
    public void testHashCode() {
        System.out.println("RatingRangeTest.hashCode");        
        RatingRange instance = new RatingRange(3, 2.1, 4.2);
        RatingRange instance2 = new RatingRange(3, 2.1, 4.2);        
        Assert.assertTrue(instance.equals(instance2) && instance2.equals(instance));
        Assert.assertTrue(instance2.hashCode() == instance.hashCode());
    }

    /**
     * Test of toString method, of class RatingRange.
     */
    @Test
    public void testToString() {
        System.out.println("RatingRangeTest.toString");
        
        RatingRange instance = new RatingRange(3,0.1,0.6);
        
        StringBuilder expResult = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");        
        expResult.append("datastructures.RatingRange").append(" {").append(NEW_LINE);
        expResult.append(" rating: ").append(3).append(NEW_LINE);
        expResult.append(" Lowest Value: ").append(0.1).append(NEW_LINE);
        expResult.append(" Highest Value: ").append(0.6).append(NEW_LINE);
        expResult.append("}"); 
        
        String result = instance.toString();
        assertEquals(expResult.toString(), result);
       
    }
}
