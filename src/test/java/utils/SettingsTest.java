package utils;

import java.util.HashMap;
import java.util.Map;
import org.junit.*;
import static org.junit.Assert.*;




/**
 * This class tests the Settings class. 
 * 
 * @author Brian
 */
public class SettingsTest {
    private final static String SETTINGSFILENAME = "test_settings.cfg";
    private final double SAMP_ERROR_DELTA = 0.2;
    private final double ERROR_DELTA = 0.001;
    //Settings settings;

    /**
     *
     */
        public SettingsTest() {
    }
    
    /**
     *
     */
    @BeforeClass
    public static void setUpClass() {
        System.out.println("Initializing settings file: " + SETTINGSFILENAME);
        Settings.loadNewSetting(SETTINGSFILENAME);
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
        Settings.loadNewSetting(SETTINGSFILENAME);
    }
    
    /**
     *
     */
    @After
    public void tearDown() {
        
    }
    
    /**
     *
     */
    @Test
    public void testGetDBName() {
        System.out.println("SettingsTest.getDBName");
        Assert.assertEquals("movies", Settings.getDBName());
    }

    /**
     *
     */
    @Test
    public void testGetTopK() {
        System.out.println("SettingsTest.getTopK");
        Assert.assertEquals(5,Settings.getTopK());
    }
    
    /**
     *
     */
    @Test
    public void testGetMaxRating() {
        System.out.println("SettingsTest.getMaxRating");
        Assert.assertEquals(5,Settings.getMaxRating(),0.01);
    }
    
    /**
     *
     */
    @Test
    public void testGetMinRating() {
        System.out.println("SettingsTest.getMinRating");
        Assert.assertEquals(1, Settings.getMinRating(),0.01);
    }
    
    /**
     *
     */
    @Test
    public void testGetNumUsers() {
        System.out.println("SettingsTest.getNumUsers");
        Assert.assertEquals(3,Settings.getNumUsers());
    }
    
    /**
     *
     */
    @Test
    public void testGetRocchioParams() {
        System.out.println("SettingsTest.getRocchioParams");
        
        // Create our own HashMap filled with properties and their expected values
        Map<String, Double> expectedRocchioWeights = new HashMap<>();
        expectedRocchioWeights.put("alpha",0.4);
        expectedRocchioWeights.put("beta", 0.2);
        expectedRocchioWeights.put("gamma", 0.8);
        
        // Retrieve the Settings
        Map<String,Double> actualRocchioWeights = Settings.getRocchioParams();
        
        Assert.assertEquals(expectedRocchioWeights, actualRocchioWeights); // Compare
    }        

    /**
     * Test of init method, of class Settings.
     */
    @Test
    public void testInit() {
        System.out.println("SettingsTest.init");
        
        Settings.init();
        
        // This is true because we are loading test_settings.cfg in the @beforeclass 
        assertTrue(Settings.getIsInitialized());
        assertEquals("test_settings.cfg", Settings.getSettingsFileName());
        
        
    }

    /**
     * Test of loadNewSetting method, of class Settings.
     */
    @Test
    public void testLoadNewSetting() {
        System.out.println("SettingsTest.loadNewSetting");
        String path = "test_settings.cfg";
        Settings.loadNewSetting(path);
    }
   
    /**
     * Test of getIsInitialized method, of class Settings.
     */
    @Test
    public void testGetIsInitialized() {
        System.out.println("SettingsTest.getIsInitialized");
        boolean expResult = true;
        boolean result = Settings.getIsInitialized();
        assertEquals(expResult, result);
       
    }

    /**
     * Test of getUserList method, of class Settings.
     */
    @Test
    public void testGetUserList() {
        System.out.println("SettingsTest.getUserList");
        String expResult = "2,3,4";
        String result = Settings.getUserList();
        assertEquals(expResult, result);
       
    }

    /**
     * Test of getTrainingSetSize method, of class Settings.
     */
    @Test
    public void testGetTrainingSetSize() {
        System.out.println("SettingsTest.getTrainingSetSize");
        double expResult = 0.7;
        double result = Settings.getTrainingSetSize();
        assertEquals(expResult, result, SAMP_ERROR_DELTA);
    }

    /**
     * Test of getSettingsFileName method, of class Settings.
     */
    @Test
    public void testGetSettingsFileName() {
        System.out.println("SettingsTest.getSettingsFileName");
        String expResult = "test_settings.cfg";
        String result = Settings.getSettingsFileName();
        assertEquals(expResult, result);
       
    }
    
    @Test
    public void testSetAlpha() {
        System.out.println("SettingsTest.seAlpha");
        
        final double expectedAlpha = 0.4;
        double actualAlpha1 = Settings.getRocchioParams().get("alpha");
        assertEquals(expectedAlpha, actualAlpha1, ERROR_DELTA);
        
        Settings.setAlpha(0.5);
        final double expectedAlpha2 = 0.5;
        double actualAlpha2 = Settings.getRocchioParams().get("alpha");
        assertEquals(expectedAlpha2, actualAlpha2, ERROR_DELTA);
        
        // If alpha is set in an improper range (i.e. not in [0,1.0]), an exception should be thrown.
        boolean thrown = false;
        try {
            Settings.setAlpha(-1.0); // Should cause an exception to be thrown
        } catch(RuntimeException e) {
            thrown = true;
        }
        assertTrue(thrown);
        
    }
    
