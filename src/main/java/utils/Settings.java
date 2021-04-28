package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/*
 * Class to grab settings from Setttings.cfg or similar config file
 *
 * @author Brian Davis
 */


public class Settings {
    private static Settings	currentSettings;        // We're using static factory methods
    private static boolean	isInit;                 // Whether the Settings are initialized
    private static boolean printEachFoldEvaluation;              // Whether we print the evaluation for each fold or for all folds in total

    static Printer printer;
    
    static double getPrintLevel() {
        return 7;
    }

    private final File file;                            // Settings file (default "settings.cfg")
    private String settingsFileName;                    // The name of the Settings file to use (default "settings.cfg")
    private String DBName;                              // The name of the database to use (may want various for testing and debugging)
    private double maxRating;                           // Maximal rating possible 
    private double minRating;                           // Minimal rating possible
    private String userList;                            // A list of Users that we are interested in
    private int numUsers;                               // Limit of number of users

    private Map<String, Double>	rocchioParams;          // The parameters for the Rocchio algorithm (alpha, beta, gamma)
    private int topK;                                   // Number of items that should be used on the top K list.
    private double trainingSetSize;                     // The percentage of the data to use as the training set
    private int numFolds;                               // The number of folds to run
    private double similarityThreshold;                 // The similarity threshold to use when creating profiles in the QueryZoneRecommender
    private double pairwiseSampPercentage;              // The percentge of the Users' Movies to use when sampling in the PairwiseRocchio class
    private double mttNewCatThreshold;                           // Threhold to add to a category for MTT and ShortLongRecommender
    private double mttDelCatThreshold;                        // Threshold to delete a category for MTT recommender
    private boolean writeEvalToFile;                    // Whether the output should be written to the output file
    private int numTopKGenres;

    // Static block to initialize the class.
    static {
        init();
    }

    /**
     * Initializes the Settings. 
     */
    public static void init() {
        if (isInit)
                return;
        currentSettings = new Settings("settings.cfg");
        isInit = true;
    }

    /**
     * Reloads the settings from the given file
     * @param path A path to a settings file
     */
    public static void loadNewSetting(String path) {
        currentSettings = new Settings(path);
    }
        
    // Private constructor (abstract factory)
    private Settings(String path) {
        file = new File(path);
        try {
            printer = Printer.getCurrentPrinter();
        }
        catch(RuntimeException e) {
            printer = Printer.getConsolePrinterInstance();
        }
        if (!file.exists())
                throw new IllegalArgumentException("File does not exist: " + file);
        
        loadFromFile();
    }    
    
