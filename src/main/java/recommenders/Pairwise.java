package recommenders;

import datastructures.Movie;
import datastructures.Prediction;
import datastructures.PropertiesHash;
import datastructures.Rating;
import datastructures.User;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Random;
import java.util.LinkedList;
import utils.DBManager;
import utils.Printer;
import utils.Settings;

/**
 * This class is an implementation of the paper "Pairwise optimized Rocchio algorithm
 * for text categorization" by Yun-Qian Miao and Mohamed Kamel (2011). The idea is to
 * have individual vectors for each rating class. Then we adjust these centroids pairwise
 * so that the average distance of the movies in that class is minimized. For more information,
 * please see the paper: https://www.sciencedirect.com/science/article/pii/S0167865510003223
 *  
 * @author Brian Davis
 */
public class Pairwise implements RecommenderInterface {
    final static DecimalFormat TWOFORMAT = new DecimalFormat("0.00"); // A two decimal place formatter when printing out the alpha values (debugging and finding parameters)
    HashMap<Integer, PairwiseUser> pairwiseUsers; // The resulting HashMap of PairwiseUsers
    
    final static double ALPHA_STEP_SIZE = 0.05; // The amount to increase alpha per step (default = 0.05) when calculating the alpha matrix. 
    final static int ALPHA_LOWER_BOUND = -1;
    final static int ALPHA_UPPER_BOUND = 1;
    double SAMP_PERC; // The percentage of the rated movies with a particular rating score which should be sampled when generating the Profile
    Printer printer = Printer.getCurrentPrinter();
    
    // CONSTRUCTORS
    public Pairwise(HashMap<Integer, User> userHash) {
        pairwiseUsers = new HashMap<>();
        SAMP_PERC = Settings.getPairwiseSampPerc();
        
        for(Integer key : userHash.keySet())  { // For each User
                   
            User user = userHash.get(key);  // Get the User object...
            
            PairwiseUser currentUser = new PairwiseUser(user); // Create a new PairwiseUser from this User
            
            // Generate the user profile for each User. We use five different vectors, one for each possible rating score.
            for(Rating currentRating : currentUser.getUser().getTrainingRatings()) { // For each Movie the User rated
                
                // Get the properties for the Movie
                PropertiesHash movieProps = currentRating.getMovie().getProperties();
                
                // Add the properties to the respective vector, and keep track of
                // which Movies were given which score
                if(currentRating.getRating() == 1) {
                    
                    currentUser.avgRatingsVectors[0].add(movieProps);
                    currentUser.allMovieIDs[0].add(currentRating.getMovie().getId());
                    continue;
                                                        
                }
                if(currentRating.getRating() == 2) {
                    
                    currentUser.avgRatingsVectors[1].add(movieProps);
                    currentUser.allMovieIDs[1].add(currentRating.getMovie().getId());
                    continue;
                                     
                }
                if(currentRating.getRating() == 3) {
                    
                    currentUser.avgRatingsVectors[2].add(movieProps);
                    currentUser.allMovieIDs[2].add(currentRating.getMovie().getId());
                    continue;
                                     
                }
                if(currentRating.getRating() == 4) {
                    
                    currentUser.avgRatingsVectors[3].add(movieProps);
                    currentUser.allMovieIDs[3].add(currentRating.getMovie().getId());
                    continue;
                    
                }
                if(currentRating.getRating() == 5) {
                    
                    currentUser.avgRatingsVectors[4].add(movieProps);
                    currentUser.allMovieIDs[4].add(currentRating.getMovie().getId());
                    
                    
                }
                
                
            } // End for each Rating
            
           
                          
            // Generate the movie ID samples
            currentUser.sampleIndices = generateSampleIndices(currentUser);
            
            computeAlpha(currentUser); // Compute alpha matrix for this User
            
            // Add our current User to the HashMap of Users
            pairwiseUsers.put(currentUser.getUser().getId(), currentUser); // Add to HashMap of PairwiseUsers
            
        }
    }
        
