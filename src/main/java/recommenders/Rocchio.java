package recommenders;

import datastructures.Movie;
import datastructures.Prediction;
import datastructures.PropertiesHash;
import datastructures.Rating;
import datastructures.User;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import utils.Settings;

/**
 * A basic implementation of the Rocchio algorithm. 
 * 
 * @author Juan David Mendez
 */

public class Rocchio implements  RecommenderInterface {
    
    HashMap<Integer, RocchioUser> rocchioUsers = new HashMap<>();
    
    // Should not be called, made private
    private Rocchio()    {
        throw new RuntimeException("\"RocchioRecommender()\" should not be called. Please use \"RocchioRecommender(HashMap<Integer, User> users)\" instead. ");
    }
    
    /**
     * Default constructor for RoccioRecommender. Accepts a HashMap of users and creates profiles for them.
     * @param users The Users for whom we should create a profile
     */
    public Rocchio(HashMap<Integer, User> users) {
        //printer.print(3,"Creating " + this.getClass().getName() + " profiles for " + users.size() + " users...");
        for(Map.Entry<Integer, User> userEntry : users.entrySet())  {
            RocchioUser rocchioUser = new RocchioUser(userEntry.getValue());
            rocchioUsers.put(rocchioUser.getId(), rocchioUser);
        }
    }
    
    /**
     * Predicts movies for a list of users
     * @param users to predict movies for
     * @return List of predictions.
     */
    
     @Override
    public HashMap<User, LinkedList<Prediction>> predict(HashMap<Integer, User> users) { 
        HashMap<User, LinkedList<Prediction>> predictionHash = new HashMap<>(); // The final HashMap with Predictions for each User to be returned
        LinkedList<Prediction> predictionList; // A LinkedList to hold the Predictions for each User
        
        for(Map.Entry<Integer, User> mapEntry : users.entrySet()){ // For each User
            RocchioUser rocchioUser = getRocchioUser(mapEntry.getValue());
            predictionList = new LinkedList<>();
            
            for(Rating currentRating : rocchioUser.getUser().getTestRatings()) {
                PropertiesHash movieProps = currentRating.getMovie().getProperties();
                movieProps.normalize();
                double cosSim = movieProps.cosSimilarity(rocchioUser.getProfile());
                predictionList.add(new Prediction(currentRating.getMovie(), cosSim));
            }
            
            predictionList.sort(null); // Sort the Predictions (using the compareTo method of Prediction)
            predictionHash.put(rocchioUser.getUser(), predictionList);
        }
                
        return predictionHash;
    }
    
    public User getUser(int userID) {
        return rocchioUsers.get(userID).getUser();
    }
    
    /**
     * Predicts the rating for a movie based on one user.
     * @param user To predict rating of movie for
     * @param movie to predict rating
     * @return Value of the prediction.
     */
    @Override
    public double predictMovie(User user, Movie movie) {
        PropertiesHash userProfile = this.getRocchioUser(user).getProfile();
        PropertiesHash movieProperties = movie.getProperties().normalize();
        return userProfile.cosSimilarity(movieProperties);
    }
    
    // GETTERS
    private RocchioUser getRocchioUser(User user) {
        return rocchioUsers.get(user.getId());
    }
    
    public PropertiesHash getRocchioUserProfile(User user) {
        return rocchioUsers.get(user.getId()).getProfile();
    }
}
/**
 * User subclass for storing parameter values for the user's profile
 * 
 * @author Juan David Mendez
 */
class RocchioUser   {
    double alpha = Settings.getRocchioParams().get("alpha");
    double beta = Settings.getRocchioParams().get("beta");
    double gamma = Settings.getRocchioParams().get("gamma");
    User user;
    PropertiesHash rocchioProfile; 
    
    /**
     * Creates a Rocchio user from a default user datastructure.
     * @param user to be used as template
     */
    RocchioUser(User user)   {
        this.user = user;
        rocchioProfile = new PropertiesHash();
        createProfile();
    }
    
    /*
    * Creates a profile for a user using the Rocchio algorithm.
    * @param user For which user a profile must be made.
    */
    private void createProfile()   {
        int relCount = 0;
        int notrelCount = 0;
        PropertiesHash rel = new PropertiesHash();
        PropertiesHash notrel = new PropertiesHash();
        //for each rating of the user
        for(Rating rating : user.getTrainingRatings())  {
            PropertiesHash propertyList = rating.getMovie().getProperties();
                        
            if(rating.getRating() < user.getAvgRating()) {
                notrelCount++;
                notrel.add(propertyList);
            }
            else{
                relCount++;
                rel.add(propertyList);
            }
            
        }
               
       
        //rel.divide(relCount);
        rel.multiply(beta);
        //notrel.divide(notrelCount);
        notrel.multiply(gamma);
        rocchioProfile.multiply(alpha);
        rocchioProfile.add(rel);
        rocchioProfile.subtract(notrel);
        rocchioProfile.normalize();
        
        
    }
    
    /**
     * Sets the RocchioUser's profile
     * 
     * @param rocchProf The PropertiesHash to set this.rocchioProfile
     */
    public void setRocchioProfile(PropertiesHash rocchProf) {
        this.rocchioProfile = rocchProf;
    }
    
    // GETTERS
    User getUser() {
        return this.user;
    }

    PropertiesHash getProfile() {
        return rocchioProfile;
    }

    Integer getId() {
        return user.getId();
    }
    
    /**
     * Returns a hashCode for this RocchioUser object
     * 
     * @return A hashCode for this RocchioUser object
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).
            append(user).
            append(rocchioProfile).
            toHashCode();
    }
    
    /**
     * Overrides the Object.equals method for comparing objects to RocchioUser objects
     * 
     * @param obj The object to compare to this RocchioUser object
     * @return Whether the two objects are equal
     */
    @Override
    public boolean equals(Object obj) {
       if (!(obj instanceof RocchioUser))
            return false;
        if (obj == this)
            return true;

        RocchioUser rhs = (RocchioUser) obj;
        return new EqualsBuilder().
            append(user, rhs.user).
            append(rocchioProfile, rhs.rocchioProfile).
            isEquals();
    }
    
    /**
     * Generates a String representation of this RocchioUser object
     * 
     * @return  A String representation of this RocchioUser object
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        result.append(this.getClass().getName()).append(" {").append(NEW_LINE);
        result.append(" User: ").append(user).append(NEW_LINE);
        result.append(" RocchioProfile: ").append(rocchioProfile).append(NEW_LINE);
        result.append("}");
        
        return result.toString();
    }

}

