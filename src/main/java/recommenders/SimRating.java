package recommenders;

import datastructures.Movie;
import datastructures.Prediction;
import datastructures.PropertiesHash;
import datastructures.Rating;
import datastructures.User;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import utils.Printer;
import utils.Settings;

/**
 * BASED ON: Improving Rocchio Algorithm for Updating User Profiles 
 * in Recommender Systems
 * http://link.springer.com/chapter/10.1007/978-3-642-41230-1_14
 * Modifies the Rocchio Algorithm by using a factor of similarity between a movie 
 * and the user profile when updating the user profile. If the movie is very
 * similar the profile should change drastically and if the movie is not that
 * similar the profile should not change much. 
 * @author Juan David Mendez
 * @date Jan 21, 2016
 */

public class SimRating implements RecommenderInterface { 
    private final Printer printer = Printer.getCurrentPrinter();
    private final HashMap<Integer, SimUser> simUsers = new HashMap<>();
    
    
    /**
     *
     */
    public SimRating ()   {        
        printer.print(3, "Starting: " + this.getClass());
    }

    /**
     * Default constructor
     * @param userHash list of users for whom we build profiles
     */
    public SimRating(HashMap<Integer, User> userHash) {
        for(Map.Entry<Integer, User> userEntry : userHash.entrySet()) {
            SimUser simRatingUser = new SimUser(userEntry.getValue());
            simUsers.put(simRatingUser.getId(), simRatingUser); 
        }
    }
    

    /**
     * Predicts values using the Similarity rating algorithm for a list of users
     * @param users To predict movies for
     * @return Hashmap of users and predictions bases on them.  
     */
    @Override
    public HashMap<User, LinkedList<Prediction>> predict(HashMap<Integer, User> users) {
        /*
        for(Map.Entry<Integer, User> userEntry : users.entrySet())  {
            SimUser simUser = getUser(userEntry.getValue());
            simUsers.put(simUser.getId(), simUser);
        }*/
        
        HashMap<User, LinkedList<Prediction>> userPredHash = new HashMap<>();
        //for each user
        for(Map.Entry<Integer, User> currentUser : users.entrySet()) {
            SimUser currentSimUser = simUsers.get(currentUser.getKey());
            LinkedList<Prediction> currentUserPreds = new LinkedList<>();
            // For each movie in the testSet
            for(Rating rating : currentSimUser.getUser().getTestRatings())    {
                Movie movie = rating.getMovie();
                double similarity = currentSimUser.getProfile().dotProduct(movie.getProperties()); 
                currentUserPreds.add(new Prediction(movie, similarity));
                }
            currentUserPreds.sort(null); // Sort the Predictions (using the compareTo method of Prediction)
            userPredHash.put(currentSimUser.getUser(), currentUserPreds);            
        }
        return userPredHash;
    }

    @Override
    public double predictMovie(User user, Movie movie) {
        SimUser sRR = getUser(user);
        //SimUser sRR = simUsers.get(user.getId());
        return  sRR.simRatingProfile.dotProduct(movie.getProperties()); 
    }
    
    private SimUser getUser(User user)   {
        SimUser simUser = simUsers.get(user.getId());
            if(simUser==null)   {
                simUser = new SimUser(user);
                simUsers.put(simUser.getId(), simUser);
            }
        return simUser;
    }
}
/**
 * User datastructure needed to predict using the Similarity rating algorithm.
 * @author Juan David Mendez
 */
class SimUser   {
    double alpha = Settings.getRocchioParams().get("alpha");
    double beta = Settings.getRocchioParams().get("beta");
    double gamma = Settings.getRocchioParams().get("gamma");
    User user;
    PropertiesHash simRatingProfile; 
    
    SimUser(User user)   {
        this.user = user;
        simRatingProfile = new PropertiesHash();
        createProfile();
    }
    
    User getUser() {
        return user;
    }
    
    PropertiesHash getProfile() {
        return simRatingProfile;
    }
    
    Integer getId() {
        return user.getId();
    }
    
    /**
     * Creates a profile for a user using the Rocchio algorithm.
     */
    private void createProfile()   {
        int relCount = 0;
        int notrelCount = 0;
        int i =0;
        PropertiesHash rel = new PropertiesHash();
        PropertiesHash notrel = new PropertiesHash();
        //for each rating of the user
        for(Rating rating : user.getTrainingRatings())  {
            PropertiesHash propertyList = rating.getMovie().getProperties();
            if(rating.getRating() <= user.getAvgRating()) {
                notrelCount++;
                if(i>4) {
                    propertyList.multiply(similiarityFactor(propertyList));
                    propertyList.multiply(ratingFactor(rating));
                }
                notrel.add(propertyList);
            }
            else{
                relCount++;
                if(i>4) {
                    propertyList.multiply(similiarityFactor(propertyList));
                    propertyList.multiply(ratingFactor(rating));
                }
                rel.add(propertyList);
            }
            i++;
        }
        if(relCount>0)
            rel.divide(relCount);
        rel.multiply(beta);
        if(notrelCount>0)
            notrel.divide(notrelCount);
        notrel.multiply(gamma);
        simRatingProfile.multiply(alpha);
        simRatingProfile.add(rel);
        simRatingProfile.subtract(notrel);
    }
    
    /**
     * Calculates the similarity factor by comparing how similar a movie is to
     * the profile of the user. The more similar the movie and the user profile 
     * are the bigger the factor should be.
     * @param movieProperties Movie to compare to the user profile
     * @return factor that maps to how similar the user and the movie are.
     */
    private double similiarityFactor(PropertiesHash movieProperties)    {
        double result = this.simRatingProfile.dotProduct(movieProperties);
        return 1 + Math.pow(result, 2);
    }
    
    /**
     * Calculates the rating factor by comparing the rating given to a movie
     * with the average rating. The more extreme the rating give the bigger the 
     * factor should be.
     * @param rating
     * @return a factor that maps to how extreme the rating is.
     */
    private double ratingFactor(Rating rating)    {
        double max = Settings.getMaxRating();
        double min = Settings.getMinRating();
        double result = rating.getRating() - (min + max)/2;
        return 1 + Math.pow(result, 2);
    }
    
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        
        result.append(this.getClass().getName()).append(" {").append(NEW_LINE);
        result.append(" User: ").append(user).append(NEW_LINE);
        result.append(" Profile: ").append(simRatingProfile).append(NEW_LINE);
        result.append("}");
        
        return result.toString();
    }
}
