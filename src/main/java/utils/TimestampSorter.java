package utils;

import datastructures.Rating;
import java.util.Comparator;

/**
 * Sort Ratings by timestamp and if equal then by alphabetical order
 */
 public class TimestampSorter implements Comparator<Rating> {

        @Override
        public int compare(Rating t, Rating t1) {
            if(t.getTimestamp() > t1.getTimestamp()) {
                return 1;
            } else if (t.getTimestamp() < t1.getTimestamp()) {
                return -1;
            } else {
                return t.getMovie().getTitle().compareTo(t1.getMovie().getTitle());
            }
        }
        
    }