    /**
     * This method generates the indices of the Movies to sample for the given PairwiseUser. The number of indices generated 
     * per possible rating score (1-5) is a percentage (SAMP_PERC) of the total number of Movies rated with that score by the User.
     * Indices are randomly selected but duplicates are disallowed.   
     * 
     * @param user The PairwiseUser whose Movies we wish to sample
     * @return ret A LinkedList<Integer>[], i.e. five LinkedLists (one for each possible score) containing Movie ID numbers to sample
     */
    private LinkedList<Integer>[] generateSampleIndices(PairwiseUser user) {
        LinkedList<Integer>[] ret = new LinkedList[5];
        Random rand = new Random(); // A random number generator to randomly select movies to sample
        
        // Initialize the LinkedLists
        for(int i = 0; i < 5; i++) {
            ret[i] = new LinkedList<>();            
        }
        
        // Generate the samples. The idea is to create an array containing the movie IDs for each rating score (which we stored
        // when first scanning the Ratings for each User). Then randomly choose an index into this array and put the ID at 
        // that index location into the final list. Do this until we have generated the required number of samples, rejecting duplicates.
        for(int i = 0; i < 5; i++) { // For each possible rating score
            int numSamples = (int)(user.allMovieIDs[i].size() * SAMP_PERC);  // Determine the number of samples for this rating score
            
            Integer[] tempArray = new Integer[user.allMovieIDs[i].size()]; // Create a new array to store the IDs.
            user.allMovieIDs[i].toArray(tempArray);
            
            while(numSamples > 0) { // Until we have all of our desired samples...
                int arrayIndex = rand.nextInt(tempArray.length);  // Randomly generate a new index
                                
                if(!ret[i].contains(tempArray[arrayIndex])) { // If the Movie wasn't already selected
                    ret[i].add(tempArray[arrayIndex]); // Add it to the LinkedList for this rating score
                    numSamples--;                    
                }              
            }
        }
        
        user.setSamplesWereCreated(true); // Mark that we have generated the samples for this PairwiseUser
        
        
        return ret;
    }
    
    /**
     * This method is used to generate the profile vector for a given rating using only the sampled movies.
     * 
     * @param user The User for whom we wish to generate the profile vector
     * @param ratingScoreIndex The rating score for which we wish to find the profile vector
     * @return A PropertiesVector which contains the properties for the samples for the given rating
     */
    private PropertiesHash getVectorFromSampleIDs(PairwiseUser user, int ratingScoreIndex) {
        PropertiesHash ret = new PropertiesHash();
        for(Integer movieID : user.sampleIndices[ratingScoreIndex]) {
            ret.add(DBManager.movies.get(movieID).getProperties());
        }
              
        return ret;
    }
    
    /**
     * This method computes the alpha matrix for the given PairwiseUser. The alpha matrix gives a factor "alpha"
     * pairwise between classes (rating scores) which describe the centroid relationship between the two classes. 
     * It is used to calculate the new centroid (c') for the class c[i] when using the following equation:
     * c'[i] = c[i] + alpha*(c[i] - c[j])
     * 
     * @param usr The PairwiseUser for whom we wish to calculate the alpha matrix
     */
    private void computeAlpha(PairwiseUser usr) {
                
        // Make sure that we have already generated movie sample indices for the "calculateNumMisclassified" method. 
        // Without this we would have to check all movies, which could be costly if they have rated too many. 
        if(!usr.getSamplesWereCreated()) {
            usr.sampleIndices = generateSampleIndices(usr); // Generate indices to sample
        }
              
        
        // Now compute Alpha!
        for(int i = 0; i < 4; i++) {
            if(usr.getAverageVector(i).getSize() == 0) { // To avoid calculating alpha when there are no properties
                    continue;
                }
            for(int j = i+1; j < 5; j++) {
                if(usr.getAverageVector(j).getSize() == 0) { // To avoid calculating alpha when there are no properties
                    continue;
                }
                
                // Calcualte the average distance of the movies in the current class
                PropertiesHash centroidJ = getVectorFromSampleIDs(usr, j);
                PropertiesHash centroidI = getVectorFromSampleIDs(usr, i);
                //double avgDist = computeAverageDistance(usr.getSampleIndices()[i], getVectorFromSampleIDs(usr,j));
                double avgDist = computeAverageDistance(usr.getSampleIndices()[i], centroidJ);
                
                double bestAlpha = -1.0; // Stores the best alpha value
                double bestDistance = avgDist; // Stores the best distance score
                boolean alphaImproved = false; // whether moving the centroid helped
                
                for(double alpha = ALPHA_LOWER_BOUND; alpha <= ALPHA_UPPER_BOUND ; alpha += ALPHA_STEP_SIZE) {
                    
                    // Calculate new centroid c' (cPrime) using the equation:
                    // c'[i] = c[i] + alpha*(c[i] - c[j])
                    PropertiesHash tmpCentroidJ = new PropertiesHash(centroidJ);
                    PropertiesHash tmpCentroidI = new PropertiesHash(centroidI);
                    tmpCentroidI.subtract(tmpCentroidJ);
                    tmpCentroidI.multiply(alpha);
                    
                    //PropertiesHash cPrime = getVectorFromSampleIDs(usr,i);
                    PropertiesHash cPrime = new PropertiesHash(centroidI);
                    cPrime.add(tmpCentroidI);
                    
                    double tmpAvgDist = computeAverageDistance(usr.getSampleIndices()[j],cPrime);
                                        
                    if(tmpAvgDist < bestDistance) { 
                        alphaImproved = true;
                        bestAlpha = alpha;
                        bestDistance = tmpAvgDist;
                        usr.setCPrimeVector(i, j, cPrime);
                            
                    }
                    
                }
                if(alphaImproved) { // If we improved on the original, use the new alpha.
                    usr.setAlpha(i, j, bestAlpha);
                } else { // Otherwise set it to zero (i.e. leave the original centroid alone)
                    usr.setAlpha(i,j,0.0);
                }
            }
        }
        
               
    } // end computeAlpha
    
