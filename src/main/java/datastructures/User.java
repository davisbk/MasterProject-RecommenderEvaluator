package datastructures;

import java.util.ArrayList;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Data structure to represent a User with the ratings tied to him/her.
 * 
 */
final public class User implements Comparable {
    
    final private int id, age_range, occupation, zipCode; // The User's unique identifier, their age, occupation, and zip code
    final private String gender; // The User's gender
    private double avgRating; // The average rating this User gives to movies
    
    private ArrayList<Rating> trainingRatings; // The User's training set of Ratings
    private ArrayList<Rating> testRatings; // The User's test set of Ratings
    
    /**
     * Constructor
     * @param id
     * @param gender
     * @param age_range
     * @param occupation
     * @param zipCode
     * @param trainingRatings
     * @param testRatings 
     */
    public User(int id, String gender, int age_range, int occupation, int zipCode, ArrayList<Rating> trainingRatings, ArrayList<Rating> testRatings)  {
        this.id = id;
        this.gender = gender;
        this.age_range = age_range;
        this.occupation = occupation;
        this.zipCode = zipCode;
        this.trainingRatings = trainingRatings;
        this.testRatings = testRatings;
        avgRating = -1;
    }
    
    /**
     * Constructor from existing User with training and test ratings 
     * @param user The User to copy
     * @param trainingRatings The training set to copy
     * @param testRatings  The test set to copy
     */
    public User(User user, ArrayList<Rating> trainingRatings, ArrayList<Rating> testRatings)  {
        this.id = user.id;
        this.gender = user.gender;
        this.age_range = user.age_range;
        this.occupation = user.occupation;
        this.zipCode = user.zipCode;
        this.trainingRatings = trainingRatings;
        this.testRatings = testRatings;
        avgRating = -1;
    }
    
    /**
     * Copy Constructor from existing User
     * @param user The User to copy
     */
    public User(User user)  {
        this.id = user.id;
        this.gender = user.gender;
        this.age_range = user.age_range;
        this.occupation = user.occupation;
        this.zipCode = user.zipCode;
        this.trainingRatings = user.trainingRatings;
        this.testRatings = user.testRatings;
        avgRating = user.avgRating;
    }

    // Getters
    
    public int getId() {
        return id;
    }
    
    public String getGender() {
        return gender;
    }
    
    public int getAgeRange() {
        return age_range;
    }
    
    public int getOccupation() {
        return occupation;
    }

    public int getZipCode() {
        return zipCode;
    }
    
    /**
     * Returns this User's average rating, i.e. the rating they usually give to 
     * movies. It calculates this number if it has not already been done.
     * @return 
     */
    public double getAvgRating() {
        if(avgRating != -1)
            return avgRating;
        else {
            avgRating = calculateAverageRating();
            return avgRating;
        }
    }
            
   
    /**
     * Returns the training set of Ratings for this User.
     * @return The training set of Ratings for this User
     */
    public ArrayList<Rating> getTrainingRatings() {
        ArrayList<Rating> result = new ArrayList<>(trainingRatings);
        return result;
    }
    
    /**
     * Returns the test set of Ratings for this User.
     * 
     * @return The test set of Ratings for this User
     */
    public ArrayList<Rating> getTestRatings() {
       ArrayList<Rating> result = new ArrayList<>(testRatings);
        return result;
    }
    
    /**
     * This method retrieves the Rating value that the User gave to a Movie (found by movie ID).
     * If the Movie was not rated by the User, it returns -1
     * 
     * @param movieID The ID number of the movie whose Rating we are seeking
     * @return  The Rating value of the Movie rated by this User
     */
    public int getTestRatingByID(int movieID) {
        for(Rating rating : this.getTestRatings()) {
            if(rating.getMovie().getId() == movieID){
                return rating.getRating();
            }
        }
        
        return -1;
    }
   
        
    /**
     * Sets the training set for this User. Used only for debugging.
     * 
     * @param trainingRatings 
     */
    @Deprecated
    public void setTrainingRatings(ArrayList<Rating> trainingRatings) {
        this.trainingRatings = trainingRatings;
    }
    
    /**
     * Sets the test set for this User. Used only for debugging.
     * 
     * @param testRatings 
     */
    @Deprecated
    public void setTestRatings(ArrayList<Rating> testRatings) {
        this.testRatings = testRatings;
    }
    /**
     * Calculates the average rating given by this User across all ratings in
     * the User's training set. 
     * 
     * @return the average rating value
     */
    private double calculateAverageRating() {
        int i = 0;
        int total = 0;
        if(!trainingRatings.isEmpty())   {
            for(Rating rating : trainingRatings)  {
                total = total + rating.getRating();
                i++;
            }
        }
        else { // User hasn't rated anything
            i = 1; // To avoid divide by zero errors
        }
        return total/i;
    }
    
    /**
     * For sorting the Users by ID
     * 
     * @param t Other object to compare to.
     * @return True if both Users are the same object or if the have the same id.
     */
    @Override
    public int compareTo(Object t) {
        if (this.id == ((User) t).id)
            return 0;
        else if ((this.id) < ((User) t).id)
            return -1;
        else
            return 1;
    }  
    
    /**
     * Returns a hashCode for this User
     * @return a hashCode for this User
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).
            append(id).
            append(age_range).
            append(occupation).
            append(zipCode).
            append(gender).
            append(avgRating).
            append(trainingRatings).
            append(testRatings).
            toHashCode();
    }

    /**
     * Overrides Object.compare for comparing objects to this User
     * @param obj The object to compare with this User
     * @return Whether the two objects are equal
     */
    @Override
    public boolean equals(Object obj) {
        if(obj == null)
            return false;
        if(getClass() != obj.getClass())
            return false;
        if(this == obj)
            return true;
        
        User rhs = (User) obj;
        return new EqualsBuilder().
            append(id, rhs.id).
            append(age_range, rhs.age_range).
            append(occupation, rhs.occupation).
            append(zipCode, rhs.zipCode).
            append(gender, rhs.gender).
            append(avgRating, rhs.avgRating).
            append(trainingRatings, rhs.trainingRatings).
            append(testRatings, rhs.testRatings).
            isEquals();
    }
    
    /**
     * Generates a String representation of this User.
     * 
     * @return A String representation of this User
     */
    @Override 
    public String toString() {
        StringBuilder result = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        
        result.append(this.getClass().getName()).append(" {").append(NEW_LINE);
        result.append(" ID: ").append(id).append(NEW_LINE);
        result.append(" Gender: ").append(gender).append(NEW_LINE);
        result.append(" Age Range: ").append(age_range).append(NEW_LINE);
        result.append(" Occupation: ").append(occupation).append(NEW_LINE);
        result.append(" ZipCode: ").append(zipCode).append(NEW_LINE);
        result.append(" Ratings: ").append(NEW_LINE).append(trainingRatings).append(NEW_LINE);
        result.append(" Test-Ratings: ").append(NEW_LINE).append(testRatings);
        result.append("}");
        
        return result.toString();
    }
}
