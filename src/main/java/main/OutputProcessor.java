package main;

import datastructures.Result;
import datastructures.Result.EvalType;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import main.ShortLongParameterFinder.ShortLongResult;

/**
 * This class is meant to read in an output file from the ParameterFinder mains and find
 * the optimal parameter values. 
 * 
 * @author Brian Davis
 * 
 */
public class OutputProcessor {
    final static String SINGLE_LINE = "---------------------------";
    
    public static void main(String[] args) {
        //String inputFileName = "Rocchio_06-23-2018_20-25-27.txt"; // Rochio
        //String inputFileName = "06-23-2018_16-30-33.txt"; // Pairwise
        //String inputFileName = "06-23-2018_16-43-26.txt"; // QZR
        //String inputFileName = "08-23-2018_22-33-25.txt"; // QZR
        //String inputFileName = "08-24-2018_08-30-25.txt"; // QZR
        //String inputFileName = "08-24-2018_14-15-49.txt"; // MTT
        //String inputFileName = "08-24-2018_15-01-47.txt"; // Pairwise
        //String inputFileName = "08-24-2018_19-50-37.txt"; // SimRating
        //String inputFileName = "08-24-2018_22-33-06.txt"; // ShortLong
        //String inputFileName = "08-24-2018_22-06-29.txt"; // Rocchio
        //String inputFileName = "08-27-2018_17-12-29.txt"; // QZR
        //String inputFileName = "08-29-2018_01-42-29.txt"; // QZR
        String inputFileName = "09-27-2018_17-27-57.txt"; // SimRating
        
                
        OutputProcessor.ResultType resType = OutputProcessor.ResultType.INVALID;
        ArrayList<Result> resArray = new ArrayList<>(); // An ArrayList to store all of the results
        
                
        // Read in the input file
        try {
            BufferedReader reader = new BufferedReader(new FileReader(inputFileName));
            System.out.println("Reading in \"" + inputFileName + "\"...");
            
            String line;
            line = reader.readLine();
            
            switch (line) {
                case "QueryZoneParameterFinder.java":
                    resType = OutputProcessor.ResultType.QZR;
                    System.out.println("Reading in a QueryZoneRecommender result file...");
                    break;
                case "PairwiseParameterFinder.java":
                    resType = OutputProcessor.ResultType.PAIRWISE;
                    System.out.println("Reading in a PairwiseRocchio result file...");
                    break;
                case "RocchioParameterFinder.java":
                    resType = OutputProcessor.ResultType.ROCCHIO;
                    System.out.println("Reading in a RocchioRecommender result file...");
                    break;
                case "MTTParameterFinder.java":
                    resType = OutputProcessor.ResultType.MTT;
                    System.out.println("Reading in a MTT result file...");
                    break;
                case "ShortLongParameterFinder.java": 
                    resType = OutputProcessor.ResultType.SHORTLONG;
                    System.out.println("Reading in a ShortLong result file...");
                    break;
                case "SimRatingParameterFinder.java":
                    resType = OutputProcessor.ResultType.SIMRATING;
                    System.out.println("Reading in a SimRating result file...");
                    break;
                    
                default:
                    break;
            }
            
            // Get to the column headers
            while(!line.equals("Fold 1...")) {
                line = reader.readLine();
            }
            
            line = reader.readLine(); // Skip over the column headers
            
            while(!(line = reader.readLine()).equals("")) {
                Result res = createNewResult(resType, line);
                resArray.add(res);
            }
            
        } catch(FileNotFoundException e) {
            System.out.println("Could not find file \"" + inputFileName + "\". Exiting...");
            System.exit(-1);
            
        }  catch(IOException e) {
            System.out.println("Error reading in file \"" + inputFileName + "\".\n" + e);
        }
        
        // Now we have read in all of the results. Set up a FileWriter to output the best parameters to a file.
        File dir = null;
        final String dirName = "Best Parameters";
        dir = new File(dirName);
        dir.mkdirs();
        String outputFileName = resType.name() + "_" + inputFileName;
        
        PrintWriter out = null;
        try {
            FileWriter fw = new FileWriter(new File(dir,outputFileName));
            BufferedWriter bw = new BufferedWriter(fw);
            out = new PrintWriter(bw,true);
        } catch(IOException e) {
            System.out.println("Error when setting up FileWriter: " + e);
        }
        
        out.write("Best Parameters for " + resType.name() + " file \"" + inputFileName + "\"...");
        
        System.out.println("Reading of file complete.");
        System.out.println("Finding the optimal values and writing to file...");
                
        FindBestParams(resType, resArray, out);
        System.out.print("Done.");
        
        out.flush();
        out.close();
        
    }
    
