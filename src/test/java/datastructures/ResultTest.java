package datastructures;

import java.sql.Timestamp;
import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import utils.Printer;

/**
 * This class tests the Result class.
 * 
 * @author Brian Davis
 */
public class ResultTest {
    public static Printer printer;
    public static ArrayList<Result> resultArray;
    private final double ERROR_DELTA = 0.001;
    public ResultTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        printer = Printer.getConsolePrinterInstance();
        resultArray = new ArrayList<>();
        
        /*
         * Set up two Result objects to be used throughout the tests. 
         * The order of the evalautors is: 
         * NDCG, F1, MAE, MAP, MRR, PREC, RMSE, RECALL
         */
        Result result1 = new Result();
        result1.set(Result.EvalType.NDCG, 1.0);
        result1.set(Result.EvalType.F1, 0.8);
        result1.set(Result.EvalType.MAE, 1.75);
        result1.set(Result.EvalType.MAP, 0.8);
        result1.set(Result.EvalType.MRR, 0.75);
        result1.set(Result.EvalType.PREC, 0.8);
        result1.set(Result.EvalType.RMSE, 1.8);
        result1.set(Result.EvalType.RECALL, 0.8);
        Timestamp timestamp1 = new Timestamp((long)12345);
        result1.set(timestamp1);
        
        Result result2 = new Result();
        result2.set(Result.EvalType.NDCG, 0.3);
        result2.set(Result.EvalType.F1, 0.2);
        result2.set(Result.EvalType.MAE, 2.75);
        result2.set(Result.EvalType.MAP, 0.4);
        result2.set(Result.EvalType.MRR, 0.5);
        result2.set(Result.EvalType.PREC, 0.1);
        result2.set(Result.EvalType.RMSE, 2.8);
        result2.set(Result.EvalType.RECALL, 0.1);
        
        
        resultArray.add(result1);
        resultArray.add(result2);
        
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
     * Test of set method, of class Result.
     */
    @Test
    public void testSet_ResultEvalType_double() {
        System.out.println("ResultTest.set(EvalType,double)");
        Result.EvalType eval = null; // we'll test first with a null value; should throw a RuntimeException
        double val = 0.0;
        Result instance = new Result();
                
        boolean runtimeExceptionThrown = false;
        try {
            instance.set(eval, val);
        } catch(RuntimeException e) {
            runtimeExceptionThrown = true;
        }
        assertTrue(runtimeExceptionThrown);
        
        
        // Now change a value for our second Result from the resultArray
        
        // First make sure the value is what we expect before changing
        instance = resultArray.get(1);
        double expectedValue1 = 0.2;
        assertEquals(expectedValue1, instance.getF1(), ERROR_DELTA);
        
        // Now change the value
        eval = Result.EvalType.F1;
        instance.set(eval, 0.8);
        
        double expectedValue2 = 0.8;
        assertEquals(expectedValue2, instance.getF1(), ERROR_DELTA);
    }

    /**
     * Test of set method, of class Result.
     */
    @Test
    public void testSet_Timestamp() {
        System.out.println("ResultTest.set(Timestamp)");
        Timestamp timestamp = new Timestamp(12345);
        Result instance = new Result();
        instance.set(timestamp);
        
        assertTrue(instance.getTimestamp()!= null);
    }

    /**
     * Test of toString method, of class Result.
     */
    @Test
    public void testToString() {
        System.out.println("ResultTest.toString");
        Result instance = resultArray.get(0);
        String expResult = "1.000" + "\t\t" + "1.750" + "\t\t" + "1.800" + "\t\t" +
        "0.800" + "\t\t" + "0.800" + "\t\t" + "0.800" + "\t\t" + "0.800" + "\t\t" +
        "0.750" + "\t\t[" + new Timestamp(12345)+"]";
      
        
        String result = instance.toString();
        assertEquals(expResult, result);
        
    }

    /**
     * Test of getNDCG method, of class Result.
     */
    @Test
    public void testGetDCG() {
        System.out.println("ResultTest.getNDCG");
        Result instance = resultArray.get(0);
        double expResult = 1.0;
        double result = instance.getNDCG();
        assertEquals(expResult, result, 0.0);
        
    }

    /**
     * Test of getF1 method, of class Result.
     */
    @Test
    public void testGetF1() {
        System.out.println("ResultTest.getF1");
        Result instance = resultArray.get(0);
        double expResult = 0.8;
        double result = instance.getF1();
        assertEquals(expResult, result, 0.0);
        
    }

    /**
     * Test of getMAE method, of class Result.
     */
    @Test
    public void testGetMAE() {
        System.out.println("ResultTest.getMAE");
        Result instance = resultArray.get(0);
        double expResult = 1.75;
        double result = instance.getMAE();
        assertEquals(expResult, result, 0.0);
        
    }

