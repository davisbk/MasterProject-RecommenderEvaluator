package datastructures;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import utils.Printer;

/**
 * This class tests the Property class
 * 
 * @author Brian Davis
 */
public class PropertyTest {
    private static Property prop1,prop2,prop3;        // Properties objects to use to test the class
    final double ERROR_DELTA = 0.001;
    /**
     *
     */
    @BeforeClass
    public static void setUpClass() {
        Printer printer = Printer.getConsolePrinterInstance();
        prop1 = new Property("genre","action");
        prop2 = new Property("genre","comedy"); 
        prop3 = new Property("genre","comedy");
    }
    
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
        System.out.println("PropertyTest.equals");
        Assert.assertEquals(new Property("genre","action") ,  new Property("genre","action"));
        Assert.assertNotEquals(new Property("genre","comedy") ,  new Property("genre","action"));
    }

    
    /**
     * Test of getPropCatLabel method, of class Property.
     */
    @Test
    public void testGetPropCatLabel() {
        System.out.println("PropertyTest.getPropCatLabel");
        Property instance = new Property("genre","action"); 
        String expResult = "genre";
        String result = instance.getPropCatLabel();
        assertEquals(expResult, result);
    }

    /**
     * Test of getPropLabel method, of class Property.
     */
    @Test
    public void testGetPropLabel() {
        System.out.println("PropertyTest.getPropLabel");
        Property instance = new Property("genre","action"); 
        String expResult = "action";
        String result = instance.getPropLabel();
        assertEquals(expResult, result);
    }

    /**
     * Test of toString method, of class Property.
     */
    @Test
    public void testToString() {
        System.out.println("PropertyTest.toString");
        Property instance = new Property("genre","comedy"); 
        StringBuilder strBlder = new StringBuilder();
        
        String NEW_LINE = System.getProperty("line.separator");        
        strBlder.append("datastructures.Property").append(" {").append(NEW_LINE);
        strBlder.append(" Property: ").append("genre").append(NEW_LINE);
        strBlder.append(" PropertyName: ").append("comedy").append(NEW_LINE);        
        strBlder.append("}");
        
        String expResult = strBlder.toString();
        String actualResult = instance.toString();
        
        assertEquals(expResult, actualResult);
        
    }

    /**
     * Test of hashCode method, of class Property.
     */
    @Test
    public void testHashCode() {
        System.out.println("PropertyTest.hashCode");
        Property instance = new Property("genre","comedy"); 
        Property instance2 = new Property("genre","comedy");     
        Assert.assertTrue(instance.equals(instance2) && instance2.equals(instance));
        Assert.assertTrue(instance2.hashCode() == instance.hashCode());
        
    }

        
}
