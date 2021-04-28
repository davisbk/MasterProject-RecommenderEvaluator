
package datastructures;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Data structure that represents the range of ratings that a user uses when rating
 * movies. 
 * 
 * @author Juan David Mendez
 * 
 */
public class RatingRange {
    int rating;
    double lowestValue;
    double highestValue; 

    /**
     * Constructor
     * @param rating rating given
     * @param lowestValue Lowest possible rating
     * @param highestValue Highest possible rating.
     */
    public RatingRange(int rating, double lowestValue , double highestValue)   {
        this.rating = rating;
        this.lowestValue = lowestValue;
        this.highestValue = highestValue;
    }

    /**
     * Returns the particular rating
     * @return
     */
    public int getRating() {
        return rating;
    }

    /**
     * Returns the lowest value for this RatingRange
     * 
     * @return The lowest value for this RatingRange
     */
    public double getLowestValue() {
        return lowestValue;
    }

    /**
     * Returns the highest value for this RatingRange
     * @return The highest value for this RatingRange
     */
    public double getHighestValue() {
        return highestValue;
    }

    /**
     * Sets the lowest value for this RatingRange
     * 
     * @param lowestValue The value to assign to lowestValue
     */
    public void setLowestValue(double lowestValue) {
        this.lowestValue = lowestValue;
    }

    /**
     * Sets the highest value for this RatingRange
     * 
     * @param highestValue The value to assign to highestValue
     */
    public void setHighestValue(double highestValue) {
        this.highestValue = highestValue;
    }
    
    /**
     * Override for Object.equals for RatingRange. Determines whether two RatingRanges
     * are equal based on their ratings.
     * 
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj)  {
        if(obj == null)
            return false;
        if(getClass() != obj.getClass())
            return false;
        if(this == obj)
            return true;
        
        RatingRange rhs = (RatingRange) obj;
        return new EqualsBuilder().
            append(rating, rhs.rating).
            isEquals();
    }
    
    /**
     * Returns a hashCode for this RatingRange object. Does not include the min
     * and max ratings, just the rating
     * @return A hashCode for this RatingRange object
     */
    @Override
    public int hashCode()   {
        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
            append(rating).
            toHashCode();
    }    
    
    /**
     * Returns a String representation of this RatingRange object. 
     * 
     * @return A String representation of this RatingRange object
     */
    @Override 
    public String toString() {
        StringBuilder result = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        
        result.append(this.getClass().getName()).append(" {").append(NEW_LINE);
        result.append(" rating: ").append(rating).append(NEW_LINE);
        result.append(" Lowest Value: ").append(lowestValue).append(NEW_LINE);
        result.append(" Highest Value: ").append(highestValue).append(NEW_LINE);
        result.append("}");
        
        return result.toString();
    }
}