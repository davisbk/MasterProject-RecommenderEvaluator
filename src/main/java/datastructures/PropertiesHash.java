package datastructures;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 *  This class represents a vector of Properties for various objects (Movies, Users, user profiles, etc). 
 *  The underlying data structure is a HashMap, which allows for random access of its elements. 
 * 
 *  @author Brian Davis
 */
public class PropertiesHash {

    private final HashMap<Property, Double> properties;
    
    /**
     * Standard constructor
     */
    public PropertiesHash() {
        properties = new HashMap<>();
    }
    
    /**
     * Copy constructor
     * 
     * @param rhs The PropertiesHash to be copied
     */
    public PropertiesHash(PropertiesHash rhs) {
        properties = new HashMap<>(rhs.properties);
    }
    
    /**
     * Copy constructor from existing HashMap of Properties/values
     * 
     * @param props 
     */
    public PropertiesHash(HashMap <Property, Double> props) {
        this.properties = new HashMap<>(props);
    }
    
    /**
     * Adds a Property and its value to the "vector"
     * 
     * @param prop The Property to add to the HashMap
     * @param val  The value for the property
     */
    public void add(Property prop, double val) {
        if(properties.containsKey(prop)) { // If the Property already exists, get its value and increase it by val
            double tmp = properties.get(prop);
            tmp += val;
            properties.put(prop, tmp);
        } else {
            properties.put(prop,val);
        }
    }
    
    /**
     * Adds another PropertiesHash to this PropertiesHash by calling the add(Property,value) method 
     * for each Property in rhs. 
     * 
     * @param rhs 
     */
    public void add(PropertiesHash rhs) {
        for(Map.Entry<Property, Double> mapEntry : rhs.getProperties().entrySet()) {
            this.add(mapEntry.getKey(), mapEntry.getValue());
        }              
        
    }
    
    /**
     * Subtracts the given Property from the PropertiesHash by the given value.
     * If the Property doesn't exist, it is given a negative value (i.e. the 
     * value started off at 0). 
     * 
     * @param prop The Property to be subtracted from this PropertiesHash
     * @param val The amount to subtract
     */
    public void subtract(Property prop, double val) {
        
        if(properties.containsKey(prop)) {
            double tmp = properties.get(prop);
            tmp -= val;
            properties.put(prop, tmp);
        } else {
            properties.put(prop, -val);
        }
        
    }
    
    /**
     * Subtracts the given PropertiesHas from this PropertiesHas (i.e. vector subtraction)
     * by calling the subtract(Property, value) method for each Property in rhs.
     * 
     * @param rhs The PropertiesHash to subtract from this PropertiesHash
     */
    public void subtract(PropertiesHash rhs) {
        for(Map.Entry<Property, Double> mapEntry : rhs.getProperties().entrySet()) {
            this.subtract(mapEntry.getKey(), mapEntry.getValue());
        }
        
    }
    
    /**
     * Multiplies (scales) the Properties in this PropertiesHas by the given value.
     * 
     * @param factor The value by which to scale this PropertiesHash
     */
    public void multiply(double factor) {
        for(Map.Entry<Property, Double> currentProp : properties.entrySet()) {
            double tmp = properties.get(currentProp.getKey());
            tmp *= factor;
            properties.put(currentProp.getKey(), tmp);
        }
    }
    
    /**
     * Divides (scales) this PropertiesHas by the given value.
     * 
     * @param divisor The value by which to divide the Properties in this PropertiesHash
     */
    public void divide(double divisor) {
        if(divisor == 0.0) {
            throw new RuntimeException("Dividing by zero!");
        }
        for(Map.Entry<Property, Double> currentProp : properties.entrySet()) {
            double tmp = properties.get(currentProp.getKey());
            tmp /= divisor;
            properties.put(currentProp.getKey(), tmp);
        }
    }
    
