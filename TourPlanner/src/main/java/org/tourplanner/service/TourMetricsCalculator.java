package org.tourplanner.service;

import org.tourplanner.persistence.entity.Tour;
import org.tourplanner.persistence.entity.TourLog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Objects;

public class TourMetricsCalculator {
    private static final Logger log = LogManager.getLogger(TourMetricsCalculator.class);

    // Distance
    private static final double DISTANCE_STAR_5 = 3.0;
    private static final double DISTANCE_STAR_4 = 6.0;
    private static final double DISTANCE_STAR_3 = 9.0;
    private static final double DISTANCE_STAR_2 = 12.0;

    // Time (in minutes)
    private static final int TIME_STAR_5 = 30;
    private static final int TIME_STAR_4 = 45;
    private static final int TIME_STAR_3 = 60;
    private static final int TIME_STAR_2 = 90;

    public int calculatePopularity(List<TourLog> allLogs, Tour tour) {
        if(tour == null || tour.getTourId() == null) {
            log.debug("calculatePopularity: tour is null");
            return 0;
        }
        log.debug("Calculating popularity for tour id={}", tour.getTourId());

        List<TourLog> logs = allLogs.stream()
                .filter(logEntry -> logEntry.getTour() != null && Objects.equals(logEntry.getTour().getTourId(), tour.getTourId()))
                .toList();

        if(logs.isEmpty()) {
            log.debug("No logs found for tour id={}", tour.getTourId());
            return 0;
        }

        int countStars;
        int size = logs.size();

        if(size >= 10) countStars = 5;
        else if(size >= 7) countStars = 4;
        else if(size >= 4) countStars = 3;
        else if(size >= 2) countStars = 2;
        else countStars = 1;

        double avgRating = logs.stream().mapToInt(TourLog::getRating).average().orElse(3.0);
        double combined = (0.7 * countStars) + (0.3 * avgRating);
        int result = (int) Math.round(combined);

        log.debug("Popularity for tour id={} with {} logs: {} stars", tour.getTourId(), size, result);
        return result;
    }

    public int calculateChildFriendliness(List<TourLog> allLogs, Tour tour) {
        if(tour == null || tour.getTourId() == null) {
            log.debug("calculateChildFriendliness: tour is null");
            return 0;
        }
        log.debug("Calculating childFriendliness for tour id={}", tour.getTourId());

        List<TourLog> logs = allLogs.stream()
                .filter(logEntry -> logEntry.getTour() != null && Objects.equals(logEntry.getTour().getTourId(), tour.getTourId()))
                .toList();

        if(logs.isEmpty()) {
            log.debug("No logs found for tour id={}", tour.getTourId());
            return 0;
        }

        // Convert difficulty to stars
        double avgDifficulty = logs.stream().mapToInt(logEntry -> switch(logEntry.getDifficulty()) {
            case EASY -> 5;
            case MEDIUM -> 3;
            case HARD -> 1;
        }).average().orElse(3); // Default 3

        // Convert distance to stars
        double avgDistance = logs.stream().mapToDouble(logEntry -> {
            double dist = logEntry.getTotalDistance();
            if(dist <= DISTANCE_STAR_5) return 5;
            else if(dist <= DISTANCE_STAR_4) return 4;
            else if(dist <= DISTANCE_STAR_3) return 3;
            else if(dist <= DISTANCE_STAR_2) return 2;
            else return 1;
        }).average().orElse(3.0);

        // Convert time to stars
        double avgTime = logs.stream().mapToDouble(logEntry -> {
            int time = logEntry.getTotalTime();
            if(time <= TIME_STAR_5) return 5;
            else if(time <= TIME_STAR_4) return 4;
            else if(time <= TIME_STAR_3) return 3;
            else if(time <= TIME_STAR_2) return 2;
            else return 1;
        }).average().orElse(3.0);

        // Round overall average to get final score
        double score = (avgDifficulty + avgDistance + avgTime) / 3.0;
        int result = (int) Math.round(score);

        log.debug("Child-friendliness for tour id={} with {} logs: {} stars", tour.getTourId(), logs.size(), result);
        return result;
    }
}
