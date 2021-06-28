package datastructures;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/*
 * Data structure to hold information for a movie.
 * @author Juan David Mendez
 * 
 */

final public class Movie implements Comparable {
    
    final private PropertiesHash properties;
    final private int id; //Unique identifier for a Movie
    final private String uri; // The Uniform Resource Identifier for this Movie
    final private String title; // The name of this Movie
    
    /**
     * Constructor
     * @param id The unique identifying number for this Movie
     * @param uri The Uniform Resource Identifier for the new Movie
     * @param title The title of the Movie
     * @param properties Properties for the Movie
     */
    public Movie(int id, String uri, String title, PropertiesHash properties) {
        this.id = id;
        this.uri = uri;
        this.title = title;
        if(properties == null) {
            this.properties = new PropertiesHash();
        } else {
            this.properties = properties;
        }
    }
    
    /**
     * Copy constructor 
     * 
     * @param movie 
     */
    public Movie(Movie movie) {
        this.id = movie.id;
        this.properties = movie.properties;
        this.title = movie.title;
        this.uri = movie.uri;
    }

    /**
     * @return A PropertyVector with the properties and values of the Movie.
     */
    public PropertiesHash getProperties() {
        return new PropertiesHash(properties);
    }

    /**
     *
     * @return the unique identifier of the Movie.
     */
    public int getId() {
        return id;
    }

    /**
     * 
     * @return the URI for the Movie
     */
    public String getUri() {
        return uri;
    }

    /**
     *
     * @return the title of the Movie
     */
    public String getTitle() {
        return title;
    }  
    
    /**
     *
     * @return A String representation of the Movie.
     */
    @Override 
    public String toString() {
        StringBuilder result = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");        
        result.append(this.getClass().getName()).append(" {").append(NEW_LINE);
        result.append(" ID: ").append(id);
        result.append(", Title: ").append(title);
        result.append(", URI: ").append(uri);
        result.append(", Properties: ").append(properties);
        result.append("}").append(NEW_LINE);        
        return result.toString();
    }
    
    /**
     * Override for compareTo to compare two Movie objects. Compares by the Movie's title.
     * 
     * @param t Object to compare a Movie to
     * @return 1, 0, -1 if t's title String is greater than, equal to, or less than this String, ignoring case considerations.
     * 
     */
    @Override
    public int compareTo(Object t) {
        Movie movieToCompareTo = (Movie) t;
        int compare = this.title.compareToIgnoreCase(movieToCompareTo.title);
        if(compare < 0){
            return 1;
        }
        else if(compare > 0 ){
            return -1;
        }
        else{
            return 0;
        }
    }
    
    /**
     *
     * @param obj Object to check if equal to
     * @return returns true if both Movies contain the same information.
     */
    @Override
    public boolean equals(Object obj)  {
        if(obj == null)
            return false;
        if(getClass() != obj.getClass())
            return false;
        if(this == obj)
            return true;
                
        Movie rhs = (Movie) obj;
        return new EqualsBuilder().
            append(id, rhs.id).
            append(uri, rhs.uri).
            append(title, rhs.title).
            isEquals();
    }
    
    /** 
     * Creates a hashCode for a Movie object
     * @return hashCode of the Movie.
     */
    @Override
    public int hashCode()   {
        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
            append(id).
            append(uri).
            append(title).
            toHashCode();
    }
}
