package datastructures;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import utils.Printer;

/**
 * This class is used to store the result of running an evaluation. It is meant to simplify
 * the code to output results, as well as to store the results for finding the optimal
 * values. 
 * 
 * @author Brian Davis
 */
public class Result {
    private double ndcg;
    private double f1;
    private double mae;
    private double map;
    private double mrr;
    private double prec;
    private double  rmse;
    private double recall;
    private Timestamp timestamp;
    
    static DecimalFormat threeForm = new DecimalFormat("0.000"); // A DecimalFormat object to format the output to have 3 decimal places
    
    /**
     * An enumeration to keep track of which type of evaluator value is being passed among methods
     */
    public enum EvalType {
        NDCG, F1, MAE, MAP, MRR, PREC, RMSE, RECALL
    }
    
    /**
     * Default constructor
     */
    public Result() {
        
    }
    
    /**
     * Sets the value specified by eval to the value specified by val
     * @param eval The type of Evaluator whose value in the Result we are setting
     * @param val  The value to be set
     */
    public void set(EvalType eval, double val) {
        if(null == eval) {
            throw new RuntimeException("Tried to set invalid Result value!");
        }
        else switch (eval) {
            case NDCG:
                this.ndcg = val;
                break;
            case F1:
                this.f1 = val;
                break;
            case MAE:
                this.mae = val;
                break;
            case MAP:
                this.map = val;
                break;
            case MRR:
                this.mrr = val;
                break;
            case PREC:
                this.prec = val;
                break;
            case RMSE:
                this.rmse = val;
                break;
            case RECALL:
                this.recall = val;
                break;
            default:
                throw new RuntimeException("Tried to set invalid Result value!");
        }
    }
    
   
    /**
     * Sets the Timestamp value for this Result
     * @param timestamp The Timestamp value to use for this Result's timestamp member
     */
    public void set(Timestamp timestamp) {
        this.timestamp = timestamp;        
    }
    /**
     * Returns a String representation of this Result. 
     * @return A String representation of this Result
     */
    //NDCG	MAE	RMSE	MAP	Prec	Recall	F1	MRR	timestamp
    @Override
    public String toString() {
        String ret = threeForm.format(ndcg) + "\t\t" + threeForm.format(mae) + "\t\t" + threeForm.format(rmse) + "\t\t";
        ret += threeForm.format(map) + "\t\t" + threeForm.format(prec) + "\t\t" + threeForm.format(recall) + "\t\t" + threeForm.format(f1) + "\t\t";
        ret += threeForm.format(mrr) + "\t\t[" + timestamp+"]";
        
        return ret;
    }
    
    // Getters
    public double getNDCG() {
        return this.ndcg;
    }
    
    public double getF1() {
        return this.f1;
    }
    
    public double getMAE() {
        return this.mae;
    }
    
    public double getMAP() {
        return this.map;
    }
    
    public double getMRR() {
        return this.mrr;
    }
    
    public double getPrec() {
        return this.prec;
    }
    
    public double getRecall() {
        return this.recall;
    }
    
    public double getRMSE() {
        return this.rmse;
    }
    
    public Timestamp getTimestamp() {
        return this.timestamp;
    }
    
    // These getters must exist in the base class because we need to be able to call them
    // from subclases (e.g. QZRResult, etc.) but we want to be able to keep things general. 
    // They should not be called except for from a subclass, thus why here they throw exceptions.
    public double getAlpha() {
        throw new RuntimeException("Alpha does not exist in Result!");
    }

    public double getBeta() {
        throw new UnsupportedOperationException("Beta does not exist in Result!");
    }
    
    public double getGamma() {
        throw new UnsupportedOperationException("Gamma does not exist in Result!");
    }

    public double getSampPerc() {
        throw new UnsupportedOperationException("Samp_Perc does not exist in Result!");
    }
    
    public double getSimilarity() {
        throw new UnsupportedOperationException("Similarity does not exist in Result!");
    }
    
    public double getNewCat() {
        throw new UnsupportedOperationException("newCat does not exist in Result!"); //To change body of generated methods, choose Tools | Templates.
    }