    /**
     * Test of getMAP method, of class Result.
     */
    @Test
    public void testGetMAP() {
        System.out.println("ResultTest.getMAP");
        Result instance = resultArray.get(0);
        double expResult = 0.8;
        double result = instance.getMAP();
        assertEquals(expResult, result, 0.0);
        
    }

    /**
     * Test of getMRR method, of class Result.
     */
    @Test
    public void testGetMRR() {
        System.out.println("ResultTest.getMRR");
        Result instance = resultArray.get(0);
        double expResult = 0.75;
        double result = instance.getMRR();
        assertEquals(expResult, result, 0.0);
        
    }

    /**
     * Test of getPrec method, of class Result.
     */
    @Test
    public void testGetPrec() {
        System.out.println("ResultTest.getPrec");
        Result instance = resultArray.get(0);
        double expResult = 0.8;
        double result = instance.getPrec();
        assertEquals(expResult, result, 0.0);
        
    }

    /**
     * Test of getRecall method, of class Result.
     */
    @Test
    public void testGetRecall() {
        System.out.println("ResultTest.getRecall");
        Result instance = resultArray.get(0);
        double expResult = 0.8;
        double result = instance.getRecall();
        assertEquals(expResult, result, 0.0);
      
    }

    /**
     * Test of getRMSE method, of class Result.
     */
    @Test
    public void testGetRMSE() {
        System.out.println("ResultTest.getRMSE");
        Result instance = resultArray.get(0);
        double expResult = 1.8;
        double result = instance.getRMSE();
        assertEquals(expResult, result, 0.0);
        
    }

    /**
     * Test of getAlpha method, of class Result.
     */
    @Test
    public void testGetAlpha() {
        System.out.println("ResultTest.getAlpha");
        Result instance = new Result();
        
        // getAlpha is only called by certain subclasses of Result, so 
        // this should just throw a RuntimeException
        boolean thrown = false;
        try {
            double result = instance.getAlpha();
        } catch(RuntimeException e) {
            thrown = true;
        }
        assertTrue(thrown);
    }

    /**
     * Test of getBeta method, of class Result.
     */
    @Test
    public void testGetBeta() {
        System.out.println("ResultTest.getBeta");
        Result instance = new Result();
        
        // getBeta is only called by certain subclasses of Result, so
        // this should just throw a RuntimeException
        boolean thrown = false;
        try {
            double result = instance.getBeta();
        } catch(RuntimeException e) {
            thrown = true;
        }
        assertTrue(thrown);
    }

    /**
     * Test of getGamma method, of class Result.
     */
    @Test
    public void testGetGamma() {
        System.out.println("ResultTest.getGamma");
        Result instance = new Result();
       
        // getGamma is only called by certain subclasses of Result, so
        // this should just throw a RuntimeException
        boolean thrown = false;
        try {
            double result = instance.getGamma();
        } catch(RuntimeException e) {
            thrown = true;
        }
        assertTrue(thrown);
    }

    /**
     * Test of getSampPerc method, of class Result.
     */
    @Test
    public void testGetSampPerc() {
        System.out.println("ResultTest.getSampPerc");
        
        // GetSampPerc is only called by certain subclasses of Result, so
        // this should just throw a RuntimeException
        
        Result instance = new Result();
        boolean thrown = false;
        try {
            double result = instance.getSampPerc();
        } catch(RuntimeException e) {
            thrown = true;
        }
        assertTrue(thrown);
    }

    /**
     * Test of getSimilarity method, of class Result.
     */
    @Test
    public void testGetSimilarity() {
        System.out.println("ResultTest.getSimilarity");
        
        // getSimilarity is only called on certain subclasses of Result, so this should just
        // thrown a RuntimeException
        Result instance = new Result();
        boolean thrown = false;
        try {
            double result = instance.getSimilarity();
        } catch(RuntimeException e) {
            thrown = true;
        }
        assertTrue(thrown);
    }

    /**
     * Test of printCounts method, of class Result.
     */
    @Test
    public void testPrintCounts() {
        System.out.println("ResultTest.printCounts");
        
        // Since this is largely a matter of preference how the output should appear, 
        // we should simply check to make sure nothing too terrible happens if a null
        // pointer is encountered, for instance
        ArrayList<Result> anArray = null; //resultArray;
        int loopCounter = 2;
        boolean nullExceptionThrown = false;
        try {
            Result.printCounts(anArray, loopCounter);
        } catch(NullPointerException e) {
            nullExceptionThrown = true;
        }
        assertTrue(nullExceptionThrown);
        
        anArray = resultArray; // no longer null
        
        // Now however, the follwoing line would cause division by zero errors so Result.printCounts should
        // throw a RuntimeException
        loopCounter = 0;
        
        boolean runtimeExceptionThrown = false;
        try {
            Result.printCounts(anArray, loopCounter);
        } catch(RuntimeException e) {
            runtimeExceptionThrown = true;
        }
        assertTrue(runtimeExceptionThrown);
    }
    
}