    /**
     * A method which generates the appropriate Result subclass (e.g. QZRResult)
     * for storing the results of the current run.
     * 
     * @param resType The type of recommender used
     * @param line The actual data from this run
     * @return res - The appropriate Result subclass 
     */
    private static Result createNewResult(OutputProcessor.ResultType resType, String line) {
        double ndcg,mae,rmse,map,prec,recall,f1,mrr;
        String[] vals = line.split("\t\t");
        Result tmp = new Result(); 
        Result ret = new Result();
        if(null != resType) switch (resType) {
            case ROCCHIO:
                double alpha = Double.parseDouble(vals[0]);
                double beta = Double.parseDouble(vals[1]);
                double gamma = Double.parseDouble(vals[2]);
                ndcg = Double.parseDouble(vals[3]);
                mae = Double.parseDouble(vals[4]);
                rmse = Double.parseDouble(vals[5]);
                map = Double.parseDouble(vals[6]);
                prec = Double.parseDouble(vals[7]);
                recall = Double.parseDouble(vals[8]);
                f1 = Double.parseDouble(vals[9]);
                mrr = Double.parseDouble(vals[10]);
                
                tmp.set(EvalType.NDCG,ndcg);
                tmp.set(EvalType.MAE, mae);
                tmp.set(EvalType.RMSE, rmse);
                tmp.set(EvalType.MAP, map);
                tmp.set(EvalType.PREC, prec);
                tmp.set(EvalType.RECALL, recall);
                tmp.set(EvalType.F1, f1);
                tmp.set(EvalType.MRR,mrr);
                                
                ret = new RocchioParameterFinder.RocchioResult(tmp, alpha, beta, gamma);
                break;
            case QZR:
                alpha = Double.parseDouble(vals[0]);
                beta = Double.parseDouble(vals[1]);
                gamma = Double.parseDouble(vals[2]);
                double similarity = Double.parseDouble(vals[3]);
                                
                ndcg = Double.parseDouble(vals[4]);
                mae = Double.parseDouble(vals[5]);
                rmse = Double.parseDouble(vals[6]);
                map = Double.parseDouble(vals[7]);
                prec = Double.parseDouble(vals[8]);
                recall = Double.parseDouble(vals[9]);
                f1 = Double.parseDouble(vals[10]);
                mrr = Double.parseDouble(vals[11]);
                
                tmp.set(EvalType.NDCG,ndcg);
                tmp.set(EvalType.MAE, mae);
                tmp.set(EvalType.RMSE, rmse);
                tmp.set(EvalType.MAP, map);
                tmp.set(EvalType.PREC, prec);
                tmp.set(EvalType.RECALL, recall);
                tmp.set(EvalType.F1, f1);
                tmp.set(EvalType.MRR,mrr);
                ret = new QueryZoneParameterFinder.QZRResult(tmp, alpha, beta, gamma, similarity);
                break;
            case PAIRWISE:
                similarity = Double.parseDouble(vals[0]);
                ndcg = Double.parseDouble(vals[1]);
                mae = Double.parseDouble(vals[2]);
                rmse = Double.parseDouble(vals[3]);
                map = Double.parseDouble(vals[4]);
                prec = Double.parseDouble(vals[5]);
                recall = Double.parseDouble(vals[6]);
                f1 = Double.parseDouble(vals[7]);
                mrr = Double.parseDouble(vals[8]);
                
                tmp.set(EvalType.NDCG,ndcg);
                tmp.set(EvalType.MAE, mae);
                tmp.set(EvalType.RMSE, rmse);
                tmp.set(EvalType.MAP, map);
                tmp.set(EvalType.PREC, prec);
                tmp.set(EvalType.RECALL, recall);
                tmp.set(EvalType.F1, f1);
                tmp.set(EvalType.MRR,mrr);
                ret = new PairwiseParameterFinder.PairwiseResult(tmp,similarity);
                break;
            case MTT:
                double newCat = Double.parseDouble(vals[0]);
                double delCat = Double.parseDouble(vals[1]);
                ndcg = Double.parseDouble(vals[2]);
                mae = Double.parseDouble(vals[3]);
                rmse = Double.parseDouble(vals[4]);
                map = Double.parseDouble(vals[5]);
                prec = Double.parseDouble(vals[6]);
                recall = Double.parseDouble(vals[7]);
                f1 = Double.parseDouble(vals[8]);
                mrr = Double.parseDouble(vals[9]);
                
                tmp.set(EvalType.NDCG,ndcg);
                tmp.set(EvalType.MAE, mae);
                tmp.set(EvalType.RMSE, rmse);
                tmp.set(EvalType.MAP, map);
                tmp.set(EvalType.PREC, prec);
                tmp.set(EvalType.RECALL, recall);
                tmp.set(EvalType.F1, f1);
                tmp.set(EvalType.MRR,mrr);
                                
                ret = new MTTParameterFinder.MTTResult(tmp,newCat, delCat);
                break;
            case SHORTLONG:
                double slAlpha = Double.parseDouble(vals[0]);
                ndcg = Double.parseDouble(vals[1]);
                mae = Double.parseDouble(vals[2]);
                rmse = Double.parseDouble(vals[3]);
                map = Double.parseDouble(vals[4]);
                prec = Double.parseDouble(vals[5]);
                recall = Double.parseDouble(vals[6]);
                f1 = Double.parseDouble(vals[7]);
                mrr = Double.parseDouble(vals[8]);
                
                tmp.set(EvalType.NDCG,ndcg);
                tmp.set(EvalType.MAE, mae);
                tmp.set(EvalType.RMSE, rmse);
                tmp.set(EvalType.MAP, map);
                tmp.set(EvalType.PREC, prec);
                tmp.set(EvalType.RECALL, recall);
                tmp.set(EvalType.F1, f1);
                tmp.set(EvalType.MRR,mrr);
                                
                ret = new ShortLongResult(tmp, slAlpha);
                
                break;
            case SIMRATING:
                double simAlpha = Double.parseDouble(vals[0]); // Have to call these '2' because they're already defined above for Rocchio
                double simBeta = Double.parseDouble(vals[1]);
                double simGamma = Double.parseDouble(vals[2]);
                ndcg = Double.parseDouble(vals[3]);
                mae = Double.parseDouble(vals[4]);
                rmse = Double.parseDouble(vals[5]);
                map = Double.parseDouble(vals[6]);
                prec = Double.parseDouble(vals[7]);
                recall = Double.parseDouble(vals[8]);
                f1 = Double.parseDouble(vals[9]);
                mrr = Double.parseDouble(vals[10]);
                
                tmp.set(EvalType.NDCG,ndcg);
                tmp.set(EvalType.MAE, mae);
                tmp.set(EvalType.RMSE, rmse);
                tmp.set(EvalType.MAP, map);
                tmp.set(EvalType.PREC, prec);
                tmp.set(EvalType.RECALL, recall);
                tmp.set(EvalType.F1, f1);
                tmp.set(EvalType.MRR,mrr);
                                
                ret = new SimRatingParameterFinder.SimRatingResult(tmp, simAlpha, simBeta, simGamma);
                break;
            default:
                break;
        }
        
        return ret;
    }
    