    public double getDelCat() {
        throw new UnsupportedOperationException("delCat does not exist in Result!"); //To change body of generated methods, choose Tools | Templates.
    }
    
    /**
     * This method prints out the counts for the various Evaluators. Used when printing
     * out statistics about the results.
     * 
     * @param anArray An ArrayList containing all the Results to print
     * @param loopCounter The number of iterations performed when calculating the Results (so we can provide percentages)
     */
        public static void printCounts(ArrayList<Result> anArray, int loopCounter) {
        if(anArray == null) {
            throw new NullPointerException("Received null pointer in Result.printCounts!");
        }
        if(loopCounter <= 0) {
            throw new RuntimeException ("Invalid value for loopCounter ("+loopCounter+") passed to Result.printCounts!");
        }
        
        Printer printer = Printer.getCurrentPrinter();
        TreeMap<Double, Double> ndcgHash = new TreeMap<>();
        TreeMap<Double, Double> f1Hash = new TreeMap<>();
        TreeMap<Double, Double> maeHash = new TreeMap<>();
        TreeMap<Double, Double> mapHash = new TreeMap<>();
        TreeMap<Double, Double> mrrHash = new TreeMap<>();
        TreeMap<Double, Double> precHash = new TreeMap<>();
        TreeMap<Double, Double> recallHash = new TreeMap<>();
        TreeMap<Double, Double> rmseHash = new TreeMap<>();
        
        
        
        for(Result currentRes : anArray) {
            double ndcg = currentRes.ndcg;
            double f1 = currentRes.f1;
            double mae = currentRes.mae;
            double map = currentRes.map;
            double mrr = currentRes.mrr;
            double prec = currentRes.prec;
            double recall = currentRes.recall;
            double rmse = currentRes.rmse;
            
            // NDCG
            if(ndcgHash.containsKey(ndcg)) {
                double count = ndcgHash.get(ndcg);
                ndcgHash.put(ndcg,++count);
            }
            else {
                ndcgHash.put(ndcg,1.0);
            }
            
            // F1
            if(f1Hash.containsKey(f1)) {
                double count = f1Hash.get(f1);
                f1Hash.put(f1,++count);
            }
            else {
                f1Hash.put(f1, 1.0);
            }
            
            // MAE
            if(maeHash.containsKey(mae)) {
                double count = maeHash.get(mae);
                maeHash.put(mae,++count);
            }
            else {
                maeHash.put(mae,1.0);
            }
            
            // MAP
            if(mapHash.containsKey(map)) {
                double count = mapHash.get(map);
                mapHash.put(map,++count);
            }
            else {
                mapHash.put(map,1.0);
            }
            
            // MRR
            if(mrrHash.containsKey(mrr)) {
                double count = mrrHash.get(mrr);
                mrrHash.put(mrr,++count);
            }
            else {
                mrrHash.put(mrr,1.0);
            }
            
            // Precision
            if(precHash.containsKey(prec)) {
                double count = precHash.get(prec);
                precHash.put(prec,++count);
            }
            else {
                precHash.put(prec,1.0);
            }
            
            // Recall
            if(recallHash.containsKey(recall)) {
                double count = recallHash.get(recall);
                recallHash.put(recall,++count);
            }
            else {
                recallHash.put(recall,1.0);
            }
            
            // RMSE
            if(rmseHash.containsKey(rmse)) {
                double count = rmseHash.get(rmse);
                rmseHash.put(rmse,++count);
            }
            else {
                rmseHash.put(rmse,1.0);
            }
        } // end for all results
        
        // Now we have sorted all of the Results into our TreeMaps and have counts for each value.
        // Output the results!
        
        printer.print(3,"\n--NDCG Count--\n");
        for(Map.Entry<Double, Double> mapEntry : ndcgHash.entrySet()) {
            double tmp = (mapEntry.getValue() / loopCounter) * 100;
            printer.print(3, mapEntry.getKey() + ":\t" + mapEntry.getValue().intValue() + "/" + loopCounter + " = " + threeForm.format(tmp) + "%"+ "\n");
        }
        
        printer.print(3,"\n--F1 Count--\n");
        for(Map.Entry<Double, Double> mapEntry : f1Hash.entrySet()) {
            double tmp = (mapEntry.getValue() / loopCounter) * 100;
            printer.print(3, mapEntry.getKey() + ":\t" + mapEntry.getValue().intValue() + "/" + loopCounter + " = " + threeForm.format(tmp) + "%"+ "\n");       
        }
        
        printer.print(3,"\n--MAE Count--\n");
        for(Map.Entry<Double, Double> mapEntry : maeHash.entrySet()) {
            double tmp = (mapEntry.getValue() / loopCounter) * 100;
            printer.print(3, mapEntry.getKey() + ":\t" + mapEntry.getValue().intValue() + "/" + loopCounter + " = " + threeForm.format(tmp) + "%"+ "\n");
        }
        printer.print(3,"\n--MAP Count--\n");
        for(Map.Entry<Double, Double> mapEntry : mapHash.entrySet()) {
            double tmp = (mapEntry.getValue() / loopCounter) * 100;
            printer.print(3, mapEntry.getKey() + ":\t" + mapEntry.getValue().intValue() + "/" + loopCounter + " = " + threeForm.format(tmp) + "%"+ "\n");
        }
        printer.print(3,"\n--MRR Count--\n");
        for(Map.Entry<Double, Double> mapEntry : mrrHash.entrySet()) {
            double tmp = (mapEntry.getValue() / loopCounter) * 100;
            printer.print(3, mapEntry.getKey() + ":\t" + mapEntry.getValue().intValue() + "/" + loopCounter + " = " + threeForm.format(tmp) + "%"+ "\n");
        }
        printer.print(3,"\n--Precision Count--\n");
        for(Map.Entry<Double, Double> mapEntry : precHash.entrySet()) {
            double tmp = (mapEntry.getValue() / loopCounter) * 100;
            printer.print(3, mapEntry.getKey() + ":\t" + mapEntry.getValue().intValue() + "/" + loopCounter + " = " + threeForm.format(tmp) + "%"+ "\n");
        }
        printer.print(3,"\n--Recall Count--\n");
        for(Map.Entry<Double, Double> mapEntry : recallHash.entrySet()) {
            double tmp = (mapEntry.getValue() / loopCounter) * 100;
            printer.print(3, mapEntry.getKey() + ":\t" + mapEntry.getValue().intValue() + "/" + loopCounter + " = " + threeForm.format(tmp) + "%"+ "\n");
        }
        printer.print(3,"\n--RMSE Count--\n");
        for(Map.Entry<Double, Double> mapEntry : rmseHash.entrySet()) {
            double tmp = (mapEntry.getValue() / loopCounter) * 100;
            printer.print(3, mapEntry.getKey() + ":\t" + mapEntry.getValue().intValue() + "/" + loopCounter + " = " + threeForm.format(tmp) + "%"+ "\n");
        }
    }
    
    /**
     * Overrides the Object.equals method to be used for Result
     * @param obj The object to compare this Result to
     * @return Returns whether the two objects are equal
     */
    @Override
    public boolean equals(Object obj)  {
        if(obj == null)
            return false;
        if(getClass() != obj.getClass())
            return false;
        if(this == obj)
            return true;
        
        Result rhs = (Result)obj;
        
        return new EqualsBuilder()
                .append(ndcg, rhs.ndcg)
                .append(f1, rhs.f1)
                .append(mae, rhs.mae)
                .append(map, rhs.map)
                .append(mrr, rhs.mrr)
                .append(prec, rhs.prec)
                .append(recall, rhs.recall)
                .append(rmse, rhs.rmse)
                .append(timestamp, rhs.timestamp)
                .isEquals();
    }
    
    /**
     * Returns a hashCode for this Result object
     * @return  A hashCode for this Result object
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31)
                .append(ndcg)
                .append(f1)
                .append(mae)
                .append(map)
                .append(mrr)
                .append(prec)
                .append(recall)
                .append(rmse)
                .append(timestamp)
                .toHashCode();
    }
}
