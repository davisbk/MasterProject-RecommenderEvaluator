package utils;

import datastructures.Rating;
import datastructures.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * This class tests the DataPreparer class. 
 * 
 * @author Brian Davis
 */
public class DataPreparerTest {
    static int numFoldsBeforeAdjusting;
    static boolean numFoldsWasAdjusted = false;
    
    public DataPreparerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        Settings.loadNewSetting("test_settings.cfg");
        numFoldsBeforeAdjusting = Settings.getNumFolds();
        if(numFoldsBeforeAdjusting == 1) {
            Settings.setNumFolds(3);
            numFoldsWasAdjusted = true;
        }
    }
    
    @AfterClass
    public static void tearDownClass() {
        
        // If we adjusted the number of folds, reset it to this number.
        // Most other unit tests assume just one fold, but this class needs multiple folds
        // in order to be properly tested.
        if(numFoldsWasAdjusted) {
            Settings.setNumFolds(numFoldsBeforeAdjusting);
        }
        
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getUserLists method, of class DataPreparer.
     */
    @Test
    public void testGetUserLists() {
        System.out.println("DataPreparerTest.getUserLists");
        DBManager dbmgr = DBManager.getInstance();
        dbmgr.getDataWithEnoughRatings();
                
        DataPreparer instance = new DataPreparer();
        
        List<HashMap<Integer, User>> result = instance.getUserLists();
        assertEquals(3, result.size());
        
        for(HashMap<Integer, User> currentHash : result) {
            assertTrue(currentHash.size() == 3);
            assertTrue(currentHash.containsKey(2));
            assertTrue(currentHash.containsKey(3));
            assertTrue(currentHash.containsKey(4));
            
        }
        
        // Get the same User from each iteration
        HashMap<Integer,User> hash1 = result.remove(0);
        User user2_iter1 = hash1.get(2);
        
        HashMap<Integer,User> hash2 = result.remove(0);
        User user2_iter2 = hash2.get(2);
        
        HashMap<Integer,User> hash3 = result.remove(0);
        User user2_iter3 = hash3.get(2);
        
        // Get the Ratings for each Iteration
        ArrayList<Rating> user2_iter1_ratings = user2_iter1.getTrainingRatings();
        ArrayList<Rating> user2_iter2_ratings = user2_iter2.getTrainingRatings();
        ArrayList<Rating> user2_iter3_ratings = user2_iter3.getTrainingRatings();
               
        // Now that we have all Ratings, we can test that there are no duplicates among the iterations
        
        // Iteration 1 to 2
        boolean containsAll1 = true;
        
        for(Rating rating : user2_iter1_ratings) { // for every Rating in the first iteration
            if(!user2_iter2_ratings.contains(rating)) { // at some point there is some Rating in the first iteration which is not in the second
                containsAll1 = false;
            }
        }
        
        assertTrue(containsAll1 == false);
        
        // Iteration 1 to 3
        boolean containsAll5 = true;
        
        for(Rating rating : user2_iter1_ratings) {
            if(!user2_iter3_ratings.contains(rating)) {
                containsAll5 = false;
            }
        }
        
        assertTrue(containsAll5 == false);
        
        // Iteration 2 to 3
        boolean containsAll2 = true;
        
        for(Rating rating : user2_iter2_ratings) {
            if(!user2_iter3_ratings.contains(rating)) {
                containsAll2 = false;
            }
        }
        
        assertTrue(containsAll2 == false);
                
    }
    
}
