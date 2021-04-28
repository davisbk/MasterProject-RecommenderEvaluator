package datastructures;

import java.util.HashMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import utils.Settings;

/**
 * This class tests the PropertiesHash class.
 * 
 * @author Brian Davis
 */
public class PropertiesHashTest {
    final static double ERROR_DELTA = 0.001;
    
    
    public PropertiesHashTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        Settings.loadNewSetting("test_settings.cfg");
        //dbManager.getDataWithEnoughRatings();
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of add method, of class PropertiesHash.
     */
    @Test
    public void testAdd_Property_double() {
        System.out.println("PropertiesHashTest.add(Property, double)");
        Property prop = new Property("genre", "action");
        double val1 = 1.0;
        PropertiesHash instance = new PropertiesHash();
        
        assertTrue(instance.getSize() == 0);
                
        // Add our first property
        instance.add(prop, val1);
        
        // Should just have one item and its value should be the same as val1
        assertTrue(instance.getSize() == 1);
        assertEquals(1.0, instance.getPropValue(prop), ERROR_DELTA);
        
        // Now we can create a second Property with the same propCatLabel and propLabel and 
        // the values should be added to each other.
        Property prop2 = new Property("genre", "action");
        double val2 = 1.0;
        instance.add(prop2,val2);
        
        assertTrue(instance.getSize() == 1);
        assertEquals(2.0,instance.getPropValue(prop),ERROR_DELTA);
        
        // Now we will add another Property with a different value, and this should
        // not affect the value of the first item
        Property prop3 = new Property("genre", "comedy");
        double val3 = 0.8;
        instance.add(prop3,val3);
        
        assertTrue(instance.getSize() == 2);
        assertEquals(2.0, instance.getPropValue(prop),ERROR_DELTA);
        assertEquals(0.8, instance.getPropValue(prop3),ERROR_DELTA);
        
        // Now we can add a negative value to the vector and it should retain its value
        Property prop4 = new Property("director", "peter jackson");
        double val4 = -0.5;
        instance.add(prop4, val4);
        
        assertTrue(instance.getSize() == 3);
        assertEquals(2.0, instance.getPropValue(prop), ERROR_DELTA);
        assertEquals(0.8, instance.getPropValue(prop3),ERROR_DELTA);
        assertEquals(-0.5, instance.getPropValue(prop4),ERROR_DELTA);
        
        // Finally we should be able to add a negative value to a positive value and have it come out correctly
        Property prop5 = new Property("genre", "comedy");
        double val5 = -0.3;
        instance.add(prop5,val5);
        
        assertTrue(instance.getSize() == 3);
        assertEquals(2.0, instance.getPropValue(prop), ERROR_DELTA);
        assertEquals(0.5, instance.getPropValue(prop3),ERROR_DELTA);
        assertEquals(-0.5, instance.getPropValue(prop4),ERROR_DELTA);
        
        
    }

    /**
     * Test of add method, of class PropertiesHash.
     */
    @Test
    public void testAdd_PropertiesHash() {
        System.out.println("PropertiesHashTest.add(PropertiesHash)");
        
        // Create two PropertiesHash objects to add together
        PropertiesHash rhs = new PropertiesHash();
        PropertiesHash instance = new PropertiesHash();
        
        // Create some Properties
        Property prop1 = new Property("genre","action");
        Property prop2 = new Property("director","peter jackson");
        Property prop3 = new Property("year", "2003");
        Property prop4 = new Property("starring", "elijah wood");
        
        
        // Add some Properties to this instance
        instance.add(prop1,1.0);
        instance.add(prop2,1.0);
        instance.add(prop3,1.0);
        instance.add(prop4, -1.0);
                
        // Add some Properties to the RHS
        rhs.add(prop1,1.0);
        rhs.add(prop2,-0.8);
        rhs.add(prop4, -1.0);
        
        // Now actually add the two PropertiesHash objects
        instance.add(rhs);
        
        assertTrue(instance.getSize() == 4);
        assertEquals(2.0, instance.getPropValue(prop1), ERROR_DELTA); // 1.0 + 1.0 = 2.0
        assertEquals(0.2, instance.getPropValue(prop2), ERROR_DELTA); // 1.0 + (-0.8) = 0.2
        assertEquals(1.0, instance.getPropValue(prop3), ERROR_DELTA); // 1.0 + 0.0 = 1.0
        assertEquals(-2.0, instance.getPropValue(prop4), ERROR_DELTA); // -1.0 + -1.0 = -2.0
                
    }

