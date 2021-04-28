package recommenders;


import datastructures.Movie;
import datastructures.Prediction;
import datastructures.PropertiesHash;
import datastructures.Property;
import datastructures.Rating;
import datastructures.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import recommenders.Pairwise.PairwiseUser;
import utils.DBManager;
import utils.Settings;

/**
 * This class tests the Pairwise recommender class. 
 * 
 * @author Brian Davis
 */
public class PairwiseTest {
    private final static double ERROR_DELTA = 0.001; // Margin of error for assertEquals comparisons
    private static DBManager dbmgr;
    
    // This stores the "default" movies loaded in by test_settings.cfg.
    // Different methods may change the Movies in DBManager.movies, so we want to be able to restore the original movies.
    private static HashMap<Integer, Movie> storedMovies; 
    
    
    
    @BeforeClass
    public static void setUpClass() {
        Settings.loadNewSetting("test_settings.cfg");
        dbmgr = DBManager.getInstance();
        dbmgr.getDataWithEnoughRatings();
        storedMovies = new HashMap<>(DBManager.getMovies());
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        DBManager.setMovies(storedMovies);
    }

    @After
    public void tearDown() throws Exception {
    }
      
    /**
     * Test of computeAlpha method, of class Pairwise.
     */
    @Test
    public void testComputeAlpha() {
        System.out.println("PairwiseRocchio.computeAlphaTest");
        
        // We need to create a PairwiseUser object in order to test this method.
        // First we will create some Movies for this User. We will have three movies,
        // in our case the three LotR movies. The User will rate movies 1 and 2 with 
        // four stars and the last with 1 even though they are so similar. The
        // calculation of alpha should then have a high absolute value for the values from
        // class 1 to 4. 
        PropertiesHash movieProp1 = new PropertiesHash();
        movieProp1.add(new Property("genre", "action"), 1.0);
        movieProp1.add(new Property("genre", "drama"), 1.0);
        movieProp1.add(new Property("starring", "hugo weaving"), 1.0);
        movieProp1.add(new Property("director", "peter jackson"), 1.0);
        movieProp1.add(new Property("year", "2001"), 1.0);
        
        PropertiesHash movieProp2 = new PropertiesHash();
        movieProp2.add(new Property("genre", "action"), 1.0);
        movieProp2.add(new Property("genre", "drama"), 1.0);
        movieProp2.add(new Property("director", "peter jackson"), 1.0);
        movieProp2.add(new Property("starring", "viggo mortensen"), 1.0);
        movieProp2.add(new Property("year", "2002"), 1.0);
        
        PropertiesHash movieProp3 = new PropertiesHash();
        movieProp3.add(new Property("genre", "action"), 1.0);
        movieProp3.add(new Property("genre", "drama"), 1.0);
        movieProp3.add(new Property("director", "peter jackson"), 1.0);
        movieProp3.add(new Property("year", "2003"), 1.0);
        movieProp3.add(new Property("starring", "viggo mortensen"), 1.0);
        
        Movie movie1,movie2,movie3;
        movie1 = new Movie(1, "uri1", "LotR1", movieProp1);
        movie2 = new Movie(2, "uri2", "LotR2", movieProp2);
        movie3 = new Movie(3, "uri3", "LotR3", movieProp3);
        
        // Now we will create some training Ratings for movies 1 and 2
        ArrayList<Rating> trainingRatings = new ArrayList<>();
        trainingRatings.add(new Rating(movie1,4,1234));
        trainingRatings.add(new Rating(movie2,4,1235));
        trainingRatings.add(new Rating(movie3,1,1236));
        
        // Now we can create our User (with an empty test set)
        User user = new User(1,"M",32,1,83301,trainingRatings, new ArrayList<Rating>());
        HashMap<Integer, User> userHash = new HashMap<>();
        userHash.put(user.getId(), user);
        
        // Set up our DBManager's movies member
        HashMap<Integer, Movie> movieHash = new HashMap<>();
        movieHash.put(movie1.getId(), movie1);
        movieHash.put(movie2.getId(), movie2);
        movieHash.put(movie3.getId(), movie3);
        DBManager.setMovies(movieHash);
        
        // We will use all movies for our samples
        Settings.setPairwiseSampleSize(1.0);
        assertEquals(1.0, Settings.getPairwiseSampPerc(), ERROR_DELTA);
        
        // Create our Pairwise object.
        Pairwise pwr = new Pairwise(userHash);
        
        // We have all only two classes, ratings 1 and 4. Since they're stored in a two-dimensional array their indices are 0 and 3. 
        final double expectedAlpha = -0.35; 
        final double actualAlpha = pwr.pairwiseUsers.get(1).getAlpha(0, 3);
        assertEquals(expectedAlpha, actualAlpha, ERROR_DELTA);
        
        // All other values should be zero:
        for(int i = 0; i < 5; i++) {
            for(int j = 0; j < 5; j++) {
                if(i == 0 && j == 3) { // This is the value we previously checked
                } else {
                    assertEquals(0.0, pwr.pairwiseUsers.get(1).getAlpha(i,j), ERROR_DELTA);
                }
            }
        }
        
    }
    