    /**
     * Loads the settings from the file.
     */
    private void loadFromFile() {
        // If we're not using the default settings.cfg file, print the file name
        
        Printer.getConsolePrinterInstance();  
        if(!file.getName().equals("settings.cfg")) 
            printer.print(3,"Loading Settings from " + file.getName());
        Properties props = new Properties();
        try {
                settingsFileName = file.getName();
                props.load(new FileInputStream(file));
                
                /*
                 * Database settings
                */
                
                /** For each Property, we try to get the Property and then if it is not found or there is an error 
                  * we inform the user and set a default value. 
                  */
                
                // DBName
                DBName = props.getProperty("dbname");
                
                if(DBName.equals("")) {
                    printer.print(3,"DBName not specified. Using default value of \"movies\".");
                    DBName = "movies";
                }
                
                // minRating
                try{
                    minRating = getDouble("minRating", props);
                }
                catch(RuntimeException e) {
                    printer.print(3,"Could not find value for minRating. Using default value of 1.");
                    minRating = 1;
                }
                
                // maxRating
                try{
                    maxRating = getDouble("maxRating", props);
                }
                catch(RuntimeException e) {
                    printer.print(3,"Could not find value for maxRating. Using default value of 5.");
                    maxRating = 5;
                }
                
                
                /*
                 * Evaluation Settings
                */
                
                // printEachFoldEvaluation
                try {
                    printEachFoldEvaluation = getBoolean("printEachFoldEvaluation", props);
                }
                catch(RuntimeException e) {
                    printer.print(3,"Could not load value for printEachFoldEvaluation. Using default value of \"true\"");
                    printEachFoldEvaluation = true;
                }
                
                // topK
                try {
                    topK = getInt("topK", props);
                }
                catch(RuntimeException e) {
                    printer.print(3,"Could not load value for topK. Using default value of 3.");
                    topK = 3;
                }
                
                // trainingSetSize
                try {
                    trainingSetSize = getDouble("trainingSetSize",props);
                }
                catch(RuntimeException e) {
                    printer.print(3,"Could not load value for trainingSetSize. Using default value of 0.8");
                    trainingSetSize = 0.8;
                }
                
                // numFolds
                try {
                    numFolds = getInt("numFolds", props);
                }
                catch(RuntimeException e) {
                    printer.print(3,"Could not load value for numFolds from " + file.getName() + " using default value of 4 folds.");
                }
                
                // userList
                userList = props.getProperty("userList");
                if(userList == null) // Property 'userList' was not found in the file
                    userList = "";
                
                // numUsers
                try {
                    numUsers = getInt("numUsers",props);
                }
                catch(RuntimeException e) {
                    printer.print(3,"No value for numUsers found in " + file.getName() + " .");
                    printer.print(3,"All Users will be loaded.");
                    numUsers = -1;
                }
                
                               
                /*
                 * Rocchio Algorithm Settings
                */
                try {
                    rocchioParams = new HashMap<>();
                    rocchioParams.put("alpha", getDouble("rocchioParams.alpha", props));
                    rocchioParams.put("beta", getDouble("rocchioParams.beta", props));
                    rocchioParams.put("gamma", getDouble("rocchioParams.gamma", props));
                }
                catch(RuntimeException e) {
                    printer.print(3,"Could not load values for rocchioParams. Using default values of 4, 16, and 4");
                    printer.print(3," for alpha, beta, and gamma, respectively.");
                    rocchioParams.put("alpha",4.0);
                    rocchioParams.put("beta", 16.0);
                    rocchioParams.put("gamma", 4.0);
                }
                
                                
                try {
                    writeEvalToFile = getBoolean("writeEvalToFile",props);
                }
                catch(RuntimeException e) {
                    printer.print(3, "Could not load value for \"writeEvalToFile\", setting to true.");
                    writeEvalToFile = true;
                }
                
                try {
                    numTopKGenres = getInt("numTopKGenres",props);
                } catch (RuntimeException e) {
                    printer.print(3, "Could not load value for \"numTopKGenres\", setting to default of 5.");
                    numTopKGenres = 5;
                }
                
                // Recommender-specific settings
                try {
                    similarityThreshold = getDouble("similarityThreshold",props);
                }
                catch(RuntimeException e) {
                    printer.print(3, "Could not load value for similarityThreshold. Using default value of 0.3");
                    similarityThreshold = 0.3;
                }
                
                try {
                    pairwiseSampPercentage = getDouble("pairwiseSampPercentage",props);
                }
                catch(RuntimeException e) {
                    printer.print(3, "Could not load value for pairwiseSampPercentage. Using default value of 0.6");
                    pairwiseSampPercentage = 0.6;
                }
                
                try {
                    mttNewCatThreshold = getDouble("mttNewCatThreshold", props);
                } catch(RuntimeException e) {
                    printer.print(3, "Could not load value for mttNewCatThreshold. Using default value of 0.2 ");
                    mttNewCatThreshold = 0.2; // TODO - Make sure this is the correct default value!
                }
                
                try {
                    mttDelCatThreshold = getDouble("mttDelCatThreshold", props);
                } catch(RuntimeException e) {
                    printer.print(3, "Could not load value for mttDelCatThreshold. Using default value of 0.5");
                    mttDelCatThreshold = 0.5; // TODO - Make sure this is the correct default value!
                }
                
                
        } catch (IOException | NumberFormatException e) {
                printer.print(3,"Settings failed to load." + e);
                throw new RuntimeException(e);                
        }
    }
    
