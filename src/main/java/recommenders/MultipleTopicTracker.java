package recommenders;

import datastructures.Movie;
import datastructures.Prediction;
import datastructures.PropertiesHash;
import datastructures.Rating;
import datastructures.User;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import utils.Settings;

/**
 * BASED ON: Tracking Multiple Topics for Finding Interesting Articles
 * dl.acm.org/ft_gateway.cfm?id=1281253
 * Uses multiple profiles for each category a user is interested in, and recommendations
 * are based on the category that is most similar to the item.
 * @author Juan David Mendez
 */
public class MultipleTopicTracker implements RecommenderInterface {
    private final HashMap<Integer, MTTUser> mttUsers = new HashMap<>();

    public MultipleTopicTracker() {
    }
    
    /**
     * Default constructor
     * @param userHash list of users for whom we build profiles
     */
    public MultipleTopicTracker(HashMap<Integer, User> userHash) {
        for(Map.Entry<Integer, User> userEntry : userHash.entrySet()) {
            MTTUser mttUser = new MTTUser(userEntry.getValue());
            mttUsers.put(mttUser.getId(), mttUser);
        }
    }

    /**
     * For each User in users create predictions for the test set.
     * 
     * @param users to create predictions for
     * @return A list with predictions linked to the use for whom they are created.
     */
    @Override
    public HashMap<User, LinkedList<Prediction>> predict(HashMap<Integer, User> users) {  
        HashMap<User, LinkedList<Prediction>> predictions = new HashMap<>();        
        LinkedList<Prediction> scores; 
        
        for(Map.Entry<Integer, User> userEntry : users.entrySet())  {
            MTTUser mttUser = mttUsers.get(userEntry.getKey());
            scores = new LinkedList<>();
            if(mttUser==null)   {
                mttUser = new MTTUser(userEntry.getValue());
                mttUsers.put(mttUser.getId(), mttUser);
            }
            for(Rating rating : mttUser.getUser().getTestRatings())    {
                Movie movie = rating.getMovie();
                double similarity = mttUser.getScore(movie.getProperties());
                scores.add(new Prediction(movie, similarity));
            }
            scores.sort(null);
            predictions.put(mttUser.getUser(), scores); 
        }
        return predictions;
    }

    /**
     * Predicts the score of movie based on the profiles of user
     * @param user to predict movie for
     * @param movie to predict value of
     * @return 
     */
    @Override
    public double predictMovie(User user, Movie movie) {
        MTTUser mttUser = getUser(user);
        return mttUser.getScore(movie.getProperties());
    }
    