    /**
     * Test of subtract method, of class PropertiesHash.
     */
    @Test
    public void testSubtract_Property_double() {
        System.out.println("PropertiesHashTest.subtract(Property, double)");
        
        // We'll create a PropertiesHash instance with two Properties, one positive and one negative. Then we'll create two
        // more Properties, one positive and one negative, and subtract these from the Properties of the instance. 
        
        // First create our PropertiesHash with two Properties, one positive and one negative
        Property prop1 = new Property("genre", "action");
        Property prop2 = new Property("genre", "comedy");
        PropertiesHash instance = new PropertiesHash();
        instance.add(prop1, 1.0);
        instance.add(prop2, -1.0);
        
        // Now create our first Property to subtract from the positive value (action).
        Property actionPropToSubtract = new Property("genre", "action");
                
        PropertiesHash copy1 = new PropertiesHash(instance); // make a copy of the instance so that we don't have to keep resetting its values after each subtraction
        copy1.subtract(actionPropToSubtract, 1.0);
        assertEquals(0.0, copy1.getPropValue(actionPropToSubtract), ERROR_DELTA); // 1.0 - 1.0 = 0.0
        
        PropertiesHash copy2 = new PropertiesHash(instance);
        copy2.subtract(actionPropToSubtract, -1.0);
        assertEquals(2.0, copy2.getPropValue(actionPropToSubtract), ERROR_DELTA); // 1.0 - (-1.0) = 2.0
        
        // Now create our second Property to subtract from the negative value (comedy).
        Property comedyPropToSubtract = new Property("genre", "comedy");
        
        PropertiesHash copy3 = new PropertiesHash(instance);
        copy3.subtract(comedyPropToSubtract, 1.0);
        assertEquals(-2.0, copy3.getPropValue(comedyPropToSubtract), ERROR_DELTA); // -1.0 - 1.0 = -2.0
        
        PropertiesHash copy4 = new PropertiesHash(instance);
        copy4.subtract(comedyPropToSubtract, -1.0);
        assertEquals(0.0, copy4.getPropValue(comedyPropToSubtract), ERROR_DELTA); // -1.0 - (-1.0) = 0.0
        
        
    }

    /**
     * Test of subtract method, of class PropertiesHash.
     */
    @Test
    public void testSubtract_PropertiesHash() {
        System.out.println("PropertiesHashTest.subtract(PropertiesHash)");
        PropertiesHash rhs = new PropertiesHash();
        PropertiesHash instance = new PropertiesHash();
        
        instance.add(new Property("genre", "action"), 1.0);
        instance.add(new Property("genre", "comedy"), -1.0);
        instance.add(new Property("starring", "elijah wood"), 0.0);
        instance.add(new Property("starring", "cate blanchett"), 1.0);
        instance.add(new Property("starring", "vigo mortensen"), -1.0);
        
        rhs.add(new Property("genre", "action"), 1.0);
        rhs.add(new Property("genre", "comedy"), -1.0);
        rhs.add(new Property("starring", "elijah wood"), 0.0);
        rhs.add(new Property("starring", "jim carey"), 1.0);
        rhs.add(new Property("starring", "vigo mortensen"), 1.0);
        
        assertEquals(5, instance.getSize());
        
        instance.subtract(rhs);
        
        assertEquals(0.0, instance.getValueByCatAndLabel("genre", "action"),ERROR_DELTA); // 1.0 - 1.0 = 0.0
        assertEquals(0.0, instance.getValueByCatAndLabel("genre", "comedy"),ERROR_DELTA); // -1.0 - (-1.0) = 0.0
        assertEquals(0.0, instance.getValueByCatAndLabel("starring", "elijah wood"),ERROR_DELTA); // 0.0 - 0.0 = 0.0
        assertEquals(1.0, instance.getValueByCatAndLabel("starring", "cate blanchett"), ERROR_DELTA); // no change
        assertEquals(-1.0, instance.getValueByCatAndLabel("starring", "jim carey"), ERROR_DELTA); // Didn't exist before, 0.0 - 1.0 = -1.0
        assertEquals(-2.0, instance.getValueByCatAndLabel("starring", "vigo mortensen"), ERROR_DELTA); // -1.0 - 1.0 = -2.0
        
        assertEquals(6, instance.getSize()); // Original 5 plus one new Property (jim carey)
        
        
    }