    public static void resetSettingsToDefaultValues() {
        printer.printTS(3, "RESETTING SETTINGS TO DEFAULT VALUES");
        
        
        currentSettings.maxRating = 5;                           // Maximal rating possible 
        currentSettings.minRating = 1;                           // Minimal rating possible
        currentSettings.userList = "2,3,4";                            // A list of Users that we are interested in
        currentSettings.numUsers = 3;                               // Limit of number of users

        currentSettings.rocchioParams.clear();          // The parameters for the Rocchio algorithm (alpha, beta, gamma)
        currentSettings.rocchioParams.put("alpha", 0.4);
        currentSettings.rocchioParams.put("beta", 0.2);
        currentSettings.rocchioParams.put("gamma", 0.8);
        currentSettings.topK = 5;                                   // Number of items that should be used on the top K list.
        currentSettings.trainingSetSize = 0.7;                     // The percentage of the data to use as the training set
        currentSettings.numFolds = 1;                               // The number of folds to run
        currentSettings.similarityThreshold = 0.15;                 // The similarity threshold to use when creating profiles in the QueryZoneRecommender
        currentSettings.pairwiseSampPercentage = 1.0;              // The percentge of the Users' Movies to use when sampling in the PairwiseRocchio class
        currentSettings.mttNewCatThreshold = 0.2;                           // Threhold to add to a category for MTT and ShortLongRecommender
        currentSettings.mttDelCatThreshold = 0.1;                        // Threshold to delete a category for MTT recommender
        currentSettings.writeEvalToFile = true;                    // Whether the output should be written to the output file
        currentSettings.numTopKGenres = 10;
    }

    // GETTERS

    /**
     *
     * @return
     */
        public static boolean getIsInitialized() {
        return Settings.isInit;
    }

    /**
     *
     * @return
     */
    public static String getDBName() {
        return currentSettings.DBName;
    }

    /**
     *
     * @return
     */
    public static double getMaxRating() {
            return currentSettings.maxRating;
    }

    /**
     *
     * @return
     */
    public static double getMinRating() {
            return currentSettings.minRating;
    }    

    /**
     *
     * @return
     */
    public static int getTopK() {
        return currentSettings.topK;
    }    

    /**
     *
     * @return
     */
    public static int getNumUsers() {
        return currentSettings.numUsers;
    }

    /**
     *
     * @return
     */
    public static String getUserList() {
        return currentSettings.userList;
    }
    
    public static int getNumFolds() {
        return currentSettings.numFolds;
    }

    /**
     *
     * @return
     */
    public static double getTrainingSetSize() {
        return currentSettings.trainingSetSize;
    }

    /**
     *
     * @return
     */
    public static Map<String, Double> getRocchioParams() {
            return currentSettings.rocchioParams;
    }    

    /**
     *
     * @return
     */
    public static String getSettingsFileName() {
        return currentSettings.settingsFileName;
    }
    
    public static boolean getPrintEachFoldEvaluation() {
        return Settings.printEachFoldEvaluation;
    }
    
    public static double getSimilarityThreshold() {
        return currentSettings.similarityThreshold;
    }
    
    public static double getPairwiseSampPerc() {
        return currentSettings.pairwiseSampPercentage;
    }
    
    public static boolean getWriteEvalToFile() {
        return currentSettings.writeEvalToFile;
    }
    
    public static int getNumTopKGenres() {
        return currentSettings.numTopKGenres;
    }
    
    public static double getMttNewCatThreshold() {
        return currentSettings.mttNewCatThreshold;
    }
    
    public static double getMttDelCatThreshold() {
        return currentSettings.mttDelCatThreshold;
    }
    
    // SETTERS (Usually only to be used when determining parameters)
    
    static void setNumUsers(int num) {
        System.out.println("Updating currentSettings.numUsers to " + num + "...");
        currentSettings.numUsers = num;
    }
    
    public static void setAlpha(double alpha) {
        if(alpha >= 0.0 && alpha <= 1.0) {
            currentSettings.rocchioParams.replace("alpha",alpha);
        }
        else {
            printer.print(0, "Invalid setting for alpha called from Settings.setAlpha(): " + alpha);
            throw new RuntimeException();
        }
    }
    
