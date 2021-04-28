package recommenders;


import datastructures.Movie;
import datastructures.Prediction;
import datastructures.PropertiesHash;
import datastructures.Property;
import datastructures.Rating;
import datastructures.User;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import utils.DBManager;
import utils.Printer;
import utils.Settings;

/**
 * This recommender is a type of recommender with the addition of logic from
 * the paper "Learning Routing Queries in a Query Zone" by Singhal, Mitra, and Buckley.
 * The idea is that instead of using ALL non-relevant data, only those in a "query zone"
 * are used to update the Rocchio profile. A "query zone" is a subset of the total data 
 * which is relevant to the domain of the query but not relevant to the actual query.
 * For more information please see the paper at: http://singhal.info/query-zone.pdf
 * 
 * Genres are our interpretation of a query zone. 
 * 
 * @author Brian Davis
 */
public class QueryZone implements RecommenderInterface {
    HashMap<Integer, QueryZoneUser> qzrUsers = new HashMap<>();

    protected int numTopKGenres = Settings.getNumTopKGenres(); // How many genres to use for calculating top genres
    protected double alpha = Settings.getRocchioParams().get("alpha");
    protected double beta = Settings.getRocchioParams().get("beta");
    protected double gamma = Settings.getRocchioParams().get("gamma");
    protected final double similarityThreshold = Settings.getSimilarityThreshold();  // The threshold for whether a Movie is similar enough to the profile
    Printer printer = Printer.getCurrentPrinter();
    
    /**
     * The default constructor.  The Users for whom we wish to build profiles are passed as an argument. 
     * 
     * @param users The Users for whom we build profiles
     */
    public QueryZone(HashMap<Integer, User> users) {
        
        // We're working with QueryZoneUsers, so we must create them from the Users
        for(Map.Entry<Integer, User> userEntry : users.entrySet())  {
                        
            User user = userEntry.getValue();  // The current user
            QueryZoneUser qzrUser = new QueryZoneUser(user); // The current QueryZoneUser
             
            qzrUsers.put(qzrUser.getId(), qzrUser);
        }
        
    }
    
    /**
     * This method finds the top k genres for User. It does this by using a Comparator to compare the 
     * values of each Property.value where the Property.propLabel is one of the genres ("comedy","horror", etc.)
     * 
     * @param profileVector The PropertiesHash which contains the profile of the user
     * @param k_genres The number (top-k) of genres to return
     * @return ret An ArrayList of Strings of the top k genres
     */
    
    public static ArrayList<String> findTopKGenres (final PropertiesHash profileVector, int k_genres) {
        ArrayList<String> ret = new ArrayList<>(getPropLabelsForPropCat(profileVector, "genre")); // Get all genres
        
        // Sort them according to their value
        ret.sort(new Comparator<String>()
        {
        @Override
        public int compare(String s1, String s2 ) {
            
            if( (profileVector.getValueByCatAndLabel("genre",s1)) > (profileVector.getValueByCatAndLabel("genre",s2)) )
                return -1;
            else if ( (profileVector.getValueByCatAndLabel("genre", s1)) < (profileVector.getValueByCatAndLabel("genre", s2)) )
                return 1;
            else return 0;
        }
        } );
        
        // Check to make sure we have enough genres!
        if(ret.size() > k_genres) // If not, return the first (top) k
            return new ArrayList<>(ret.subList(0, k_genres));
        else // Return all
            return ret;
        
    }
    
    /**
     * Returns an ArrayList of Strings with all of the genres. 
     * 
     * @param propHash
     * @param catToFind
     * @return 
     */
    public static ArrayList<String> getPropLabelsForPropCat(PropertiesHash propHash, String catToFind) {
        HashMap<Property, Double> properties = propHash.getProperties();
        ArrayList<String> ret = new ArrayList<>();
        
        for(Map.Entry<Property, Double> mapEntry : properties.entrySet()) {
            if(mapEntry.getKey().getPropCatLabel().equalsIgnoreCase(catToFind)) {
                ret.add(mapEntry.getKey().getPropLabel());
            }
        }
        
        return ret;
    }
    