    /**
     * Test of multiply method, of class PropertiesHash.
     */
    @Test
    public void testMultiply() {
        System.out.println("PropertiesHashTest.multiply(double)");
        double factor = 0.6;
        PropertiesHash instance = new PropertiesHash();
        
        instance.add(new Property("genre","action"),1.0);
        instance.add(new Property("genre", "comedy"), -1.0);
        instance.add(new Property("starring", "elijah wood"), 0.0);
        instance.multiply(factor);
        
        assertEquals(0.6, instance.getValueByCatAndLabel("genre", "action"), ERROR_DELTA);
        assertEquals(-0.6, instance.getValueByCatAndLabel("genre", "comedy"), ERROR_DELTA);
        assertEquals(0.0, instance.getValueByCatAndLabel("starring", "elijah wood"), ERROR_DELTA);
        
        // Now change the factor to 1.0 and they should all stay the same!
        factor = 1.0;
        instance.multiply(factor);
        assertEquals(0.6, instance.getValueByCatAndLabel("genre", "action"), ERROR_DELTA);
        assertEquals(-0.6, instance.getValueByCatAndLabel("genre", "comedy"), ERROR_DELTA);
        assertEquals(0.0, instance.getValueByCatAndLabel("starring", "elijah wood"), ERROR_DELTA);
        
        // Now change the factor to -1.0 and all they should retain their magnitude but change sign
        factor = -1.0;
        instance.multiply(factor);
        assertEquals(-0.6, instance.getValueByCatAndLabel("genre", "action"), ERROR_DELTA);
        assertEquals(0.6, instance.getValueByCatAndLabel("genre", "comedy"), ERROR_DELTA);
        assertEquals(0.0, instance.getValueByCatAndLabel("starring", "elijah wood"), ERROR_DELTA);
        
        
    }