    public static void setBeta(double beta) {
        if(beta >= 0.0 && beta <= 1.0) {
            currentSettings.rocchioParams.replace("beta",beta);
        }
        else {
            printer.print(0, "Invalid setting for beta called from Settings.setBeta(): " + beta);
            throw new RuntimeException();
        }
    }
    
    public static void setGamma(double gamma) {
        if( gamma >= 0.0 && gamma <= 1.0) {
            currentSettings.rocchioParams.replace("gamma", gamma);
        }
        else {
            printer.print(0, "Invalid setting for gamma called from Settings.setGamma(): " + gamma);
            throw new RuntimeException();
        }
    }
    
     public static void setSimilarityThreshold(double similarity) {
        currentSettings.similarityThreshold = similarity;
    }
     
    public static void setPairwiseSampleSize(double perc) {
        currentSettings.pairwiseSampPercentage = perc;
    }
    
    public static void setMttNewCatThreshold(double mttNewCatThreshold) {
        currentSettings.mttNewCatThreshold = mttNewCatThreshold;
    }
    
    public static void setMttDelCatThreshold(double mttDelCatThreshold) {
        currentSettings.mttDelCatThreshold = mttDelCatThreshold;
    }
    
    public static void setNumTopKGenres(int numTopKGenres) {
        currentSettings.numTopKGenres = numTopKGenres;
    }
    
    // PARSERS
    private int getInt(String key, Properties prop) {
        int result = 0;
        try {
            result = Integer.parseInt(prop.getProperty(key));
        }
        catch(NumberFormatException e) {
            throw new RuntimeException("Error in getInt parsing String \"" + key + "\"!" + "\n"+e);
        }
        return result;
    }
    
    private Double getDouble(String key, Properties prop) {
        double result = 0.0;
        try {
            result = Double.parseDouble(prop.getProperty(key));
        }
        catch(NumberFormatException e) {
            throw new RuntimeException("Error in getDouble parsing String \"" + key + "\"!" + "\n"+e);
        }
        return result;
    }

    private boolean getBoolean(String key, Properties prop) {
        boolean result = false;
        try {
            result = Boolean.parseBoolean(prop.getProperty(key));
        }
        catch(Exception e) {
            throw new RuntimeException("Error in getBoolean parsing String \"" + key + "\"!" + "\n" + e);
        }
        return result;
    }
    
    /**
     * @param newNumFolds The new number of folds
     */
    public static void setNumFolds(int newNumFolds) {
        if(newNumFolds > 0) {
            currentSettings.numFolds = newNumFolds;
        }
        else {
            throw new RuntimeException("Error setting new number of folds; " + newNumFolds + " is not positive.");
        }
    }
    
    /**
     * This method returns a String representation of the current Settings (usually for debugging)
     * 
     * @return A String representation of the current Settings
     */
    public static String SettingsAsString() {
                
        String ret = "";
        
        // General settings
        ret += "Settings file: " + currentSettings.file.getName().concat("\n");
        ret += "Database: " + currentSettings.DBName + "\n";
        ret += "numUsers: " + currentSettings.numUsers + "\n";
        ret += "userList: " + currentSettings.userList + "\n";
        ret += "topK: " + currentSettings.topK + "\n";
        ret += "trainingSetSize: " + currentSettings.trainingSetSize + "\n";
        ret += "numFolds: " + currentSettings.numFolds + "\n";
        
        // Rocchio parameters
        for(Map.Entry<String, Double> mapEntry : currentSettings.rocchioParams.entrySet()) {
            ret += mapEntry.getKey() + ": " + mapEntry.getValue() + "\n";
        }
        
        
        // QZR settings
        ret += "similarityThreshold: " + currentSettings.similarityThreshold + "\n";
        ret += "numTopKGenres: " + currentSettings.numTopKGenres + "\n";
        
        // Pairwise settings
        ret += "pairwiseSampPercentage: " + currentSettings.pairwiseSampPercentage + "\n";
        
        // MTT settings
        ret += "threshold: " + currentSettings.mttNewCatThreshold + "\n";
        ret += "catThreshold: " + currentSettings.mttDelCatThreshold + "\n";
        
        return ret;
    }
}