    private MTTUser getUser(User user)   {
        MTTUser mttUser = mttUsers.get(user.getId());
            if(mttUser==null)   {
                mttUser = new MTTUser(user);
                mttUsers.put(mttUser.getId(), mttUser);
            }
        return mttUser;
    }
}

    
    /**
    * Data structure specific for the MTT Algorithm.
    * @author Juan David Mendez
    */
    class MTTUser   {
        User user;
        ArrayList<MTTData> categories = new ArrayList<>();
        double threshold = Settings.getMttNewCatThreshold();//Settings.getRocchioParams().get("threshold");
        double catThreshold = Settings.getMttDelCatThreshold();//Settings.getRocchioParams().get("preThreshold");

        /**
         * Creates a MTTUser from a default user datastructure.
         * @param user to be used as template
         */
        
        MTTUser(User user)   {
            
            this.user = user;
            createProfile();
        }
        /**
         * Creates a profile for a user using the MTT algorithm. For each rating
         * looks for the most similar category profile, or create a new empty category. 
         * If the movie is relevant add it category and update precision of the category. 
         * If the movie is not relevant, update precision and delete category if
         * falls from the threshold.
         */ 
        private void createProfile()   {
            for(Rating rating : user.getTrainingRatings())  {
                PropertiesHash movieProperties = rating.getMovie().getProperties();
                MTTData cat = findCat(movieProperties);
                //Category was not found
                if(cat==null)    {
                    //If Movie is relevant then create a new category, else do nothing.
                    if(rating.getRating()>=user.getAvgRating())  {
                        categories.add(new MTTData(movieProperties));
                    }
                }   else    {
                    //Category was found
                    //Add the movie the category profile
                    if(rating.getRating()>=user.getAgeRange())   {
                        cat.addToProfile(movieProperties);           
                        cat.addRelevant();
                    }
                    //Category found but movie not relevant.
                    //If precision of a category has dropped below a threshold then
                    //Remove it
                    else    {
                        cat.addNotRelevant();
                        if(cat.getPrecision()<catThreshold)   {
                            categories.remove(cat);
                        }
                    } 
                }
            }
        }
        
        public double getScore(PropertiesHash movie)   {
            MTTData pMax = findCat(movie);   
            if(pMax == null)
                return 0;
            return pMax.getPrecision() * pMax.getProfile().cosSimilarity(movie);
            
        }
        /**
         * Find which PropertiesHash has the highest similarity to movie, or
         * or returns an empty PropertiesHash.
         * @param movie to find the highest similar PropertiesHash to
         * @return PropertiesHash with highest similarity above the threshold or
         * empty otherwise.
         */
        private MTTData findCat(PropertiesHash movie)  {
            double topSim = 0;
            MTTData topCat = null;
            for(MTTData data : categories)    {
                PropertiesHash category = data.getProfile();
                double temp  = category.cosSimilarity(movie);
                if(temp > threshold && temp > topSim)
                    topCat = data;
            }
            return topCat;
        }

        public User getUser() {
            return user;
        }
        
        public int getId() {
            return user.getId();
        }

        public double getThreshold() {
            return threshold;
        }

        public double getCatThreshold() {
            return catThreshold;
        }

        public void setThreshold(double threshold) {
            this.threshold = threshold;
        }

        public void setCatThreshold(double catThreshold) {
            this.catThreshold = catThreshold;
        }

        public ArrayList<MTTData> getCategories() {
            return categories;
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 31).
                append(user).
                append(categories).
                toHashCode();
        }

        @Override
        public boolean equals(Object obj) {
           if (!(obj instanceof MTTUser))
                return false;
            if (obj == this)
                return true;

            MTTUser rhs = (MTTUser) obj;
            return new EqualsBuilder().
                append(user, rhs.user).
                append(categories, rhs.categories).
                append(threshold, rhs.threshold).
                append(catThreshold, rhs.catThreshold).
                isEquals();
        }

        @Override
        public String toString() {
            StringBuilder result = new StringBuilder();
            String NEW_LINE = System.getProperty("line.separator");
            result.append(this.getClass().getName()).append(" {").append(NEW_LINE);
            result.append(" User: ").append(user).append(NEW_LINE);
            result.append(" Categories: ").append(categories).append(NEW_LINE);
            result.append("}");
            return result.toString();
        }
    }

class MTTData   {
    private PropertiesHash profile = new PropertiesHash();
    private int relevant;
    private int total;
    
    MTTData(){
        relevant = 0;
        total = 0;
    }
    
    MTTData(PropertiesHash profile){
        this.profile = new PropertiesHash(profile);
        relevant++;
        total++;
    }

    public PropertiesHash getProfile() {
        return profile;
    }   
    
    public void addRelevant()   {
        relevant++;
        total++;
    }
    public void addNotRelevant()   {
        total++;
    }

    public int getRelevant() {
        return relevant;
    }

    public int getTotal() {
        return total;
    }  
    
    public double getPrecision() {
        if(total==0)
            return 0;
        BigDecimal numerator = new BigDecimal(relevant + "");
        BigDecimal denominator = new BigDecimal(total + "");
        BigDecimal total = numerator.divide(denominator,3, BigDecimal.ROUND_HALF_UP);
        double result = total.doubleValue();
        return result;
    }
    
    public void addToProfile(PropertiesHash movie)  {
        profile.add(movie);        
    }    
    
    @Override
    public int hashCode()   {
        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
            append(profile).
            append(relevant).
            append(total).
            toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MTTData other = (MTTData) obj;
        if (!Objects.equals(this.profile, other.profile)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "MTTData{" + "profile=" + profile + ", relevant=" + relevant + ", total=" + total + '}';
    }
    
    
}