    /**
     * Test of divide method, of class PropertiesHash.
     */
    @Test
    public void testDivide() {
        System.out.println("PropertiesHashTest.divide(double)");
        double divisor = 1.0;
        PropertiesHash instance = new PropertiesHash();
        
        instance.add(new Property("genre","action"),1.0);
        instance.add(new Property("genre", "comedy"), -1.0);
        instance.add(new Property("starring", "elijah wood"), 0.0);
        
        
        
        // Divide by 1.0, should all stay the same
        instance.divide(divisor);
        assertEquals(1.0, instance.getValueByCatAndLabel("genre", "action"), ERROR_DELTA);
        assertEquals(-1.0, instance.getValueByCatAndLabel("genre", "comedy"), ERROR_DELTA);
        assertEquals(0.0, instance.getValueByCatAndLabel("starring", "elijah wood"), ERROR_DELTA);
        
        // Now divide by 0.3 and they should all increase in magnitude but keep their sign
        divisor = 0.3;
        instance.divide(divisor);
        assertEquals(3.333, instance.getValueByCatAndLabel("genre", "action"), ERROR_DELTA);
        assertEquals(-3.333, instance.getValueByCatAndLabel("genre", "comedy"), ERROR_DELTA);
        assertEquals(0.0, instance.getValueByCatAndLabel("starring", "elijah wood"), ERROR_DELTA);
        
        // Now divide by -0.3 and they should further increase in magnitude but switch their sign
        divisor = -0.3;
        instance.divide(divisor);
        assertEquals(-11.111, instance.getValueByCatAndLabel("genre", "action"), ERROR_DELTA);
        assertEquals(11.111, instance.getValueByCatAndLabel("genre", "comedy"), ERROR_DELTA);
        assertEquals(0.0, instance.getValueByCatAndLabel("starring", "elijah wood"), ERROR_DELTA);
        
        // If we set the divisor to zero, an Exception should be thrown
        divisor = 0.0;
        boolean thrown = false;
        try {
            instance.divide(divisor);
        } catch(RuntimeException e) {
            thrown = true;
        }
        assertTrue(thrown);
        
       
        
        
    }

    /**
     * Test of cosSimilarity method, of class PropertiesHash.
     */
    @Test
    public void testCosSimilarity() {
        System.out.println("PropertiesHashTest.cosSimilarity(PropertiesHash)");
        
        PropertiesHash vect1 = new PropertiesHash();
        PropertiesHash vect2 = new PropertiesHash();
        PropertiesHash vect3 = new PropertiesHash();
        PropertiesHash vect4 = new PropertiesHash();
        PropertiesHash vect5 = new PropertiesHash();
        vect1.add(new Property("genre","action"),1.0);
        vect1.add(new Property("genre", "drama"), 1.0);
        
        vect2.add(new Property("genre","action"),1.0);
        vect2.add(new Property("genre", "drama"), 1.0);
        
        vect3.add(new Property("genre","action"),-1.0);
        vect3.add(new Property("genre","drama"), -1.0);
        
        vect4.add(new Property("genre","action"), 0.0);
        vect4.add(new Property("genre","drama"), 0.0);
        
        vect5.add(new Property("genre","action"), 0.4);
        vect5.add(new Property("genre","drama"), 0.8);
        
        
        
        // vect1 and vect2 are identical, their cosine similarity should be 1
        double expResult1 = 1.0;
        double actualResult1 = vect1.cosSimilarity(vect2);
        assertEquals(expResult1, actualResult1, ERROR_DELTA);
        
        
        // vect1 and vect3 are opposite, their cosine similarity should be -1
        double expResult2 = -1.0;
        double actualResult2 = vect1.cosSimilarity(vect3);
        assertEquals(expResult2, actualResult2, ERROR_DELTA);
        
        // vect1 and vect4 are orthogonal, their cosine similarity should be 0
        double expResult3 = 0.0;
        double actualResult3 = vect1.cosSimilarity(vect4);
        assertEquals(expResult3, actualResult3, ERROR_DELTA);
        
        // vect1 and vect5 are very similar so their cosine similarity should be close to 1.
        // in fact, it is: (0.4+0.8) / (sqrt(0.4^2+0.8^2)* sqrt(2)) = 0.949
        double expResult4 = 0.949;
        double actualResult4 = vect1.cosSimilarity(vect5);
        assertEquals(expResult4, actualResult4, ERROR_DELTA);
        
        // Now if we add more Properties to one of the vectors and redo the calculations we should
        // get different results.
        vect5.add(new Property("genre","romance"), 1.0);
        
        // Since vect1 does not have "romance" as a genre, their cosine similarity should decrease.
        // It does: (0.4 + 0.8 + 0.0) / (sqrt(0.4^2 + 0.8^2 + 1.0^2) * (sqrt(2)) = 0.632
        double expResult5 = 0.632;
        double actualResult5 = vect1.cosSimilarity(vect5);
        assertEquals(expResult5, actualResult5, ERROR_DELTA);
        
        // Now we should test what happens when two empty vectors are tested against each other. The result
        // should be 0.
        PropertiesHash emptyPropVec1 = new PropertiesHash();
        PropertiesHash emptyPropVec2 = new PropertiesHash();
        
        double expResult6 = 0.0;
        double actualResult6 = emptyPropVec1.cosSimilarity(emptyPropVec2);
        assertEquals(expResult6, actualResult6, ERROR_DELTA);
        
        // And finally, when a non-empty vector is tested against an empty vector
        double expResult7 = 0.0;
        double actualResult7 = vect1.cosSimilarity(emptyPropVec1);
        assertEquals(expResult7, actualResult7, ERROR_DELTA);
    }