    /**
     * This method accepts a User and a Movie and then finds the cosine similarity of 
     * that User's profile with the movie. It is mostly used for quick predictions or 
     * for calculating a ScoreToRating. 
     * 
     * @param user The User whose profile we are to use
     * @param movie The movie with which to compute the cosine similarity of the profile
     * @return similarity The cosine similarity of the given User's profile with the given Movie's properties
     */
    @Override
    public double predictMovie(User user, Movie movie) {
        //PropertiesHash movieProps = PropertiesHash.getCriticalProps(movie.getProperties());
        PropertiesHash movieProps = movie.getProperties();
        double similarity = (this.qzrUsers.get(user.getId()).getProfile().cosSimilarity(movieProps));
        
        return similarity;        
    }
    
    /**
     * Returns the QueryZoneUser for a given User
     * @param usr The User whose QueryZoneUser object we wish to find
     * @return The QueryZoneUser for the given User
     */
    private QueryZoneUser getQzrUser(User usr) { 
        return qzrUsers.get(usr.getId());
    }
         
    /**
     * This method creates, for each User in the passed HashMap, a LinkedList of Predictions. These are pulled from the 
     * User's test ratings. Predictions are generated for all Movies in the test set and then it is up to the 
     * Evaluators to choose the top-k Predictions. The Predictions are sorted for each User. 
     * 
     * @param userHash The Users for whom we wish to generate Predictions
     * @return allPredictions A HashMap containing the (sorted) Predictions for each User
     */
    @Override
     public HashMap<User, LinkedList<Prediction>> predict(HashMap<Integer, User> userHash) {
         HashMap<User, LinkedList<Prediction>> allPredictions = new HashMap<>(); // The returned HashMap with the predictions for all Users
        
        LinkedList<Prediction> userPredictions; // A LinkedList to store the predictions for each User
        
        
        // For each QZRUser:
        for(Map.Entry<Integer,User> usr : userHash.entrySet()) {

            userPredictions = new LinkedList<>();
            QueryZoneUser currentUser = getQzrUser(usr.getValue()); // The QZRUser object with the User's profile

            
            for(Rating movieEntry : currentUser.getUser().getTestRatings()) {
               
                PropertiesHash userProfile = currentUser.getProfile();
                PropertiesHash movieProps = movieEntry.getMovie().getProperties();
                
                double sim = movieProps.cosSimilarity(userProfile);
                userPredictions.add(new Prediction(movieEntry.getMovie(), sim));
            }
            userPredictions.sort(null); // Sort the Predictions (using the compareTo method of Prediction)
            allPredictions.put(currentUser.getUser(), userPredictions);
        } // For each QZRUser in qzrUsers
        
            
        return allPredictions;
        
    }
     
     /**
      * This class is an extension of the User class, with some additional data 
      * members and methods.
      */
     final class QueryZoneUser {
         User user;
         PropertiesHash userProfile;
         public QueryZoneUser(User usr) {
             this.user = usr;
             userProfile = new PropertiesHash();
             createProfile();
         }
         
