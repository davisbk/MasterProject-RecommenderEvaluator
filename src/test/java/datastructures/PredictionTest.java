package datastructures;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import utils.Printer;

/**
 * This class tests the Prediction class
 * 
 * @author Brian Davis
 */
public class PredictionTest {
    private static Movie movie1,movie2;                             // Two Movies
    private static Prediction pred1,pred2;                          // Two Predictions (two make use of the compareTo method)
    private static PropertiesHash propVec1,propVec2;              // Two PropertiesHashes for the two Movies
    private static Property prop1,prop2,prop3,prop4,prop5,prop6;    // Six Property objects for the two Movies (3 each)
    
    /**
     *
     */
    @BeforeClass
    public static void setUpClass() {
        Printer printer = Printer.getFileAndConsoleInstance();
        // Create our properties for the first Movie
        prop1 = new Property("comedy","genre"); 
        prop2 = new Property("tom hanks","starring"); 
        prop3 = new Property("family","genre"); 
        
        // Create the PropertiesHash and add the Properties to it
        propVec1 = new PropertiesHash();
        propVec1.add(prop1,1.0);
        propVec1.add(prop2,1.0);
        propVec1.add(prop3,1.0);
        
        // Create the first Movie
        movie1 = new Movie(1, "http://dbpedia.org/resource/Toy_Story","Toy Story",propVec1);
        pred1 = new Prediction(movie1,3.0);
        
        // Create our properties for the second Movie
        prop4 = new Property("genre","action"); 
        prop5 = new Property("genre","comedy"); 
        prop6 = new Property("starring","Leeroy Jenkins"); 
        
        // Create the PropertiesHash and add the Properties to it
        propVec2 = new PropertiesHash();
        propVec2.add(prop4, 1.0);
        propVec2.add(prop5, 1.0);
        propVec2.add(prop6, 1.0);
        
        // Create the second Movie
        movie2 = new Movie(2,"http://dbpedia.org/resource/Some_Film","Some Film",propVec2);
        pred2 = new Prediction(movie2, 1.0);
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
    public void testGetMovie() {
        System.out.println("PredictionTest.getMovie");
        Assert.assertEquals(pred2.getMovie(), movie2);
    }
    
    /**
     *
     */
    @Test
    public void testGetValue() {
        System.out.println("PredictionTest.getValue");
        Assert.assertEquals(3.0, pred1.getValue(),0.01);
    }
    
    /**
     *
     */
    @Test
    public void testCompareTo() {
        System.out.println("PredictionTest.compareTo");
        Assert.assertTrue(pred1.compareTo(pred2) == -1); // pred1.value > pred2.value
        Assert.assertTrue(pred2.compareTo(pred1) == 1);  // pred2.value < pred1.value
        Assert.assertTrue(pred1.compareTo(pred1) == 0);  // Equal
    }

    /**
     * Test of toString method, of class Prediction.
     */
    @Test
    public void testToString() {
        System.out.println("PredictionTest.toString");
        Prediction instance = pred1;
        String result = instance.toString();  
       
        StringBuilder expResult = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");        
        expResult.append("datastructures.Prediction {").append(NEW_LINE);
        expResult.append(" Movie: ").append("Toy Story").append(NEW_LINE);
        expResult.append(" Value: ").append("3.0").append(NEW_LINE);
        expResult.append("}");  
        assertEquals(expResult.toString(), result);
    }

    /**
     * Test of equals method, of class Prediction.
     */
    @Test
    public void testEquals() {
        System.out.println("PredictionTest.equals");
        Assert.assertFalse(pred1.equals(pred2));
        Assert.assertTrue(pred1.equals(pred1));
    }

    /**
     * Test of hashCode method, of class Prediction.
     */
    @Test
    public void testHashCode() {
        System.out.println("PredictionTest.hashCode");
        Prediction instance = new Prediction(movie1,3.0);
        Prediction instance2 = pred1;
        Assert.assertTrue(instance.equals(instance2) && instance2.equals(instance));
        Assert.assertTrue(instance2.hashCode() == instance.hashCode());
    }
}
