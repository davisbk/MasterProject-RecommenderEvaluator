
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
import utils.Printer;
import utils.Settings;

/**
 * BASED ON: An adaptive algorithm for learning changes in user interests 
 * http://dl.acm.org/citation.cfm?id=319950.323230
 * Modifies the Rocchio algorithm by creating 3 profiles for each user, long term,
 * positive short term, and negative short term. Each new item will change either
 * of the short term profiles depending to which is most similar, and the long term profile.
 * @author Juan David Mendez
 * @date 08/05/2017
 */
public class ShortLong implements RecommenderInterface {
    HashMap<Integer, ShortUser> shortUsers = new HashMap<>();
    private final Printer printer = Printer.getCurrentPrinter();

    public ShortLong() {
        printer.print(3, "Starting: " + this.getClass());
    }
    
    /**
     * Default constructor
     * @param userHash list of users for whom we build profiles
     */
    public ShortLong(HashMap<Integer, User> userHash) {
        for(Map.Entry<Integer, User> userEntry : userHash.entrySet()) {
            ShortUser shortUser = new ShortUser(userEntry.getValue());
            shortUsers.put(shortUser.getId(), shortUser);
        }
    }
    
    /**
     * Predicts the score of a movie for a user.
     * @param user to use to predict value
     * @param movie to predict value for
     * @return value of the prediction.
     */
    @Override
    public double predictMovie(User user, Movie movie) {      
        //ShortUser shortUser = getUser(user);
        ShortUser shortUser = shortUsers.get(user.getId());
        double simiLong = shortUser.getLongTerm().dotProduct(movie.getProperties());
        double simiPos = shortUser.getShortPositive().dotProduct(movie.getProperties());
        double simiNeg = shortUser.getShortNegative().dotProduct(movie.getProperties());
        double result = Math.max(simiLong, simiPos) + Math.min(simiLong, simiNeg);
        return result;
    }

    /**
     * Predicts ratings for a list of users.
     * @param users to predict values for
     * @return Hashmap of users and predictions.
     */
    @Override
    public HashMap<User, LinkedList<Prediction>> predict(HashMap<Integer, User> users) {
        /*for(Map.Entry<Integer, User> userEntry : users.entrySet())  {
            ShortUser shortUser = getUser(userEntry.getValue());
            shortUsers.put(shortUser.getId(), shortUser);
        }*/
        HashMap<User, LinkedList<Prediction>> predictions = new HashMap<>();
        LinkedList<Prediction> currentUserPreds; 
        //for each user
        for(Map.Entry<Integer,User> currentUser : users.entrySet()) {
            currentUserPreds = new LinkedList<>();
            ShortUser shortUser = shortUsers.get(currentUser.getKey());
            // For each movie in the testSet
            for(Rating rating : shortUser.getUser().getTestRatings())    {
                Movie movie = rating.getMovie();
                PropertiesHash movieProps = movie.getProperties();
                double simiLong = shortUser.getLongTerm().cosSimilarity(movieProps);
                double simiPos = shortUser.getShortPositive().cosSimilarity(movieProps);
                double simiNeg = shortUser.getShortNegative().cosSimilarity(movieProps);
                currentUserPreds.add(new Prediction(movie, Math.max(simiLong, simiPos) + Math.min(simiLong, -simiNeg)));
            }
            currentUserPreds.sort(null); // Sort the Predictions (using the compareTo method of Prediction)
            predictions.put(shortUser.getUser(), currentUserPreds);
        }
        return predictions;
    }
    private ShortUser getUser(User user)   {
        ShortUser shortUser = shortUsers.get(user.getId());
            if(shortUser==null)   {
                shortUser = new ShortUser(user);
                shortUsers.put(shortUser.getId(), shortUser);
            }
        return shortUser;
    }
}
/**
 * User data structures needed to predict using ShortLong algorithm.
 * @author Juan David Mendez
 */
class ShortUser   {
    double alpha = Settings.getRocchioParams().get("alpha");
    User user;
    int totalCount = 0;
    PropertiesHash longTerm; 
    PropertiesHash shortPositive; 
    PropertiesHash shortNegative; 
    
    
    ShortUser(User user)   {
        this.user = user;
        longTerm = new PropertiesHash();
        shortPositive = new PropertiesHash();
        shortNegative = new PropertiesHash();
        createProfile();
    } 
    
    /*
    * Creates a profile for a user using the Rocchio algorithm.
    * @param user For which user a profile must be made.
    */
    private void createProfile()   {
        //for each rating of the user
        for(Rating rating : user.getTrainingRatings())  {
            PropertiesHash propertyList = rating.getMovie().getProperties();            
            //If the rating is lower than the user average
            //add it to the short term negative + long profile
            if(rating.getRating() <= user.getAvgRating()) {
                addShortNegative(propertyList);
            }
            //Else added to the short positive + long profile
            else{
                addShortPositive(propertyList);
            }   
            shortNegative.multiply(alpha);
            shortPositive.multiply(1-alpha);
        }
    }
    
    /**
     * 
     * @return 
     */
    User getUser() {
        return user;
    }

    /**
     * 
     * @return 
     */
    PropertiesHash getLongTerm() {
        return longTerm;
    }
    
    /**
     * 
     * @return 
     */
    PropertiesHash getShortPositive() {
        return shortPositive;
    }
    
    /**
     * 
     * @return 
     */
    PropertiesHash getShortNegative() {
        return shortNegative;
    }
    
    /**
     * 
     * @return 
     */
    public void addShortNegative(PropertiesHash propertyList) {  
        totalCount++;               
        shortNegative.add(propertyList);
        propertyList.divide(totalCount);
        longTerm.add(propertyList);
    }
    
    /**
     * 
     * @return 
     */
    public void addShortPositive(PropertiesHash propertyList) { 
        totalCount++;   
        shortPositive.add(propertyList);
        propertyList.divide(totalCount);
        longTerm.add(propertyList);
    }

    /**
     * 
     * @return 
     */
    Integer getId() {
        return user.getId();
    }
   
    /**
     * 
     * @return 
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).
            append(user).
            append(longTerm).
            append(shortPositive).
            append(shortNegative).
            toHashCode();
    }

    /**
     * 
     * @return 
     */
    @Override
    public boolean equals(Object obj) {
       if (!(obj instanceof ShortUser))
            return false;
        if (obj == this)
            return true;

        ShortUser rhs = (ShortUser) obj;
        return new EqualsBuilder().
            append(user, rhs.user).
            append(longTerm, rhs.longTerm).
            append(shortPositive, rhs.shortPositive).
            append(shortNegative, rhs.shortNegative).
            isEquals();
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        result.append(this.getClass().getName()).append(" {").append(NEW_LINE);
        result.append(" User: ").append(user).append(NEW_LINE);
        result.append(" LongTerm: ").append(longTerm).append(NEW_LINE);
        result.append(" ShortPositive: ").append(shortPositive).append(NEW_LINE);
        result.append(" ShortNegative: ").append(shortNegative).append(NEW_LINE);
        result.append("}");
        
        return result.toString();
    }
}