         void createProfile() {
            PropertiesHash qzrProfile = this.userProfile;
            PropertiesHash relevant = new PropertiesHash(); // Keeps track of the relevant Movies
            PropertiesHash nonrelevant = new PropertiesHash();  // Keeps track of the non-relevant Movies
            
            for(Rating rating : user.getTrainingRatings()) { // For each Movie the User rated
                //PropertiesHash moviePropVec = PropertiesHash.getCriticalProps(DBManager.movies.get(rating.getMovie().getId()).getProperties());
                PropertiesHash moviePropVec = DBManager.movies.get(rating.getMovie().getId()).getProperties();
               
                
                if(rating.getRating() >= user.getAvgRating() ) { // The Movie was relevant
                    relevant.add(moviePropVec);
                }

            }
            
            // We have to do this in two loops, otherwise the "relevant" vector might so far be empty so that when we're 
            // computing the similarity, we would always get 0.0, meaning nothing would get added to the "relevant"
            // vector and we would lose valuable information!
            
            
            // Get the top-k genres of the user to know whether it matters that this movie was rated poorly
           
            ArrayList<String> topGenres = findTopKGenres(relevant,numTopKGenres);
            
            // Now to check for movies which were not obviously relevant but which might fall within the query zone
            for(Rating rating : user.getTrainingRatings()) {
                if(rating.getRating() < user.getAvgRating()) { // The User didn't like the Movie...
                    PropertiesHash allMovieProps = DBManager.movies.get(rating.getMovie().getId()).getProperties();
                    //PropertiesHash movieCritProps  = PropertiesHash.getCriticalProps(allMovieProps);
                    PropertiesHash movieCritProps = allMovieProps;
                    
                    double similarity = relevant.cosSimilarity(movieCritProps); // This is what would cause problems when trying to do this in one go (it would be empty)

                    
                    if(similarity >= similarityThreshold ) { // But the Movie is similar to the movies they DID like...
                        // If the movie is in the same genre as one of the user's "favorites", take this into consideration
                        // by subtracting it from the non-relevant PropertiesHash
                        boolean movieIsATopGenre = false;
                        for(String aGenre : topGenres) {
                            if(this.containsPropertyLabel(movieCritProps, aGenre)){
                                movieIsATopGenre = true;
                                break;
                            }
                        }
                        
                        // Our query zone is the top genres. If they didn't like the movie (rating <= 2) but it was similar to
                        // movies that they usually DO like (similarity >= similarityThreshhold) and it was 
                        // not in their normal top genre, this is relevant information. Subtract it from their profile (i.e. add to nonrelevant vector)!
                        if(movieIsATopGenre) {
                            nonrelevant.add(allMovieProps);
                        }
                    }
                    // Notice that we don't do anything if the Movie was in one of the user's top genres
                } // end rating <= 2
               
            } 
           
            // Now we've built up the relevant and non-relevant profiles for this QZRUser. 
            // Calculate the final profile and add it to the list of all profiles
            //relevant.divide(relCount);
            relevant.multiply(beta);
            //nonrelevant.divide(nonRelCount);
            nonrelevant.multiply(gamma);
            qzrProfile.multiply(alpha);
            qzrProfile.add(relevant);
            qzrProfile.subtract(nonrelevant);
            qzrProfile.normalize();
            this.setQzrProfile(qzrProfile);
            
         }
         
        
         /**
          * This method sets the profile of the given QueryZoneUser
          * 
          * @param qzrProfile The PropertiesHash to set to this.userProfile
          */
        private void setQzrProfile(PropertiesHash qzrProfile) {
            if(qzrProfile != null) {
                this.userProfile = qzrProfile;
            }
            else {
                throw new NullPointerException("Null pointer error from setQZRProfile!");
            }
        }
        
        // GETTERS
        private Integer getId() {
            return this.user.getId();
        }

        public User getUser() {
            return this.user;
        }

        public PropertiesHash getProfile() {
            return this.userProfile;
        }
        
        /**
        * Whether or not the passed properties HashMap contains a Property with the given propLabel (ignores case)
        * 
        * @param movieCritProps The PropertiesHas containing the critical Properties
        * @param propLabel A label (generally "genre")
        * 
        * @return Found or not
        */
        private boolean containsPropertyLabel(PropertiesHash movieCritProps, String propLabel) {
            HashMap<Property,Double> properties = movieCritProps.getProperties();
            for(Map.Entry<Property, Double> mapEntry : properties.entrySet()) {
                if(mapEntry.getKey().getPropLabel().equalsIgnoreCase(propLabel)) {
                    return true;
                }
            }
        
        return false;
        }
     }
}