    /**
     * Test of computeAverageDistance method, of class Pairwise.
     */
    @Test
    public void testComputeAverageDistance() {
        System.out.println("PairwiseRocchio.computeAverageDistanceTest");
        
        LinkedList<Integer> indices = new LinkedList<>(); // Stores the indices in order to retrieve the Movies from the database
        
        
        PropertiesHash profileVector = new PropertiesHash(); // The "profile vector" to compare the distance against
        
        // Add a couple Movies to be compared
        indices.add(1357);
        indices.add(3068);
        
        // Add the "critical props" to the profile vector
        profileVector.add(new Property("director","Scott Hicks"), 1.0); // 1357
        profileVector.add(new Property("director","Sidney Lumet"), 1.0); // 3068
        profileVector.add(new Property("starring","Paul Newman"), 1.0); // 3068
        profileVector.add(new Property("starring","James Mason"), 1.0); // 3068
        profileVector.add(new Property("starring","Milo O'Shea"), 1.0); // 3068
        profileVector.add(new Property("starring","Charlotte Rampling"), 1.0); // 3068
        profileVector.add(new Property("starring","Jack Warden"), 1.0); // 3068
        profileVector.add(new Property("starring","Geoffrey Rush"), 1.0); // 1357
        profileVector.add(new Property("starring","John Gielgud"), 1.0); // 1357
        profileVector.add(new Property("starring","Noah Taylor"), 1.0); // 1357
        profileVector.add(new Property("starring","Lynn Redgrave"), 1.0); // 1357
        profileVector.add(new Property("starring","Armin Mueller-Stahl"), 1.0); //1357
        profileVector.add(new Property("genre", "Drama"), 1.0); // 1357 and 3068
        profileVector.add(new Property("genre", "Romance"), 1.0); // 1357
        
        // The profile vector is normalized, so we normalize it here.
        profileVector.normalize();
        
        // Using the order given above, our profile vector after the normalization is:
        // [0.26726   0.26726   0.26726   0.26726   0.26726   0.26726   0.26726   0.26726   0.26726   0.26726   0.26726   0.26726   0.26726   0.2672]
        
        //  Now we have to set up the Pairwise object in order to create a PairwiseUser for the computeAverageDistance method
        Pairwise pwr = new Pairwise(new HashMap<Integer, User>()); // Since PairwiseUser is nested we have to have an instance object
        User usr = new User(1,"M",32,8,83301,new ArrayList<Rating>(), new ArrayList<Rating>()); // We need a User object for the PairwiseUser constructor
        Pairwise.PairwiseUser pwUser = pwr.new PairwiseUser(usr);
        
        // We need to set the DBManager.movies variable or else the method will retrieve all Properties
        HashMap<Integer, Movie> movies = new HashMap<>();
        Movie movie1_tmp = new Movie(DBManager.getMovies().get(1357));
        Movie movie2_tmp = new Movie(DBManager.getMovies().get(3068));
        Movie movie1 = new Movie(1357,"uri1","title1",PropertiesHash.getCriticalProps(movie1_tmp.getProperties()));
        Movie movie2 = new Movie(3068,"uri2","title2",PropertiesHash.getCriticalProps(movie2_tmp.getProperties()));
        
        // movie1's (normalized) properties represented as a vector (again using the ordering from above) is:
        // [0.35355   0.00000   0.00000   0.00000   0.00000   0.00000   0.00000   0.35355   0.35355   0.35355   0.35355   0.35355   0.35355   0.35355]
        
        // movie2's (normalized) properties represented as a vector (also using the above ordering) is:
        // [0.00000   0.37796   0.37796   0.37796   0.37796   0.37796   0.37796   0.00000   0.00000   0.00000   0.00000   0.00000   0.37796   0.00000]
        
        // Add the Movies to the DBManager.movies data member (the Properties of the movie are read from here in the method).        
        movies.put(movie1.getId(),movie1);
        movies.put(movie2.getId(),movie2);
        DBManager.setMovies(movies);
        
        // dist = sqrt( sum ( (vect1 .- vect2).^2) )
        // So for us, avg dist =  ( dist(movie1, profile) + dist(movie2, profile) ) /  2
        // In the math below, the vector operations have been shortened, since for either vector there are really only two possibilities:
        // 1) The value 0.26726 (the normalized value in the profile) is subtracted from something, as when the movie vector has a value for that property, or
        // 2) 0.26726 is squared with itself since there is no value for that particular property (i.e. (0.26726 - 0 )^2)
        
        // Thus for our calculations:
        // avgdist = (sqrt( 8 * ( 0.35355 - 0.26726)^2 + 6 * (0.26726^2)) + sqrt(7 * (0.37796 - 0.26726)^2 + 7 * (0.26726^2)) ) /2 = 0.73202
        //final double expectedAverageDistance = 0.73202;
        final double expectedAverageDistance = 2.119;
        //double actualAverageDistance = Pairwise.computeAverageDistance(pwUser,0,0,profileVector);
        double actualAverageDistance = Pairwise.computeAverageDistance(indices, profileVector);
        assertEquals(expectedAverageDistance, actualAverageDistance, ERROR_DELTA);
        
        // If we now add a new Property with value 1 which neither of the movies have,
        // because of how distance is calculated the difference is zero. 
        profileVector.add(new Property("genre", "Action"), 1.0); // None of the movies have this
        
        final double expectedAverageDistance2 = 2.737;
        //double actualAverageDistance2 = Pairwise.computeAverageDistance(profileVector, indices);
        //assertEquals(expectedAverageDistance2, actualAverageDistance2, ERROR_DELTA);
    }
    
    
    