    /**
     * Test of distance method, of class PropertiesHash.
     */
    @Test
    public void testDistance() {
        System.out.println("PropertiesHashTest.distance(PropertiesHash)");
        // Create two PropertiesHash objects and calculate the distance between them manually. 
        
        // Create the PropertiesHash
        PropertiesHash propHash1 = new PropertiesHash();
        PropertiesHash propHash2 = new PropertiesHash();
        
        // Add some Properties to them
        propHash1.add(new Property("genre", "comedy"), 1.0);
        propHash1.add(new Property("genre", "action"), 1.0);
        propHash1.add(new Property("genre", "romance"), 1.0);
        propHash1.add(new Property("director", "peter jackson"), 1.0);
        propHash1.add(new Property("year", "2001"), 1.0);
        
        propHash2.add(new Property("genre", "comedy"), 1.0);
        propHash2.add(new Property("director", "peter jackson"), 1.0);
        propHash2.add(new Property("year", "1984"), 1.0);
        propHash2.add(new Property("genre", "romance"), 1.0);
                
        final double expResult1 = Math.sqrt(3); // There are basically 3 differences between the two vectors (2x year and action)
        double actualResult1 = propHash1.distance(propHash2);
        assertEquals(expResult1, actualResult1, ERROR_DELTA);
        
        // Distance is symmetric, so dist(a,b) = dist(b,a). Test this!
        final double expResult2 = Math.sqrt(3);
        double actualResult2 = propHash2.distance(propHash1);
        assertEquals(expResult2, actualResult2, ERROR_DELTA);
        
        // Now test it with two empty vectors. Their distance is then zero (though this is not likely to be useful information). 
        PropertiesHash propVec3 = new PropertiesHash();
        PropertiesHash propVec4 = new PropertiesHash();
        
        final double expResult3 = 0.0;
        double actualResult3 = propVec3.distance(propVec4);
        assertEquals(expResult3, actualResult3, ERROR_DELTA);
        
        // Now test with some negative values. In this case it shouldn't affect the result.
        propVec3.add(new Property("genre", "action"), 1.0);
        propVec3.add(new Property("genre", "romance"), -1.0);
        
        propVec4.add(new Property("genre", "action"), 1.0);
      
        final double expResult4 = 1.0;
        double actualResult4 = propVec3.distance(propVec4);
        assertEquals(expResult4, actualResult4, ERROR_DELTA);
        
        // Now test with some additional Properties
        propVec4.add(new Property("genre", "romance"), -1.0);
        
        final double expResult5 = 0.0; // they should now have the same properties!
        double actualResult5 = propVec3.distance(propVec4);
        assertEquals(expResult5, actualResult5, ERROR_DELTA);
        
        // If we add another -1 value to one of the vectors, the distance should go back up to 1
        propVec3.add(new Property("year", "1994"), -1.0);
        
        final double expResult6 = 1.0;
        double actualResult6 = propVec3.distance(propVec4);
        assertEquals(expResult6, actualResult6, ERROR_DELTA);
    }

