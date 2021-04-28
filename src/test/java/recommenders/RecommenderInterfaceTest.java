package recommenders;

import datastructures.Movie;
import datastructures.Prediction;
import datastructures.User;
import java.util.HashMap;
import java.util.LinkedList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * This class tests the RecommenderInterface interface class. 
 * 
 * @author Juan David Mendez
 */
public class RecommenderInterfaceTest {
    
    /**
     *
     */
    public RecommenderInterfaceTest() {
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
    }
    
    /**
     *
     */
    @After
    public void tearDown() {
    }

    /**
     * Test of predict method, of class RecommenderInterface.
     */
    @Test
    public void testPredict() {
        
        System.out.println("predict");
        RecommenderInterface instance = new RecommenderInterfaceImpl();
        HashMap<User, LinkedList<Prediction>> expResult = null;
        HashMap<User, LinkedList<Prediction>> result = instance.predict(new HashMap<Integer, User>());
        assertEquals(expResult, result);
        
    }

    /**
     * Test of predictMovie method, of class RecommenderInterface.
     */
    @Test
    public void testPredictMovie() {
        System.out.println("predictMovie");
        User user = null;
        Movie movie = null;
        RecommenderInterface instance = new RecommenderInterfaceImpl();
        double expResult = 0.0;
        double result = instance.predictMovie(user, movie);
        assertEquals(expResult, result, 0.0);
        
    }

    /**
     *
     */
    public class RecommenderInterfaceImpl implements RecommenderInterface {

        /**
         *
         * @param user
         * @param movie
         * @return
         */
        @Override
        public double predictMovie(User user, Movie movie) {
            return 0.0;
        }

        @Override
        public HashMap<User, LinkedList<Prediction>> predict(HashMap<Integer, User> users) {
            return null;
        }
      
    }
           
}