    @Test
    public void testGenerateIndices() {
        System.out.println("PairwoseRocchioTest.testGeneratedIndices");
        Settings.loadNewSetting("test_settings.cfg"); // To get Movies from the Database
        final HashMap<Integer, Movie> movies = DBManager.getMovies();
        
        // First get some Movies for this User.
        // Training Movies
        Movie movie1 = movies.get(2628); // Star Wars - Episode I
        Movie movie2 = movies.get(1196); // Star Wars - Episode V
        Movie movie3 = movies.get(647); // Courage Under Fire
        Movie movie4 = movies.get(2194); // The Untouchables
        
        // Test Movies
        Movie movie5 = movies.get(648); // Mission: Impossible
        Movie movie6 = movies.get(1210); // Sar Wars - Episode VI
        
        // Create ratings for this User (training and test)
        ArrayList<Rating> trainingRatings = new ArrayList<>();
        trainingRatings.add(new Rating(movie1,3,12345));
        trainingRatings.add(new Rating(movie2,5,12346));
        trainingRatings.add(new Rating(movie3,3,12347));
        trainingRatings.add(new Rating(movie4,4,12348));
        
        ArrayList<Rating> testRatings = new ArrayList<>();
        testRatings.add(new Rating(movie5,4,12349));
        testRatings.add(new Rating(movie6,4,12350));
        
        // Create the User
        User user1 = new User(1,"F",30,3,83301,trainingRatings, testRatings);
        
        // Add them to the HashMap for initializing our Pairwise object
        HashMap<Integer, User> userHash = new HashMap<>();
        userHash.put(user1.getId(), user1);
        
        // Now we have our 4 Movies in the training set. We need to create a Pairwise object and
        // then we can see how many samples were generated, etc. This is determined by Pairwise.SAMP_PERC, 
        // which we can set via the Settings. 
        Settings.setPairwiseSampleSize(0.50);
        
        // Create the Pairwise object
        Pairwise pwrInstance = new Pairwise(userHash);
        
        // Make sure that the sample percentage really was set to 0.5
        assertEquals(0.50, pwrInstance.SAMP_PERC, ERROR_DELTA);
        
        LinkedList<Integer>[] actualSamplesList = pwrInstance.pairwiseUsers.get(1).getSampleIndices();
        
        // Since we only had one rating for which there was more than one movie (3), 
        // this is the only rating score where we will have an index.
        final int expNum = 1;
        int actualNum = actualSamplesList[2].size();
        assertEquals(expNum, actualNum);
        
        // Else the size should be zero!
        for(int i = 0; i < 5; i++) {
            if(i != 2) {
                assertTrue(actualSamplesList[i].isEmpty());
            }
        }
        
        
    }

