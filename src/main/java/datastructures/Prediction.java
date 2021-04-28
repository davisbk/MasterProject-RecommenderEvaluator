
package datastructures;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Data structure to hold predictions for a Movie
 * @author Juan David Mendez
 * 
 */
final public class Prediction implements Comparable {
    private final Movie movie; // The Movie whose value will be predicted
    private final double value; // The predicted value for this movie

    /**
     * Constructor
     * 
     * @param movie Movie that was predicted
     * @param value Predicted value
     */
    public Prediction(Movie movie, double value) {
        this.movie = movie;
        this.value = value;
    }

    /**
     * @return movie for which the prediction was made
     */
    public Movie getMovie() {
        return movie;
    }

    /**
     * @return value predicted
     */
    public double getValue() {
        return value;
    }

    /**
     *
     * @return string representation of the prediction data structure
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        
        result.append(this.getClass().getName()).append(" {").append(NEW_LINE);
        result.append(" Movie: ").append(movie.getTitle()).append(NEW_LINE);
        result.append(" Value: ").append(value).append(NEW_LINE);
        result.append("}");
        
        return result.toString();
    }    
    
    /**
     * Override of compareTo for comparing two Predictions by their values.
     * 
     * @param t Object to compare this Prediction to
     * @return 0 if value of A is the same, 1 if its less, and -1 if the value greater.
     */
    @Override
    public int compareTo(Object t) {
        if (this.value == ((Prediction) t).value)
            return 0;
        else if ((this.value) < ((Prediction) t).value)
            return 1;
        else
            return -1;
    }    

    /**
     * Compares two predictions for equivalence 
     * 
     * @param obj
     * @return true if they are logically equivalent (same movie, and value)
     */
    @Override
    public boolean equals(Object obj)  {
        if(obj == null)
            return false;
        if(getClass() != obj.getClass())
            return false;
        if(this == obj)
            return true;
        
        
        Prediction rhs = (Prediction) obj;
        return new EqualsBuilder().
            append(movie, rhs.movie).
            append(value, rhs.value).
            isEquals();
    }
    
    /**
     *
     * @return hashCode for the data structure
     */
    @Override
    public int hashCode()   {
        return new HashCodeBuilder(17, 31). 
            append(movie).
            append(value).
            toHashCode();
    }
}