    /**
     * This method calculates the average distance of the movies whose IDs are given in the given LinkedList from the 
     * PropertiesVector identified in the parameter 'target'. 
     * 
     * @param indices The indices of the movies whose Properties are used for the distance calculations
     * @param target The vector against which we calculate the distance of the movies
     * @return The average distance
     */
    public static double computeAverageDistance(LinkedList<Integer> indices, PropertiesHash target) {
        double ret = Double.MAX_VALUE;
        double sum = 0.0;
        
        for(Integer currentIndex : indices) {
            PropertiesHash currentMovieProps = DBManager.movies.get(currentIndex).getProperties();
            
            sum += currentMovieProps.distance(target);
        }
        
        if(!indices.isEmpty()) {
            ret = sum / indices.size();
        }
        
        return ret;
    }
    
    /**
     * This method computes the average cosine similarity of the given class (indices) and
     * the target PropertiesHash
     * 
     * @param indices The centroid whose movies we wish to compare to target
     * @param target The PropertiesHash to compare with the centroid in indices
     * @return The average cosine similarity of the movies in indices vs. target
     */
    public static double computeAverageCosSim(LinkedList<Integer> indices, PropertiesHash target) {
        double ret = Double.MIN_VALUE;
        double sum = 0.0;
        
        for(Integer currentIndex : indices) {
            PropertiesHash currentMovieProps = DBManager.movies.get(currentIndex).getProperties();
            sum += currentMovieProps.cosSimilarity(target);
        }
        
        if(!indices.isEmpty()) {
            ret = sum / indices.size();
        }
        
        return ret;
    }
            
    /**
     * Generates Predictions for the Users in the HashMap
     * 
     * @param users The HashMap of Users for whom we wish to generate Predictions
     * @return  A HashMap of Predictions for the given Users
     */
    @Override
    public HashMap<User, LinkedList<Prediction>> predict(HashMap<Integer, User> users) {
               
        HashMap<User, LinkedList<Prediction>> retPreds = new HashMap<>(); // The final returned HashMap with all Predictions for all Users
        
        LinkedList<Prediction> currentUserPreds; // To store the Predictions for each User
        
        // Begin!
        for(Integer userID : users.keySet()) { // For each User
            
            currentUserPreds = new LinkedList<>(); // Initialize the LinkedList for the current User
            
            PairwiseUser currentUser = pairwiseUsers.get(userID); // Get the current PairwiseUser
            
            // To save us from constantly re-calculating the sampled vectors we can create them once
            // and store the results.
            PropertiesHash[] sampledCentroids = new PropertiesHash[5];
            for(int i = 0; i < 5; i++) {
                sampledCentroids[i] = getVectorFromSampleIDs(currentUser, i);
            }
            
            
            for(Rating currentRating : currentUser.getUser().getTestRatings()) { // For each Movie in the User's test set
                PropertiesHash movieProps = currentRating.getMovie().getProperties();
                              
                double bestSim = currentUser.getCPrimeVector(0, 0).cosSimilarity(movieProps);
                for(int i = 0; i < 5; i++) {
                    for(int j = 0; j < 5; j++) {
                        double tmp = currentUser.getCPrimeVector(i, j).cosSimilarity(movieProps);
                        
                        if(tmp > bestSim) {
                            bestSim = tmp;
                        }
                    }
                }
                
                currentUserPreds.add(new Prediction(currentRating.getMovie(),bestSim));
                                
            } // end for each Rating
            
            currentUserPreds.sort(null); // Sort the Predictions
            
            retPreds.put(currentUser.getUser(), currentUserPreds);
           
            
             
        } // end for each User
               
        return retPreds;
        
    }
    