    /**
     * Test of predict method, of class Pairwise.
     */
    @Test
    public void testPredict() {
        System.out.println("PairwiseRocchio.predict");
        
        final HashMap<Integer, Movie> movies = DBManager.getMovies();
        
        // Make a new User
        
        // First get some Movies for this User.
        // Training Movies
        Movie movie1 = movies.get(2628); // Star Wars - Episode I
        Movie movie2 = movies.get(1196); // Star Wars - Episode V
        Movie movie3 = movies.get(647); // Courage Under Fire
        
        // Test Movies
        Movie movie4 = movies.get(2194); // The Untouchables
        Movie movie5 = movies.get(648); // Mission: Impossible
        Movie movie6 = movies.get(1210); // Sar Wars - Episode VI
        
        // Create ratings for this User (training and test)
        ArrayList<Rating> trainingRatings = new ArrayList<>();
        trainingRatings.add(new Rating(movie1,3,12345));
        trainingRatings.add(new Rating(movie2,5,12346));
        trainingRatings.add(new Rating(movie3,3,12347));
        
        ArrayList<Rating> testRatings = new ArrayList<>();
        testRatings.add(new Rating(movie4,4,12348));
        testRatings.add(new Rating(movie5,4,12349));
        testRatings.add(new Rating(movie6,4,12350));
        
        // Create the User
        User user2 = new User(2,"F",30,3,83301,trainingRatings, testRatings);
        
        // Add them to the HashMap for initializing our Pairwise object
        HashMap<Integer, User> userHash = new HashMap<>();
        userHash.put(user2.getId(), user2);
        
        // Create the Pairwise object
        Pairwise pwrInstance = new Pairwise(userHash);
        
        // Now we can run the predict method        
        HashMap<User, LinkedList<Prediction>> predictions = new HashMap<>(pwrInstance.predict(userHash));
        
        // Only one User, user2
        assertTrue(predictions.containsKey(user2));
        assertTrue(predictions.size() == 1);
        
        // Three Movies in the test set
        LinkedList<Prediction> user2Preds = predictions.get(user2);
        assertTrue(user2Preds.size() == 3);
        
       
        assertTrue(user2Preds.get(0).getMovie().equals(movie6));
        final double expectedPred1Value = 0.8;
        double actualPred1Value = user2Preds.get(0).getValue();
        assertEquals(expectedPred1Value, actualPred1Value, ERROR_DELTA);
        
        assertTrue(user2Preds.get(1).getMovie().equals(movie4));
        final double expectedPred2Value = 0.1511;
        double actualPred2Value = user2Preds.get(1).getValue();
        assertEquals(expectedPred2Value, actualPred2Value, ERROR_DELTA);
        
        assertTrue(user2Preds.get(2).getMovie().equals(movie5));
        final double expectedPred3Value = 0.1414;
        double actualPred3Value = user2Preds.get(2).getValue();
        assertEquals(expectedPred3Value, actualPred3Value, ERROR_DELTA);
    }

    /**
     * Test of predictMovie method, of class Pairwise.
     */
    @Test
    public void testPredictMovie() {
        System.out.println("PairwiseRocchio.testPredictMovie");
        final HashMap<Integer, Movie> movies = DBManager.getMovies();
        // Make a new User
        
        // First get some Movies for this User
        Movie movie1 = movies.get(1357);
        Movie movie2 = movies.get(3068);
        Movie movie3 = movies.get(647);
        Movie movie4 = movies.get(2194);
        Movie movie5 = movies.get(648);
        
        //Create ratings for this User (training and test)
        ArrayList<Rating> trainingRatings = new ArrayList<>();
        trainingRatings.add(new Rating(movie1,5,12345));
        trainingRatings.add(new Rating(movie2,4,12346));
        trainingRatings.add(new Rating(movie3,3,12347));
        
        ArrayList<Rating> testRatings = new ArrayList<>();
        testRatings.add(new Rating(movie4,4,12348));
        testRatings.add(new Rating(movie5,4,12349));
        
        // Create the User
        User user2 = new User(2,"F",30,3,83301,trainingRatings, testRatings);
        
        // Add them to the HashMap for initializing our Pairwise object
        HashMap<Integer, User> userHash = new HashMap<>();
        userHash.put(user2.getId(), user2);
        
        // Create the Pairwise object
        Pairwise pwrInstance = new Pairwise(userHash);
        
        final double expectedPrediction = 0.1989;
        double actualPrediction = pwrInstance.predictMovie(user2, movie4);
        assertEquals(expectedPrediction, actualPrediction, ERROR_DELTA);
    }
    
    
}
