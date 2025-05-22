package org.tourplanner.service;

import org.tourplanner.model.Tour;
import org.tourplanner.model.TourLog;
import org.tourplanner.model.Difficulty;

import java.util.List;

public class TourMetricsCalculator {
    public int calculatePopularity(List<TourLog> allLogs, Tour tour) {
        long count = allLogs.stream().filter(log -> log.tour().equals(tour)).count();

        if(count >= 10) return 5;
        else if(count >= 7) return 4;
        else if(count >= 4) return 3;
        else if(count >= 2) return 2;
        else if(count >= 1) return 1;
        else return 0;
    }

    public int calculateChildFriendliness(List<TourLog> allLogs, Tour tour) {
        var logs = allLogs.stream().filter(log -> log.tour().equals(tour)).toList();
        if(logs.isEmpty()) return 0;

        // Convert difficulty to stars
        double avgDifficulty = logs.stream().mapToInt(log -> switch(log.difficulty()) {
            case EASY -> 5;
            case MEDIUM -> 3;
            case HARD -> 1;
        }).average().orElse(3); // Default 3

        // Convert distance to stars
        double avgDistance = logs.stream().mapToDouble(log -> {
            double dist = log.totalDistance();
            if(dist <= 3) return 5;
            else if(dist <= 6) return 4;
            else if(dist <= 9) return 3;
            else if(dist <= 12) return 2;
            else return 1;
        }).average().orElse(3);

        // Convert time to stars
        double avgTime = logs.stream().mapToDouble(log -> {
            int time = log.totalTime();
            if(time <= 30) return 5;
            else if(time <= 45) return 4;
            else if(time <= 60) return 3;
            else if(time <= 90) return 2;
            else return 1;
        }).average().orElse(3);

        // Round overall average to get final score
        double score = (avgDifficulty + avgDistance + avgTime) / 3.0;
        return (int) Math.round(score);
    }
}