    /**
     * Test of dotProduct method, of class PropertiesHash.
     */
    @Test
    public void testDotProduct() {
        System.out.println("PropertiesHashTest.dotProduct(PropertiesHash)");
        
        PropertiesHash propHash1 = new PropertiesHash();
        propHash1.add(new Property("genre", "comedy"), 3.0);
        
        // First we can get a dot product of an empty vector (propHash2) with another vector (propHash1). We should get zero.
        PropertiesHash propHash2 = new PropertiesHash();
        double expResult1 = 0.0;
        double actualResult1 = propHash1.dotProduct(propHash2);
        assertEquals(expResult1, actualResult1, ERROR_DELTA);
        
        PropertiesHash propHash3 = new PropertiesHash();
        propHash3.add(new Property("genre", "comedy"), 1.0);
        double expResult2 = 3.0; // 1.0 * 3.0 = 3.0
        double actualResult2 = propHash3.dotProduct(propHash1);
        assertEquals(expResult2, actualResult2, ERROR_DELTA);
        
        // Now we can add some more properties to get some more interesting results.
        propHash1.add(new Property("genre", "action"), 0.6);
        propHash3.add(new Property("genre", "action"), 0.9);
        double expResult3 = 3.54; // 3.0 + (0.6 * 0.9) = 3.0 + 0.54 = 3.54
        double actualResult3 = propHash1.dotProduct(propHash3);
        assertEquals(expResult3, actualResult3, ERROR_DELTA);
        
        // We should also get the same result if we call the operator in the other direction
        double expResult4 = 3.54;
        double actualResult4 = propHash3.dotProduct(propHash1);
        assertEquals(expResult4, actualResult4, ERROR_DELTA);
        
        // If we have negative values, this shouldn't matter, it will simply be added into the total like normal
        propHash1.add(new Property("starring", "vigo mortensen"), 1.0);
        propHash3.add(new Property("starring", "vigo mortensen"), -0.2);
        double expResult5 = 3.34; // 3.54 from before, minus 1.0 * -0.2 = 3.54 - 0.2 = 3.34
        double actualResult5 = propHash1.dotProduct(propHash3);
        assertEquals(expResult5, actualResult5, ERROR_DELTA);
        
        // Two negative values should result in a positive value being added
        propHash1.add(new Property("starring", "elijah wood"), -0.4);
        propHash3.add(new Property("starring", "elijah wood"), -0.9);
        double expResult6 = 3.7; // 3.34 from before, plus -0.4 * -0.9 = 3.34 + (-0.36) = 3.7
        double actualResult6 = propHash1.dotProduct(propHash3);
        assertEquals(expResult6, actualResult6, ERROR_DELTA);
        
        // Lastly, we can call the dot product on the same vector
        double expResult7 = 10.52; // 3^2 + 0.6^2 + 1 + (-0.4)^2
        double actualResult7 = propHash1.dotProduct(propHash1);
        assertEquals(expResult7, actualResult7, ERROR_DELTA);        
        
    }

    /**
     * Test of equals method, of class PropertiesHash.
     */
    @Test
    public void testEquals() {
        System.out.println("PropertiesHashTest.equals");
        Property prop1 = new Property("comedy","genre"); 
        Property prop2 = new Property("comedy","genre");       
        Property prop3 = new Property("comedy","genre");
        PropertiesHash propVec1 = new PropertiesHash();
        propVec1.add(prop1, 3.0);
        propVec1.add(prop2, 1.0);
        PropertiesHash propVec2 = new PropertiesHash();        
        propVec2.add(prop1, 3.0);
        propVec2.add(prop3, 4.0);
        PropertiesHash propVec3 = new PropertiesHash();
        propVec3.add(prop1, 3.0);
        propVec3.add(prop2, 1.0);        
        assertEquals(propVec1, propVec2); 
        assertEquals(propVec2, propVec3);
    }

