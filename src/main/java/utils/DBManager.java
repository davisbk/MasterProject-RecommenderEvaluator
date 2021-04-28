package utils;

import datastructures.Movie;
import datastructures.PropertiesHash;
import datastructures.Property;
import datastructures.Rating;
import datastructures.User;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages the connection and queries to a database. 
 * Creates HashMaps representation of users and movies. 
 * 
 * @author Juan David Mendez
 */

public final class DBManager {
    private static DBManager DBinstance = null; // Singleton variable
    private static Connection  DBcon; //Database connection
    private String DBName = Settings.getDBName();   // The name of the database, retrieved from Settings.  
    private final ArrayList<String> properties = new ArrayList<>();     // List of properties that a movie can have. TODO: Fill the array from Setting files or user input
    private String db_host = "localhost";
    private String db_username = "davmen";
    private String db_password = "N8HFxc2EF28A";
    private final HashMap<Integer, User> users = new HashMap<>(); //Unmodified users
    public static HashMap<Integer, Movie> movies = new HashMap<>(); //Unmodified movies
    Printer printer;
    int numToRetrieve;
    static HashMap<Integer, Integer> ratingCounts = new HashMap<>();
    private int totalUserCount; // The total number of Users in the database
    
    
    /*
     * Sole constructor that starts connection with database, and request data
     * to create the unmodified user data structures.
    */
    private DBManager() {
        try {
            printer = Printer.getCurrentPrinter();
        }
        catch(RuntimeException e) { 
            System.out.println("Error encountered in DBManager getting Printer. Creating FileAndConsoleInstance.");
            printer = Printer.getFileAndConsoleInstance();
        }
        /*
        * Movie properties
        */
        properties.add("genre");
        properties.add("director");
        properties.add("runtime");
        properties.add("subject");
        properties.add("country");
        properties.add("starring");
        properties.add("writer");
        properties.add("year");
        try {
            Class.forName("com.mysql.jdbc.Driver");
            DBcon = DriverManager.getConnection("jdbc:mysql://" + db_host + ":3306/" + DBName + "?useSSL=false", db_username, db_password);
            if(DBcon.isValid(3)) {
                printer.printTS(3,"Successfully connected to database \"" + DBName + "\".");
                totalUserCount = FindTotalNumUsers(); // The total number of Users in the database
            }
            //getData();
            //getData2();
        }catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * This method loads the user and movie data from the database into DBManager.users 
     * and DBManager.movies. It checks to make sure that a user has enough ratings before 
     * loading the movies that that user has rated. "Enough" is defined as having at least
     * as many ratings as the average user. 
     * 
     * If a user specifies certain user IDs in userList in the Settings file, these Users
     * will be loaded in any case, even if they don't have enough ratings. 
     */
    public void getDataWithEnoughRatings() {
        System.out.println("getDataWithEnoughRatings");
        int numRequested = Settings.getNumUsers(); // Requested number of Users to retrieve from the database
        
        ArrayList<String> usersToRetrieve = new ArrayList<>(); // The particular users to retrieve
        
        if(numRequested == -1) { // If it was flagged that all users should be retrieved
            numRequested = totalUserCount;
        }
        else if(numRequested > totalUserCount) {
            printer.print(3, "Requested " + numRequested + " users, database contains only " + totalUserCount + " users. Retrieving all users.");
            numRequested = totalUserCount;
        }
        
        // Check to see whether the user requested specific Users to be retrieved.
        // If so, add these to the list to be retrieved in any case
        String userList = Settings.getUserList();
        if(!userList.isEmpty()) {
            String[] tmp = userList.split(",");
            
            usersToRetrieve.addAll(Arrays.asList(tmp));
            
        }
        
        // Now we find out how many additional Users we need to retrieve (after the specified list is subtracted out)
        numToRetrieve = numRequested - usersToRetrieve.size();
        
        if(numToRetrieve > 0 ) { // If we still need to get more users...
            int average = calculateAverageNumRatings();
                        
            if(!userList.isEmpty()) { // If the user specified some Users, before we can possibly add more Users to the list we need to add a comma.
                userList += ",";
            }
            
            // Go through our HashMap of rating counts until we find a User which fulfills our 
            // criteria (i.e. has at least the average # of Ratings) and then add them to the list
            // to be retrieved. 
            
            for(Map.Entry<Integer, Integer> mapEntry : ratingCounts.entrySet()) {
                
                if(numToRetrieve == 0) {
                    break;
                }
                else {
                    int userID = mapEntry.getKey();
                    int ratingCount = mapEntry.getValue();
                
                    if((!usersToRetrieve.contains(""+userID)) && ratingCount >= (average)){ 
                        userList += userID + ",";
                        usersToRetrieve.add(""+userID);
                        numToRetrieve--;
                    }
                }
            }
            
            // If we got through all of the Users in the database and we didn't fulfill our requested number of Users, 
            // let the user know. 
            if(numToRetrieve != 0) {
                printer.print(3, "Database does not contain enough Users to fulfill request for " + numRequested + " Users.");
            }
            
            
            userList = userList.substring(0, userList.length()-1); // to remove the last comma
                        
        } // end if we need to find more Users
        
        try {
                String queryStatement = "select * from user";
                if(!userList.isEmpty()) {
                    queryStatement += " where id in (" + userList + ")";
                }
                PreparedStatement stmt = DBcon.prepareStatement(queryStatement);
                ResultSet rs = stmt.executeQuery();
                
                while(rs.next())    {
                    User user = new User(rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getInt(4), rs.getInt(5), this.getRatingsOfUser(rs.getInt(1)), new ArrayList<Rating>());
                    users.put(rs.getInt(1), user);
                }
            } catch(SQLException e) {
                Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, e);
            }
        printer.printTS(3, usersToRetrieve.size() + " Users retrieved.");
    }
        
