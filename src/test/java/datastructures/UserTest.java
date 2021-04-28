package datastructures;

import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * This class tests the User class. 
 * 
 * @author Brian Davis
 */
public class UserTest {
        
    private static User testUser;
    //private static HashMap<Integer, User> allUsers;
    private static ArrayList<Rating> user1Ratings;                  // Stores the Ratings of testUser(uid = 1)
    private static ArrayList<Rating> user1TestRatings;             // Stores the test Ratings of testUser(uid = 1)
    private static Movie movie1;                                    // Toy Story
    private static Movie movie2;                                    // Toy Story 2
    private static Movie movie3;                                    // Trainspotting
    private static Movie movie4;                                    // Superman
    private static Movie movie5;                                    // Superman 2
    private static Rating rating1,rating2,rating3,rating4,rating5;  // The Ratings for the five Movies
    
    /**
     *
     */
    public UserTest() {
    }
    
    /**
     *
     */
    @BeforeClass
    public static void setUpClass() {
        
        // To set up the test cases, we'll build a User from scratch with 3 rated Movies and 2 test Movies
        user1Ratings = new ArrayList<>(); // All of the Ratings of the User
        user1TestRatings = new ArrayList<>(); // The test Ratings of the User
        
        // Create our 3 movies the user rated 
        movie1 = new Movie(1,"http://dbpedia.org/resource/Toy_Story","Toy Story",new PropertiesHash()); // We can just use generic PropertiesHash since we're not actually testing this
        movie2 = new Movie(2,"http://dbpedia.org/resource/Toy_Story_2","Toy Story 2", new PropertiesHash());
        movie3 = new Movie(3,"http://dbpedia.org/resource/Trainspotting_(film)","Trainspotting", new PropertiesHash());
        
        // Create 3 Ratings for the above movies
        rating1 = new Rating(movie1,1,978322275);
        rating2 = new Rating(movie2,3,978300275);
        rating3 = new Rating(movie3,5,928322272);
        
        // Add all of the Ratings to this User's Ratings ArrayList
        user1Ratings.add(rating1);
        user1Ratings.add(rating2);
        user1Ratings.add(rating3);
        
        // Create our 2 movies for the test ratings 
        movie4 = new Movie(4,"http://dbpedia.org/resource/Superman_(film)","Superman",new PropertiesHash());
        movie5 = new Movie(5,"http://dbpedia.org/resource/Superman_II","Superman 2", new PropertiesHash());
        
        // Create 2 Ratings for the test Movies
        rating4 = new Rating(movie4,2,989433386);
        rating5 = new Rating(movie5,4,942837492);
        
        // Add the 2 test Ratings to the User's test Ratings LinkedList
        user1TestRatings.add(rating4);
        user1TestRatings.add(rating5);
        
        // Finally, create our User
        testUser = new User(1,"male",20,3,90210,user1Ratings,user1TestRatings); 
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
     *
     */
    @Test
    public void testGetRatings() {
        System.out.println("UserTest.getRatings");
        ArrayList<Rating> tmpRatings = testUser.getTrainingRatings();
        
       Assert.assertTrue(tmpRatings.contains(rating1));
       Assert.assertTrue(tmpRatings.contains(rating2));
       Assert.assertTrue(tmpRatings.contains(rating3));
       Assert.assertTrue(tmpRatings.size() == 3);
        
    }
        
    /**
     *
     */
    @Test
    public void testGetAge_range() {
        System.out.println("UserTest.getAge_range");
        Assert.assertEquals(20, testUser.getAgeRange());
    }

    /**
     *
     */
    @Test
    public void testGetAvgRating() {
        System.out.println("UserTest.getAvgRating");
        System.out.println("User average: " + testUser.getAvgRating());
        Assert.assertTrue(testUser.getAvgRating() == 3.0);
    }

    /**
     *
     */
    @Test
    public void testGetGender() {
        System.out.println("UserTest.getGender");
        Assert.assertTrue(testUser.getGender().equals("male"));
    }

    /**
     *
     */
    @Test
    public void testGetId() {
        System.out.println("UserTest.getId");
        Assert.assertTrue(testUser.getId() == 1);
    }

    /**
     *
     */
    @Test
    public void testGetOccupation() {
        System.out.println("UserTest.getOccupation");
        Assert.assertTrue(testUser.getOccupation() == 3);
    }

    /**
     *
     */
    @Test
    public void testGetTestRatings() {
        System.out.println("UserTest.getTestRatings");
        ArrayList<Rating> tmpRatings = testUser.getTestRatings();
        
        Assert.assertTrue(tmpRatings.contains(rating4));
        Assert.assertTrue(tmpRatings.contains(rating5));
        Assert.assertTrue(tmpRatings.size() == 2);
    }

    /**
     *
     */
    @Test
    public void testGetZipCode() {
        System.out.println("UserTest.getZipCode");
        Assert.assertTrue(testUser.getZipCode() == 90210);
    }

    /**
     * Test of toString method, of class User.
     */
    @Test
    public void testToString() {
        System.out.println("UserTest.toString");
        User instance = testUser;
        String result = instance.toString();
        StringBuilder expResult = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        expResult.append(instance.getClass().getName()).append(" {").append(NEW_LINE);
        expResult.append(" ID: ").append(instance.getId()).append(NEW_LINE);
        expResult.append(" Gender: ").append(instance.getGender()).append(NEW_LINE);
        expResult.append(" Age Range: ").append(instance.getAgeRange()).append(NEW_LINE);
        expResult.append(" Occupation: ").append(instance.getOccupation()).append(NEW_LINE);
        expResult.append(" ZipCode: ").append(instance.getZipCode()).append(NEW_LINE);
        expResult.append(" Ratings: ").append(NEW_LINE).append(instance.getTrainingRatings()).append(NEW_LINE);
        expResult.append(" Test-Ratings: ").append(NEW_LINE).append(instance.getTestRatings());
        expResult.append("}");
        Assert.assertEquals(expResult.toString(), result);
    }

    /**
     * Test of hashCode method, of class User.
     */
    @Test
    public void testHashCode() {
        System.out.println("UserTest.hashCode");                  
        User instance = new User(1, "M", 3, 4, 83301, new ArrayList<Rating>(), new ArrayList<Rating>());    
        User instance2 = new User(1, "M", 3, 4, 83301, new ArrayList<Rating>(), new ArrayList<Rating>());  
        Assert.assertTrue(instance.equals(instance2) && instance2.equals(instance));
        Assert.assertTrue(instance2.hashCode() == instance.hashCode());
    }

    /**
     * Test of equals method, of class User.
     */
    @Test
    public void testEquals() {
        System.out.println("UserTest.equals");
        Object obj = new Object(); // Create new random Object
        User user1 =  new User(1, "M", 3, 4, 83301, new ArrayList<Rating>(), new ArrayList<Rating>()); // Create new User
        
        // User is not equal to some other Object
        final boolean expResult1 = false;
        boolean result1 = user1.equals(obj);
        assertEquals(expResult1, result1);
        
        // User should be equal to itself
        final boolean expResult2 = true;
        boolean result2 = user1.equals(user1);
        assertEquals(expResult2, result2);
        
        // Create new User; two Users should not be equal
        User user2 = new User(2, "F", 3, 4, 90210, new ArrayList<Rating>(), new ArrayList<Rating>());
        
        final boolean expResult3 = false;
        boolean result3 = user1.equals(user2);
        assertEquals(expResult3, result3);
    }

    /**
     * Test of getTestRatingByID method, of class User.
     */
    @Test
    public void testGetTestRating() {
        System.out.println("UserTest.getTestRating");
        int movieID = 4; // Rated a 2 by testUser
        User instance = testUser;
        int expResult1 = 2;
        int actualResult = instance.getTestRatingByID(movieID);
        assertEquals(expResult1, actualResult);
        
        // What happens if the movie is not found? Should return -1
        int expResult2 = -1;
        int actualResult2 = instance.getTestRatingByID(100000); // Movie with ID 100000 does not exist
        assertEquals(expResult2, actualResult2);
        
    }

    /**
     * Test of compareTo method, of class User.
     */
    @Test
    public void testCompareTo() {
        System.out.println("UserTest.compareTo");
        // The compareTo method will be working properly if an ArrayList of Users is sorted 
        // properly. Thus we can create a few new Users, add them to an ArrayList, and see what happens.
        User user1 = new User(3,"M",32,42,83301,new ArrayList<Rating>(),new ArrayList<Rating>());
        User user2 = new User(1,"F",2,22,83353,new ArrayList<Rating>(),new ArrayList<Rating>());
        User user3 = new User(2,"F",23,10,90210,new ArrayList<Rating>(),new ArrayList<Rating>());
        User user4 = new User(5,"M",42,82,90210,new ArrayList<Rating>(),new ArrayList<Rating>());
        
        ArrayList<User> unsortedUserArray = new ArrayList<>();
        unsortedUserArray.add(user1);
        unsortedUserArray.add(user2);
        unsortedUserArray.add(user3);
        unsortedUserArray.add(user4);
        
        // Copy the ArrayList
        ArrayList<User> sortedUserArray = new ArrayList<>(unsortedUserArray); 
        sortedUserArray.sort(null); // Sort the array
        
        // Now check to make sure that each User ID is greater than the previous User's ID
        int previousUserID = sortedUserArray.get(0).getId(); // Get the first User's ID
        for(int i = 1; i < sortedUserArray.size(); i++) {
            int currentUserID = sortedUserArray.get(i).getId(); // Get current User's ID
            
            assertTrue(currentUserID >= previousUserID);
            previousUserID = currentUserID;
        }
        
    }

}