    /**
     * This method scans through the results in resArray and finds the best values
     * for each evaluator. Having found them, it prints them out. 
     * 
     * @param resType The type of recommender used to generate the Results
     * @param resArray The Results from the given ParameterFinder
     * @param out A PrintWriter object for writing to either standard out or a file
     */
    private static void FindBestParams(OutputProcessor.ResultType resType, ArrayList<Result> resArray, PrintWriter out) {
        double bestNDCG = Double.MIN_VALUE;
        double bestMAP = Double.MIN_VALUE;
        double bestPrec = Double.MIN_VALUE;
        double bestRecall = Double.MIN_VALUE;
        double bestF1 = Double.MIN_VALUE;
        double bestMRR = Double.MIN_VALUE;
        double bestMAE = Double.MAX_VALUE;
        double bestRMSE = Double.MAX_VALUE;
        
        boolean printBestParamsByEvaluator = true; // Whether we should print out the best parameter values for all Evaluators
        
        // Find the best value for each of the Evaluators
        for(Result currentRes : resArray) {
            
            if(currentRes.getNDCG() > bestNDCG) {
                bestNDCG = currentRes.getNDCG();
            }
            if(currentRes.getMAE() < bestMAE) {
                bestMAE = currentRes.getMAE();
            }
            if(currentRes.getRMSE() < bestRMSE) {
                bestRMSE = currentRes.getRMSE();
            }
            if(currentRes.getMAP() > bestMAP) {
                bestMAP = currentRes.getMAP();
            }
            if(currentRes.getPrec() > bestPrec) {
                bestPrec = currentRes.getPrec();
            }
            if(currentRes.getRecall() > bestRecall) {
                bestRecall = currentRes.getRecall();
            }
            if(currentRes.getF1() > bestF1) {
                bestF1 = currentRes.getF1();
            }
            if(currentRes.getMRR() > bestMRR) {
                bestMRR = currentRes.getMRR();
            }
        }
        
        // Now we know the best values. Now find the best parameters with these values!
        
        // First we need a bunch of ArrayLists to store the results
        ArrayList<Result> bestNDCGRes = new ArrayList<>();
        ArrayList<Result> bestMAERes = new ArrayList<>();
        ArrayList<Result> bestRMSERes = new ArrayList<>();
        ArrayList<Result> bestMAPRes = new ArrayList<>();
        ArrayList<Result> bestPrecRes = new ArrayList<>();
        ArrayList<Result> bestRecallRes = new ArrayList<>();
        ArrayList<Result> bestF1Res = new ArrayList<>();
        ArrayList<Result> bestMRRRes = new ArrayList<>();
        
        // Go through the results again and if the Result has the best value, save it for retrieval later
        for(Result currentRes : resArray) {
            if(currentRes.getNDCG() == bestNDCG) {
                bestNDCGRes.add(currentRes);
            }
            if(currentRes.getF1() == bestF1) {
                bestF1Res.add(currentRes);
            }
            if(currentRes.getMAE() == bestMAE) {
                bestMAERes.add(currentRes);
            }
            if(currentRes.getMAP() == bestMAP) {
                bestMAPRes.add(currentRes);
            }
            if(currentRes.getMRR() == bestMRR) {
                bestMRRRes.add(currentRes);
            }
            if(currentRes.getPrec() == bestPrec) {
                bestPrecRes.add(currentRes);
            }
            if(currentRes.getRMSE() == bestRMSE) {
                bestRMSERes.add(currentRes);
            }
            if(currentRes.getRecall() == bestRecall) {
                bestRecallRes.add(currentRes);
            }
        } // end find best results
        
        // Now we've stored the best Results for each Evaluator. Time to output the results!
        if(printBestParamsByEvaluator) {
            // NDCG
            out.write("\n" + SINGLE_LINE + "\nBest NDCG: " + bestNDCG);
            out.write("\nMatching Parameter Count: " + bestNDCGRes.size() + "\n");
            PrintResultHeader(resType, out);
            PrintResultArray(resType, bestNDCGRes, out);


            // MAE
            out.write("\n" + SINGLE_LINE + "\nBest MAE: " + bestMAE);
            out.write("\nMatching Parameter Count: " + bestMAERes.size() + "\n");
            PrintResultHeader(resType, out);
            PrintResultArray(resType, bestMAERes, out);

            // RMSE
            out.write("\n" + SINGLE_LINE + "\nBest RMSE: " + bestRMSE);
            out.write("\nMatching Parameter Count: " + bestRMSERes.size() + "\n");
            PrintResultHeader(resType, out);
            PrintResultArray(resType, bestRMSERes, out);

            // MAP
            out.write("\n" + SINGLE_LINE + "\nBest MAP: " + bestMAP);
            out.write("\nMatching Parameter Count: " + bestMAPRes.size() + "\n");
            PrintResultHeader(resType, out);
            PrintResultArray(resType, bestMAPRes, out);

            // Prec
            out.write("\n" + SINGLE_LINE + "\nBest Prec: " + bestPrec);
            out.write("\nMatching Parameter Count: " + bestPrecRes.size() + "\n");
            PrintResultHeader(resType, out);
            PrintResultArray(resType, bestPrecRes, out);

            // Recall
            out.write("\n" + SINGLE_LINE + "\nBest Recall: " + bestRecall);
            out.write("\nMatching Parameter Count: " + bestRecallRes.size() + "\n");
            PrintResultHeader(resType, out);
            PrintResultArray(resType, bestRecallRes, out);

            // F1
            out.write("\n" + SINGLE_LINE + "\nBest F1: " + bestF1);
            out.write("\nMatching Parameter Count: " + bestF1Res.size() + "\n");
            PrintResultHeader(resType, out);
            PrintResultArray(resType, bestF1Res, out);

            // MRR
            out.write("\n" + SINGLE_LINE + "\nBest MRR: " + bestMRR);
            out.write("\nMatching Parameter Count: " + bestMRRRes.size() + "\n");
            PrintResultHeader(resType, out);
            PrintResultArray(resType, bestMRRRes, out);
        }   
        
                 
        
    }
    
