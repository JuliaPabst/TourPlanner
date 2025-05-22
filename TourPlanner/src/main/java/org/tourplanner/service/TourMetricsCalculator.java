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

        double avgDifficulty = logs.stream().mapToInt(log -> switch(log.difficulty()) {
            case EASY -> 1;
            case MEDIUM -> 2;
            case HARD -> 3;
        }).average().orElse(2);

        double avgDistance = logs.stream().mapToDouble(TourLog::totalDistance).average().orElse(0);
        double avgTime = logs.stream().mapToInt(TourLog::totalTime).average().orElse(0);

        double score = 100 - (avgDifficulty * 20 + avgDistance * 2 + avgTime * 0.5);

        if(score >= 80) return 5;
        else if(score >= 60) return 4;
        else if(score >= 40) return 3;
        else if(score >= 20) return 2;
        else return 1;
    }
}
