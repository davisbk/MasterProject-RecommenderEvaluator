package datastructures;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * A data structure to represent movie ratings.
 * 
 * @author Juan David Mendez
 * 
 */

final public class Rating implements Comparable {
    final private Movie movie;
    final private int rating, timestamp;
            
    /**
     * Standard constructor
     * 
     * @param movie data structure of the movie being rated.
     * @param rating Rating given to the movie.
     * @param timestamp Timestamp of when the rating was given.
     */
    public Rating(Movie movie, int rating, int timestamp)  {
        this.movie = movie;
        this.rating = rating;
        this.timestamp = timestamp;
    }
    
    /**
     * Generates a String representation of this Rating object
     * @return A String representation of this Rating object
     */
    @Override 
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(this.getClass().getName()).append(" {");
        result.append("Movie: ").append(movie);
        result.append(" Rating: ").append(rating);
        result.append(" Timestamp: ").append(timestamp);
        result.append("}");
        return result.toString();
    }

    /**
     * Get this Rating's Movie member object
     * 
     * @return Movie data structure.
     */
    public Movie getMovie() {
        return movie;
    }

    /**
     * Gets this Rating's rating score
     * 
     * @return The value of the rating
     */
    public int getRating() {
        return rating;
    }

    /**
     * Get method for the timestamp related to the rating
     * 
     * @return Timestamp
     */
    public int getTimestamp() {
        return timestamp;
    }
    
    /**
     * For sorting Collections of Ratings. Sorts them in descending order by rating value
     * 
     * @param t The object to compare this Rating to
     * @return 
     */
    @Override
    public int compareTo(Object t) {
        if (this.rating == ((Rating) t).rating)
            return 0;
        else if ((this.rating) < ((Rating) t).rating)
            return 1;
        else
            return -1;
    }
    
    /**
     * Method to compare two Ratings
     * 
     * @param obj Other rating to compare this Rating to
     * @return True if both ratings are the same or contain the same values.
     */
    @Override
    public boolean equals(Object obj) {
        if(obj == null)
            return false;
        if(getClass() != obj.getClass())
            return false;
        if(this == obj)
            return true;
        
        Rating rhs = (Rating) obj;
        return new EqualsBuilder().
            append(movie, rhs.movie).
            append(rating, rhs.rating).
            append(timestamp, rhs.timestamp).
            isEquals();
    }
    
    /**
     * Generates a hashCode of this Rating 
     * @return hashCode for this Rating
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).
            append(movie).
            append(rating).
            append(timestamp).
            toHashCode();
    }
      
}