    @Test
    public void testSetBeta() {
        System.out.println("SettingsTest.setBeta");
        Settings.resetSettingsToDefaultValues();
        final double expectedBeta1 = 0.2;
        double actualBeta1 = Settings.getRocchioParams().get("beta");
        assertEquals(expectedBeta1, actualBeta1, ERROR_DELTA);
        
        Settings.setBeta(0.4);
        final double expectedBeta2 = 0.4;
        double actualBeta2 = Settings.getRocchioParams().get("beta");
        assertEquals(expectedBeta2, actualBeta2, ERROR_DELTA);
        
        // If beta is set in an improper range (i.e. not in [0,1.0]), an exception should be thrown.
        boolean thrown = false;
        try {
            Settings.setBeta(-1.0); // Should cause an exception to be thrown
        } catch (RuntimeException e) {
            thrown = true;
        }
        assertTrue(thrown);
        
    }
    
    @Test
    public void testSetGamma() {
        System.out.println("SettingsTest.setGamma");
        
        final double expectedGamma = 0.8;
        double actualGamma1 = Settings.getRocchioParams().get("gamma");
        assertEquals(expectedGamma, actualGamma1, ERROR_DELTA);
        
        Settings.setGamma(0.4);
        final double expectedGamma2 = 0.4;
        double actualGamma2 = Settings.getRocchioParams().get("gamma");
        assertEquals(expectedGamma2, actualGamma2, ERROR_DELTA);
        
        // If gamma is set in an improper range (i.e. not in [0,1.0]), an exception should be thrown.
        boolean thrown = false;
        try {
            Settings.setGamma(-1.0); // Should cause an exception to be thrown
        } catch (RuntimeException e) {
            thrown = true;
        }
        assertTrue(thrown);
        
    }

    /**
     * Test of getPrintLevel method, of class Settings.
     */
    @Test
    public void testGetPrintLevel() {
        System.out.println("SettingsTest.getPrintLevel");
        double expResult = 7.0;
        double result = Settings.getPrintLevel();
        assertEquals(expResult, result, 0.0);
        
    }

    /**
     * Test of getNumFolds method, of class Settings.
     */
    @Test
    public void testGetNumFolds() {
        System.out.println("SettingsTest.getNumFolds");
        Settings.loadNewSetting("test_settings.cfg"); // resetting from previous tests
        int expResult = 1;
        int result = Settings.getNumFolds();
        assertEquals(expResult, result);
       
    }

    /**
     * Test of getPrintEachFoldEvaluation method, of class Settings.
     */
    @Test
    public void testGetPrintEachFoldEvaluation() {
        System.out.println("SettingsTest.getPrintEachFoldEvaluation");
        boolean expResult = true;
        boolean result = Settings.getPrintEachFoldEvaluation();
        assertEquals(expResult, result);
        
    }

    /**
     * Test of getSimilarityThreshold method, of class Settings.
     */
    @Test
    public void testGetSimilarityThreshold() {
        System.out.println("SettingsTest.getSimilarityThreshold");
        double expResult = 0.3;
        double result = Settings.getSimilarityThreshold();
        assertEquals(expResult, result, 0.0);
       
    }

    /**
     * Test of getPairwiseSampPerc method, of class Settings.
     */
    @Test
    public void testGetPairwiseSampPerc() {
        System.out.println("SettingsTest.getPairwiseSampPerc");
        double expResult = 0.6;
        double result = Settings.getPairwiseSampPerc();
        assertEquals(expResult, result, 0.0);
        
    }

    /**
     * Test of getWriteEvalToFile method, of class Settings.
     */
    @Test
    public void testGetWriteEvalToFile() {
        System.out.println("SettingsTest.getWriteEvalToFile");
        boolean expResult = true;
        boolean result = Settings.getWriteEvalToFile();
        assertEquals(expResult, result);
        
    }

    /**
     * Test of setNumUsers method, of class Settings.
     */
    @Test
    public void testSetNumUsers() {
        System.out.println("SettingsTest.setNumUsers");
        
        assertEquals(3, Settings.getNumUsers());
        int num = 0;
        Settings.setNumUsers(num);
        
        assertEquals(num, Settings.getNumUsers());
        
    }

    /**
     * Test of setSimilarityThreshold method, of class Settings.
     */
    @Test
    public void testSetSimilarityThreshold() {
        System.out.println("SettingsTest.setSimilarityThreshold");
        
        // the default from test_settings.cfg
        assertEquals(0.3, Settings.getSimilarityThreshold(), ERROR_DELTA); 
        
        // Now we can change it and make sure the results changed
        double similarity = 0.0;
        Settings.setSimilarityThreshold(similarity);
        
        assertEquals(similarity, Settings.getSimilarityThreshold(), ERROR_DELTA);
    }

    /**
     * Test of setPairwiseSampleSize method, of class Settings.
     */
    @Test
    public void testSetPairwiseSampleSize() {
        System.out.println("SettingsTest.setPairwiseSampleSize");
        
        // Make sure it's the default from test_settings.cfg
        assertEquals(0.6, Settings.getPairwiseSampPerc(), ERROR_DELTA);
        
        // Now set it to something else and verify it was changed
        double perc = 0.0;
        Settings.setPairwiseSampleSize(perc);
        
        assertEquals(perc, Settings.getPairwiseSampPerc(), ERROR_DELTA);
    }

    /**
     * Test of setNumFolds method, of class Settings.
     */
    @Test
    public void testSetNumFolds() {
        System.out.println("SettingsTest.setNumFolds");
        
        assertEquals(1, Settings.getNumFolds()); // Default setting in test_settings.cfg
        int newNumFolds = 3;
        Settings.setNumFolds(newNumFolds);
       
        assertEquals(newNumFolds, Settings.getNumFolds());
        
        // Now to test that a Runtime Exception is thrown if we try to set an invalid value
        boolean thrown = false;
        try {
            Settings.setNumFolds(-1);
        } catch(RuntimeException e) {
            thrown = true;
        }
        
        assertTrue(thrown);
        
    }
}