    /**
     * This method calculates the cosine similarity between the two PropertiesHash objects.
     * 
     * 
     * @param rhs The second PropertiesHash against which to find the cosine similarity
     * @return The cosine similarity, a measure between [0,1] of how similar the two "vectors" are
     */
    public double cosSimilarity(PropertiesHash rhs) {
        double numerator = 0, denominator = 0;
        
        PropertiesHash propHashA, propHashB;
        
        // Assign the smaller PropertiesHash to propHashA
        if(this.properties.size() < rhs.properties.size()) {
            propHashA = this;
            propHashB = rhs;
        } else {
            propHashA = rhs;
            propHashB = this;
        }
        
        // Now for each entry in the smaller vector, multiply its value by its equivalent value (if it exists)
        // in the other PropertiesHash
        for(Map.Entry<Property, Double> mapEntry : propHashA.properties.entrySet()) {
            if(propHashB.properties.containsKey(mapEntry.getKey())) {
                numerator += mapEntry.getValue() * propHashB.properties.get(mapEntry.getKey());
            }
        }
        
        // To calculate the denominator we must find the square root of the sum of the squared
        // entries in propHashA multiplied by the same in propHashB
        double rootA = 0.0;
        for(Map.Entry<Property, Double> mapEntry : propHashA.properties.entrySet()) {
            rootA += Math.pow(mapEntry.getValue(),2);
        }
        
        rootA = Math.sqrt(rootA);
        
        // Now for propHashB
        double rootB = 0.0;
        for(Map.Entry<Property, Double> mapEntry : propHashB.properties.entrySet()) {
            rootB += Math.pow(mapEntry.getValue(),2);
        }
        
        rootB = Math.sqrt(rootB);
        
        // Now we can calculate the denominator, which is just the product of these two roots
        denominator = rootA * rootB;
        
        if(denominator == 0) {
            return 0;
        }
        
        return numerator / denominator;
        
    }
    
    /**
     * This method calculates the Euclidean distance between this.properties and rhs.properties.
     * This is defined as:
     * 
     * distance = sqrt(sum((propHash1[i] - propHash2[i])^2))
     * 
     * i.e. the square root of the squared difference between the values in the two properties. If 
     * a property exists in one but not in the other, it is assumed to have a value of zero in the other (i.e. maximum distance).
     * 
     * Our method here is: 
     * 1. Make a copy of the longer "vector", propHashB
     * 2. Iterate over the shorter vector
     *  2a. If the property is in both, find the difference in their values and 
     *      add the square of this to the sum before removing this element from the copy
     *  2b. If the property is only in propHashA, add the square of its value to the sum
     * 3. Iterate over the remaining elements in the copy (propHashB) and add the square of each value to the sum,
     *    as these Properties are not in propHashA. 
     * 
     * @param rhs The second vector from which we wish to calculate the distance.
     * @return The Euclidean distance between the two "vectors"
     */
    public double distance(PropertiesHash rhs) {
       
        HashMap<Property, Double> intersection = new HashMap<>(this.properties);
        intersection.keySet().retainAll(rhs.properties.keySet());
        
        HashMap<Property, Double> onlyInThis = new HashMap<>(this.properties);
        onlyInThis.keySet().removeAll(intersection.keySet());
        
        HashMap<Property, Double> onlyInRHS = new HashMap<>(rhs.properties);
        onlyInRHS.keySet().removeAll(intersection.keySet());
        
        double distance = 0.0;
        
        // Summing up the square of items in both this.properties and rhs.properties
        for(Map.Entry<Property, Double> mapEntry : intersection.entrySet()) {
            double val1 = this.properties.get(mapEntry.getKey());
            double val2 = rhs.properties.get(mapEntry.getKey());
            distance += Math.pow(val1 - val2, 2);
        }
        
        // Summing up the square of items only in this.properties
        for(Map.Entry<Property, Double> mapEntry : onlyInThis.entrySet()) {
            distance += Math.pow(mapEntry.getValue(),2);
        }
        
        // Summing up the square of items only in rhs.properties
        for(Map.Entry<Property, Double> mapEntry : onlyInRHS.entrySet()) {
            distance += Math.pow(mapEntry.getValue(), 2);
        }
        
        distance = Math.sqrt(distance);
        
        return distance;
                
    }
    
    /**
     * This method calculates the dot product of two PropertiesHash objects. The dot product for two
     * vectors A and B of equal length is defined as:
     * 
     * dotProduct = sum(A[i]*B[i]) for all i from 1 to N, where N is the size of the vectors.
     * 
     * In our case we drop the assumption that they are of the same length and if one "vector"
     * does not contain a particular Property it is assumed to have a value of zero (and is
     * consequently not summed up).
     * 
     * @param rhs
     * @return The dot product of this.properties and rhs.properties
     */
    public double dotProduct(PropertiesHash rhs){
        
        HashMap<Property, Double> intersection = new HashMap<>(this.properties);
        intersection.keySet().retainAll(rhs.properties.keySet());
        
        // Now we have just the entries which are in both PropertiesHas objects.
        // We can now perform our multiplication
        double dotProduct = 0.0;
        for(Map.Entry<Property, Double> mapEntry : intersection.entrySet()) {
            double valInThis = this.properties.get(mapEntry.getKey());
            double valInRHS = rhs.properties.get(mapEntry.getKey());
            dotProduct += (valInThis * valInRHS);
        }
        
        return dotProduct;
    }
    