    /**
     * This method prints the appropriate header for a given Result subclass, using
     * the passed PrintWriter object.
     * 
     * @param resType The type of Recommender used to generate the Results
     * @param out A PrintWriter object for writing, either to standard out or a file
     */
    private static void PrintResultHeader(ResultType resType, PrintWriter out) {
        if(null != resType) switch (resType) {
            case ROCCHIO:
                out.write("\nalpha\tbeta\tgamma");
                break;
            case PAIRWISE:
                out.write("\nsamp_perc");
                break;
            case QZR:
                out.write("\nalpha\tbeta\tgamma\tsimilarity");
                break;
            case MTT:
                out.write("\nnewCat\tdelCat");
                break;
            case SHORTLONG:
                out.write("\nalpha");
                break;
            case SIMRATING:
                out.write("\nalpha\tbeta\tgamma");
                break;
            default:
                break;
        }
    }
    
    /**
     * This method prints out the values for the given subclass of the Result class
     * @param resType The type of Recommender used
     * @param resToPrint The set of Results to print
     * @param out A PrintWriter object for writing, either to a file or standard out
     */
    private static void PrintResultArray(ResultType resType, ArrayList<Result> resToPrint, PrintWriter out) {
        
        for(Result res : resToPrint) {
            if(null != resType) switch (resType) {
                case ROCCHIO:
                    out.write("\n" + res.getAlpha() + "\t" + res.getBeta() + "\t" + res.getGamma());
                    break;
                case PAIRWISE:
                    out.write("\n" + res.getSampPerc()+"");
                    break;
                case QZR:
                    out.write("\n" + res.getAlpha() + "\t\t" + res.getBeta() + "\t\t" + res.getGamma() + "\t\t" + res.getSimilarity());
                    break;
                case MTT:
                    out.write("\n" + res.getNewCat() + "\t" + res.getDelCat());
                    break;
                case SHORTLONG:
                    out.write("\n" + res.getAlpha());
                    break;
                case SIMRATING:
                    out.write("\n" + res.getAlpha() + "\t" + res.getBeta() + "\t" + res.getGamma());
                    break;
                default:
                    break;
            }
        }
    }
    
    /**
     * This enumeration describes the type of Result subclass being described. 
     */
    private enum ResultType {
        ROCCHIO, QZR, PAIRWISE, MTT,SHORTLONG, SIMRATING, INVALID
    }
    
}