   /*
    * Queries the database for a list, a number or for all users. Creates a user
    * datastructure for each user and adds it to the "users" data member of this class.
    */
    public void getData()  {
        
        System.out.println("getData()");
        
        int numRequested = Settings.getNumUsers(); //Amount of users to query the database
        
        if(numRequested == -1) { // If it was flagged that all users should be retrieved
            
            numRequested = totalUserCount;
            Settings.setNumUsers(totalUserCount);
            
        }
        
        try {          
            
            String userList = Settings.getUserList();
            String limitString = "";
            if(numRequested != totalUserCount) { // Limits the query result
                limitString = " LIMIT " + numRequested + " ";
            }
            PreparedStatement stmt = DBcon.prepareStatement("SELECT * FROM user" + limitString);
            stmt.setFetchSize(4000);
            if(!userList.isEmpty()) { //Query a specific list of users
                stmt = DBcon.prepareStatement("SELECT * FROM user WHERE id in (" + userList + ")" + limitString);
            }        
            ResultSet rs = stmt.executeQuery();
            while(rs.next())    {
                User user = new User(rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getInt(4), rs.getInt(5), this.getRatingsOfUser(rs.getInt(1)), new ArrayList<Rating>());
                users.put(rs.getInt(1), user);
            }
        } catch (SQLException e) {}
    }
    
    /*
     * Queries the database for the data of a movie and puts it on a hashmap
     * @Param movieID Unique identifier of the movie in the database
    */
    private void addMovie(int movieID)  {
        try {
            PreparedStatement stmt = DBcon.prepareStatement("SELECT * FROM movie WHERE id = ?");
            stmt.setFetchSize(500);
            stmt.setInt(1, movieID);
            ResultSet rs = stmt.executeQuery();
            rs.first();    
            movies.put(movieID, new Movie(movieID, rs.getString(2), rs.getString(3), this.getPropertiesOfMovie(rs.getInt(1))));
        } catch (SQLException e) {}
    }
    
    /*
     * Queries the database for the ratings of a user and creates a rating 
     * datastructure. 
     * @Param userID Unique identifier of the user in the database
    */
    private ArrayList<Rating> getRatingsOfUser(int userID)  {      
        ArrayList<Rating> ratings = new ArrayList<>();
        try {
            PreparedStatement stmt = DBcon.prepareStatement("SELECT mid, rating, timestamp FROM rating WHERE uid = ?");
            stmt.setFetchSize(500);
            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();
            while(rs.next())    {
                if(!movies.containsKey(rs.getInt(1)))  {
                    this.addMovie(rs.getInt(1));
                }
                Rating rating = new Rating(movies.get(rs.getInt(1)), rs.getInt(2), rs.getInt(3));
                ratings.add(rating);
            }
        } catch (SQLException e) {
        }                
        return ratings; 
    }
    
