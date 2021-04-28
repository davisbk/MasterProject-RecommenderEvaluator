package utils;

import datastructures.Rating;
import java.util.Comparator;

/**
 * This is a class for sorting Ratings by rating score. 
 * 
 * @author Brian Davis
 */
 public class RatingsSorter implements Comparator<Rating> {
        
        /**
         * Override of Comparator.compare for comparing to Ratings
         * 
         * @param t The first Rating to be compared
         * @param t1 The second Rating to be compared
         * @return 
         */
        @Override
        public int compare(Rating t, Rating t1) {
            if(t.getRating() > t1.getRating()) {
                return -1;
            } else if (t.getRating() < t1.getRating()) {
                return 1;
            } else {
                return t.getMovie().getTitle().compareTo(t1.getMovie().getTitle());
            }
        }
        
    }