    /**
     * Test of hashCode method, of class PropertiesHash.
     */
    @Test
    public void testHashCode() {
        System.out.println("PropertiesHashTest.hashCode");
        Property prop1 = new Property("genre", "comedy");  
        Property prop2 = new Property("genre", "comedy");  
        
        PropertiesHash instance1 = new PropertiesHash();
        instance1.add(prop1, 3.0);
        
        PropertiesHash instance2 = new PropertiesHash();
        instance2.add(prop2, 1.0);
        
        // Even though their values in the underlying HashMap are the same, they would be .equal() so
        // they should also have the same hashCode(). 
        assertEquals(instance1.hashCode(), instance2.hashCode());
        
        // Now add a new Property to instance2. They are no longer .equals(), so they should also 
        // no longer have the same hashCode()
        Property prop3 = new Property("genre", "action");
        instance2.add(prop3, 1.0);
        
        assertNotEquals(instance1.hashCode(), instance2.hashCode());
        
        
        
        
    }

    /**
     * Test of getSize method, of class PropertiesHash.
     */
    @Test
    public void testGetSize() {
        System.out.println("PropertiesHashTest.getSize");
        PropertiesHash instance = new PropertiesHash();
        int expResult1 = 0;
        int result1 = instance.getSize();
        assertEquals(expResult1, result1);
        
        instance.add(new Property("genre", "action"), 1.0);
        int expResult2 = 1;
        int result2 = instance.getSize();
        assertEquals(expResult2, result2);
    }

    /**
     * Test of normalize method, of class PropertiesHash.
     */
    @Test
    public void testNormalize() {
        System.out.println("PropertiesHashTest.normalize");
        
        // First we'll test whether normalization of a single value is handled properly
        PropertiesHash propHash1 = new PropertiesHash();
        propHash1.add(new Property("genre", "action"), 0.5);
        
        double expectedValue1 = 1.0;
        
        propHash1.normalize();
        
        assertEquals(expectedValue1, propHash1.getValueByCatAndLabel("genre", "action"),ERROR_DELTA);
        
        propHash1.add(new Property("genre", "comedy"), 0.5);
        
        propHash1.normalize();
        
        
        // action has a value of 1.0 now due to the previous normalization.
        // denom = sqrt((1*1 + 0.5*0.5) = sqrt(1.25) = 1.118, and 1.0/1.118 = 0.8944, 0.5 / 1.118 = 0.4472
        double expectedValue2 = 0.8944; 
        double expectedValue3 = 0.4472;
        
        assertEquals(expectedValue2, propHash1.getValueByCatAndLabel("genre","action"), ERROR_DELTA);
        assertEquals(expectedValue3, propHash1.getValueByCatAndLabel("genre", "comedy"), ERROR_DELTA);
        
        
        // If we have a single negative value, normalizing the vector should give
        // the property a value of -1
        PropertiesHash propVec2 = new PropertiesHash();
        propVec2.add(new Property("genre","action"), -0.5);
        final double expectedValue4 = -1.0;
        
        propVec2.normalize();
        
        assertEquals(expectedValue4, propVec2.getValueByCatAndLabel("genre","action"), ERROR_DELTA);
        
        // Now test how a mixed PropertiesHash is normalized (mixed = has negative and positive values)
        PropertiesHash propVec3 = new PropertiesHash();
        propVec3.add(new Property("genre","comedy"), 1.0);
        propVec3.add(new Property("genre","action"), -5.0);
        propVec3.add(new Property("genre","drama"), 1.0);
        
        propVec3.normalize();
        
        final double expectedValue5 = 0.192;
        final double expectedValue6 = -0.962;
        final double expectedValue7 = 0.192;
        
        assertEquals(expectedValue5, propVec3.getValueByCatAndLabel("genre", "comedy"), ERROR_DELTA);
        assertEquals(expectedValue6, propVec3.getValueByCatAndLabel("genre", "action"), ERROR_DELTA);
        assertEquals(expectedValue7, propVec3.getValueByCatAndLabel("genre", "drama"), ERROR_DELTA);
        
    }
   