    /*
     * Queries the database for all possible properties that a movie can have.
     * @Param movieID Unique identifier of the movie in the database
    */
    private PropertiesHash getPropertiesOfMovie(int movieID)  {    
        PropertiesHash propertyList = new PropertiesHash();
        for(String property : properties)   {
            try {
                PreparedStatement stmt = DBcon.prepareStatement("SELECT value FROM " + property + " WHERE mid = ?");
                stmt.setFetchSize(1000);
                stmt.setInt(1, movieID);
                ResultSet rs = stmt.executeQuery();
                while(rs.next())    {
                    propertyList.add(new Property(property, rs.getString(1)),1.0);
                }
            } catch (SQLException e) {
            }
            try {
                PreparedStatement stmt = DBcon.prepareStatement("SELECT label FROM " + property + " WHERE mid = ?");
                stmt.setInt(1, movieID);
                stmt.setFetchSize(1000);
                ResultSet rs = stmt.executeQuery();
                while(rs.next())    {
                    propertyList.add(new Property(property, rs.getString(1)),1.0);
                }
            } catch (SQLException e) {
            }
        }                        
        return propertyList;
    }

    /*
     * Singleton Pattern
     * @return an instance of this class or creates one if none exist
     */
    public static DBManager getInstance() {
      if(DBinstance == null) {
         DBinstance = new DBManager();
      }
      return DBinstance;
   }
   
    /*
     * Getter for the hashmap with unmodified users.
     * @return an instance of this class or creates one if none exist
     */
    public HashMap<Integer, User> getUsers() {
        return users;
    }
    
    /*
     * Getter for the HashMap with unmodified movies.
     * @return A reference to the movies HashMap
     */
    public static HashMap<Integer, Movie> getMovies() {
        return movies;
    }
    
    // SETTERS
    
    // Necessary for debugging, but probably not good design
    public static void setMovies(HashMap<Integer, Movie> movies) {
        DBManager.movies = movies;
    }
    
    /**
     * A method for finding the total number of users in the database
     * @return  The total number of users in the database. 
     */
    private int FindTotalNumUsers() {
        int ret = 0;
        try{
            PreparedStatement stmt = DBcon.prepareStatement("SELECT count(id) FROM user");
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                ret = rs.getInt(1);               
            }
        }
        catch(SQLException e){
            printer.print(3,"Something went wrong retrieving the number of users." + e);
        }
        
        return ret;
    }
    
    /**
     * A method for calculating the average number of ratings that all users have
     * 
     * @return  The average number of ratings for all users
     */
    private int calculateAverageNumRatings() {
        int numUsers = 0;
        int average = 0;
        try {
            PreparedStatement stmt = DBcon.prepareStatement("select uid, total_count from (select uid, count(mid) as total_count from rating group by uid) as T");
            ResultSet rs = stmt.executeQuery();
            
            while(rs.next()) {
                ratingCounts.put(rs.getInt(1), rs.getInt(2));
                average += rs.getInt(2);
                numUsers++;
            }
        }catch (SQLException e) {
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, e);
        }
        
        average /= numUsers;
        
        return average;
    }
    
    @Deprecated
    /**
     * This method calculates the standard deviation for the number of reviews of
     * the users, given the average as a parameter. 
     * 
     * @param average The average number of ratings per user.
     * @return stdDev The standard deviation for number of reviews
     */
    private int calculateStandardDeviation(int average) {
        int stdDev = 0;
        
        for(Map.Entry<Integer, Integer> mapEntry : ratingCounts.entrySet()) {
            int count = mapEntry.getValue();
            
            stdDev += Math.pow((count - average),2);
        }
        
        if(ratingCounts.size() != 1) { // to avoid division by zero
            stdDev /= (ratingCounts.size()-1);
        }        
        
        stdDev = (int)Math.sqrt(stdDev);
        
        return stdDev;
    }
}