    /**
     * Override of the equals method for our PropertiesHash. Since we don't actually care if
     * the values are the same, we just check that the keySets of the underlying HashMaps
     * are the same (i.e. whether the Properties have the same two Strings).
     * 
     * @param obj The passed object to compare to this for equality
     * @return Whether or not the two objects are equal
     */
    @Override
    public boolean equals(Object obj) {
        if(obj == null)
            return false;
        if(getClass() != obj.getClass())
            return false;
        if(this == obj)
            return true;
        
        PropertiesHash rhs = (PropertiesHash) obj;
        return new EqualsBuilder().
            append(properties.keySet(), rhs.properties.keySet()).
            isEquals();
        
    }
        
    
    /**
     * This method returns the value for a given set of Strings for the given propCatLabel
     * and propLabel. If it's not found, returns NaN
     * 
     * @param catLabel The category label
     * @param label The label
     * @return 
     */ 
    public double getValueByCatAndLabel(String catLabel, String label) {
        Property temp = new Property(catLabel, label);
        if(properties.containsKey(temp)) {
            return properties.get(temp);
        }
        else {
            return Double.NaN;
        }
    }
    
    /**
     * This method generates a hashCode for this PropertiesHash object.  It is based
     * on the keySet of the underlying HashMap
     * 
     * @return The hashCode for this PropertiesHash object
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
            append(properties.keySet()).
            toHashCode();
    }
    
    /**
     * Returns the size of this PropertiesHash (i.e. the underlying HashMap)
     * @return  The size of this PropertiesHash
     */
    public int getSize() {
        return this.properties.size();
    }
    
    /**
     * Returns a new copy of underlying Properties HashMap. 
     * @return a new coyp of the underlying Properties HashMap of this PropertiesHash
     */
    public HashMap<Property, Double> getProperties() {
        return new HashMap<>(this.properties);
    }
    
    /**
     * Given the specified Property, returns the value in the PropertiesHash for that value.
     * 
     * @param prop The Property whose value we want to retrieve
     * @return The value of the specified Property in the underlying HashMap
     */
    public double getPropValue(Property prop) {
        if(properties.containsKey(prop)) {
            return properties.get(prop);
        }
        else {
            return Double.NaN;
        }
    }
    
    /**
     * Normalizes the vector using a standard Euclidean normalization technique. 
     * 
     * @return A reference to this object
     */
    public PropertiesHash normalize() {
        double denom = this.dotProduct(this);
        if(denom == 0) {
            denom = 1;
        }
        denom = Math.sqrt(denom);
        
        for(Map.Entry<Property,Double> mapEntry : this.properties.entrySet()) {
            double tmp = this.properties.get(mapEntry.getKey());
            tmp /= denom;
            properties.put(mapEntry.getKey(), tmp);
        }
        
        return this;
    }
    
    /**
     * An override of the toString method which generates a String representation
     * of this PropertiesHash object.
     * @return This PropertiesHash as a String
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");        
        result.append(this.getClass().getName()).append(" {").append(NEW_LINE);
        result.append(" Properties: ").append(properties).append(NEW_LINE);
        result.append("}");        
        return result.toString();
        
    }
    
    /**
     * This method returns a new PropertiesHash with only the genre, director, and cast information 
     * contained within that PropertiesHash. It was originally used by the QueryZone but is no longer
     * in use. 
     * 
     * @param properties The PropertiesHash whose critical properties (genre, director, cast) we wish to retrieve
     * @return A new PropertiesHash with only genre, director, and cast information
     */
    @Deprecated
    public static PropertiesHash getCriticalProps(PropertiesHash properties) {
        PropertiesHash ret = new PropertiesHash();
        for(Map.Entry<Property,Double> mapEntry : properties.properties.entrySet()) {
            String catLabel = mapEntry.getKey().propCatLabel;
            if(catLabel.equalsIgnoreCase("genre") || catLabel.equalsIgnoreCase("director") || catLabel.equalsIgnoreCase("starring") ) {
                ret.add(mapEntry.getKey(), mapEntry.getValue());
            }
                
        }
        
        return ret;
    }
    
}