    /**
     * Test of getValueByCatAndLabel method, of class PropertiesHash.
     */
    @Test
    public void testGetValueByCatAndLabel() {
        System.out.println("PropertiesHashTest.getValueByCatAndLabel");
        String catLabel = "genre";
        String label = "action";
        PropertiesHash instance = new PropertiesHash();
        
        instance.add(new Property("genre","action"), 1.0);
        
        double expResult = 1.0;
        double result = instance.getValueByCatAndLabel(catLabel, label);
        assertEquals(expResult, result, ERROR_DELTA);
        
        // If we try to get the value of a Property which doesn't exist, NaN is returned
        double result2 = instance.getValueByCatAndLabel("this", "doesn't exist");
        assertEquals(Double.NaN, result2, ERROR_DELTA);
    }

    /**
     * Test of getProperties method, of class PropertiesHash.
     */
    @Test
    public void testGetProperties() {
        System.out.println("PropertiesHashTest.getProperties");
        
        // Populate our instance with some Property/Double pairs
        PropertiesHash instance = new PropertiesHash();
        instance.add(new Property("genre", "action"), 1.0);
        instance.add(new Property("genre", "fantasy"), 1.0);
        instance.add(new Property("starring", "elijah wood"), 1.0);
        instance.add(new Property("director", "peter jackson"), 1.0);
        
        // Now populate our expected result
        HashMap<Property, Double> expResult = new HashMap<>();
        expResult.put(new Property("genre", "action"), 1.0);
        expResult.put(new Property("genre", "fantasy"), 1.0);
        expResult.put(new Property("starring", "elijah wood"), 1.0);
        expResult.put(new Property("director", "peter jackson"), 1.0);
        
        HashMap<Property, Double> result = instance.getProperties();
        assertEquals(expResult, result);
        
        assertEquals(expResult.size(), result.size());
    }

    /**
     * Test of getPropValue method, of class PropertiesHash.
     */
    @Test
    public void testGetPropValue() {
        System.out.println("PropertiesHashTest.getPropValue");
        Property prop = new Property("genre", "action");
        PropertiesHash instance = new PropertiesHash();
        instance.add(new Property("genre", "action"), 1.0);
        double expResult = 1.0;
        double result = instance.getPropValue(prop);
        assertEquals(expResult, result, ERROR_DELTA);
        
        // If we try to retrieve a Property which does not exist, the result should be NaN. 
        Property prop2 = new Property("this", "won't be found");
        double expResult2 = Double.NaN;
        double result2 = instance.getPropValue(prop2);
        assertEquals(expResult2, result2, ERROR_DELTA);
    }

    /**
     * Test of getCriticalProps method, of class PropertiesHash.
     */
    @Test
    public void testGetCriticalProps() {
        System.out.println("PropertiesHashTest.getCriticalProps");
        PropertiesHash properties = new PropertiesHash();
                
        // Add the actual Properties to the PropertiesHash
        properties.add(new Property("genre","action"),1.0);
        properties.add(new Property("year", "2001"), 1.0);
        properties.add(new Property("starring", "elijah wood"), 1.0);
        properties.add(new Property("country", "united states"), 1.0);
        properties.add(new Property("country", "new zealand"), 1.0);
        properties.add(new Property("director", "peter jackson"), 1.0);
        properties.add(new Property("starring", "vigo mortensen"), 1.0);
        
        // Now create our PropertiesHash with the expected result
        PropertiesHash expResult = new PropertiesHash();
        expResult.add(new Property("starring", "vigo mortensen"), 1.0);
        expResult.add(new Property("genre", "action"), 1.0);
        expResult.add(new Property("director", "peter jackson"), 1.0);
        expResult.add(new Property("starring", "elijah wood"), 1.0);
        
        PropertiesHash result = PropertiesHash.getCriticalProps(properties);
        assertEquals(expResult, result);
        
        assertEquals(expResult.getSize(), result.getSize());
        
    }
    
}
