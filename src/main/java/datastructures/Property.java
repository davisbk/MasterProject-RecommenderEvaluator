
package datastructures;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * A container data structure for a property. Has a category label (e.g. genre, starring, director, etc.)
 * and a label (e.g. action, Elijah Wood, Peter Jackson, etc.). 
 * 
 * @author Juan David Mendez
 */
public class Property {
    
    final String propCatLabel;  // The categorical name of the Property (e.g. "genre", "director", etc.)
    final String propLabel; // The particular name of the Property (e.g. "action", "Tarantino", etc.)
        
    /**
     * "Standard" constructor with category and label parameters.
     * 
     * @param propertyCategoryLabel Category of the property, e.g. "starring"
     * @param propertyLabel Name of the property, e.g. "Will Smith"
     * 
     */
    public Property(String propertyCategoryLabel, String propertyLabel) {//, Double value)    {
        this.propCatLabel = propertyCategoryLabel;
        this.propLabel = propertyLabel;        
    }
        
    /**
     * Creates a property by copying another property. 
     * @param property to copy values from.
     */
    Property(Property property) {
        this.propCatLabel = property.propCatLabel;
        this.propLabel = property.propLabel;
       
    }
    
    /**
     * Default constructor should not be called, so it is made private
     */
    private Property() {
        throw new RuntimeException("Property constructor Property() should not be called.");
    }
    
    
    /**
     * Get the property category label
     * 
     * @return the category label of a property, i.e. Actor
     */
    public String getPropCatLabel() {
        return propCatLabel;
    }

    /**
     * Gets the property label
     * 
     * @return the label of the property, i.e. Will Smith
     */
    public String getPropLabel() {
        return propLabel;
    }
   
    /**
     * Creates a string representation of this Property
     * 
     * @return A String representation of this Property
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");        
        result.append(this.getClass().getName()).append(" {").append(NEW_LINE);
        result.append(" Property: ").append(propCatLabel).append(NEW_LINE);
        result.append(" PropertyName: ").append(propLabel).append(NEW_LINE);        
        result.append("}");        
        return result.toString();
    }
    
    /**
     * Overrides the Object.equals method for Property objects
     * @param obj The object to compare this Property to
     * @return True if both properties are the same or equal in category and label.
     */
    @Override
    public boolean equals(Object obj)  {
        if(obj == null)
            return false;
        if(getClass() != obj.getClass())
            return false;
        if(this == obj)
            return true;
                
        Property rhs = (Property) obj;
        return new EqualsBuilder().
            append(propCatLabel, rhs.propCatLabel).
            append(propLabel, rhs.propLabel).
            isEquals();
    }
    
    /**
     * Creates a hashCode for this Property
     * @return hashCode of the property.
     */
    @Override
    public int hashCode()   {
        return new HashCodeBuilder(17, 31). 
            append(propCatLabel).
            append(propLabel).
            toHashCode();
    }    
}