    /**
     * This method is used to predict the score for a single Movie. It is called
     * by the ScoreToRating class.
     * 
     * @param user The particular User whose profile we wish to use to give a Prediction.
     * @param movie The particular Movie for which we wish to produce a Prediction
     * @return A cosine similarity for the Movie's properties with the User's profile
     */
    @Override
    public double predictMovie(User user, Movie movie) {
        
        PairwiseUser currentUser = pairwiseUsers.get(user.getId());
        
        
        PropertiesHash movieProps = movie.getProperties(); // The current movie's Properties
               
        double bestSim = Double.MIN_VALUE;
        
        for(int i = 0; i < 5; i++ ) {
            for(int j = 0; j < 5; j++) {
                double tmpSim = currentUser.getCPrimeVector(i, j).cosSimilarity(movieProps);
                
                if(tmpSim > bestSim) {
                    bestSim = tmpSim;
                }
            }
        }
        
        return bestSim; // return the cosine similarity for the best distance we had
        
    }
       
    /**
     * This class is an extension of the User class. It gives the User
     * an array of PropertiesHashes which keep track of the "average" properties for
     * each Rating score. It also keeps track of all of the Movies that a User has
     * rated, as well as the IDs of the Movies rated by the User. Lastly it also 
     * stores the alpha matrix, the pairwise adjustment factors for calculating new
     * pairwise centroids. 
     * 
     */
    class PairwiseUser {
    
        final private User user;
        private PropertiesHash[] avgRatingsVectors; // Vectors containing Properties for each possible rating score
        private LinkedList<Integer>[] allMovieIDs; // Stores the indices of ALL Movies for this User, for all rating scores
        private LinkedList<Integer> sampleIndices[]; // Stores the movie indices from which we're sampling for this User
        final private double alphaMatrix[][]; // The pairwise (by rating) adjustment factors for calculating the new centroid
        private boolean samplesWereCreated; // Whether or not samples were generated
        private PropertiesHash[][] cPrimeMatrix;
        
        // CONSTRUCTORS
        /**
         * Generate a PairwiseUser from User u
         * @param u The User whose data we will use to create a PairwiseUser
         */
        public PairwiseUser(User u) {
            this.user = u;
            alphaMatrix = new double[5][5];
            allMovieIDs = new LinkedList[5];
            avgRatingsVectors = new PropertiesHash[5];
            sampleIndices = new LinkedList[5];
            samplesWereCreated = false;  
            cPrimeMatrix = new PropertiesHash[5][5];
            
            // Initialize arrays of LinkedLists and PropertiesVectors
            for(int i= 0; i < 5; i++) {
                avgRatingsVectors[i] = new PropertiesHash();
                allMovieIDs[i] = new LinkedList<>();
                sampleIndices[i] = new LinkedList<>();
            }
            
            for(int i = 0; i < 5; i++) {
                for(int j = 0; j < 5; j++) {
                    cPrimeMatrix[i][j] = new PropertiesHash();
                }
            }
            
        }
        
        // SETTERS
        public void setCPrimeVector(int i, int j, PropertiesHash cPrime) {
            cPrimeMatrix[i][j] = cPrime;
        }        
        public void setAlpha(int i, int j, double val) {
            alphaMatrix[i][j] = val;
        }
        
        public void setIndices(LinkedList<Integer> indices[]) {
            this.sampleIndices = indices;
        }
        
        private void setSamplesWereCreated(boolean bool) {
            this.samplesWereCreated = bool;
        }
        
        // GETTERS
        public PropertiesHash getCPrimeVector(int i, int j) {
            return cPrimeMatrix[i][j];
        }
        public PropertiesHash[] getRatings() {
            return this.avgRatingsVectors;
        }
        
        public User getUser() {
            return this.user;
        }
        
        public double getAlpha(int i, int j) {
            return alphaMatrix[i][j];
        }
        
        public double[][] getAlpha() {
            return alphaMatrix;
        }
        
        public PropertiesHash getAverageVector(int index) {
            return new PropertiesHash(this.avgRatingsVectors[index]);
        }
        
        private boolean getSamplesWereCreated() {
            return this.samplesWereCreated;
        }
         
        // METHODS
        
        /**
         * Returns a String containing a nice printout of the alpha matrix for this 
         * PairwiseUser. Mostly for debugging purposes. 
         * 
         * @return res A String containing the alpha matrix for this PairwiseUser
         */
        public String alphaMatrixToString() {
            String res = "";
            for(int i = 0; i < 5; i++) {
                for(int j = 0; j < 5; j++) {
                    res+= (TWOFORMAT.format(alphaMatrix[i][j]) + "\t");
                }
                res+= "\n";
            }
            
            return res;
        }

        public LinkedList<Integer>[] getSampleIndices() {
            return this.sampleIndices;
        }

    } // end class PairwiseUser
    
}