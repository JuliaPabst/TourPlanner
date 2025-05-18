package org.tourplanner.viewmodel;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.transformation.FilteredList;
import org.tourplanner.model.Tour;
import org.tourplanner.model.TourLog;
import org.tourplanner.model.Difficulty;
import org.tourplanner.service.TourManager;
import org.tourplanner.service.TourLogManager;

public class TourListViewModel {
    private final FilteredList<Tour> filteredTours;
    private final TourManager tourManager;
    private final TourLogManager logManager;

    private final ObjectProperty<Tour> selectedTour = new SimpleObjectProperty<>();

    public TourListViewModel(TourManager tourManager, TourLogManager logManager) {
        this.filteredTours = new FilteredList<>(tourManager.getTourList(), p -> true);
        this.tourManager = tourManager;
        this.logManager = logManager;

        if (!filteredTours.isEmpty()) {
            selectedTour.set(filteredTours.getFirst());
        }
    }

    public ObjectProperty<Tour> selectedTourProperty() {
        return selectedTour;
    }

    public void selectTour(Tour tour) {
        selectedTour.set(tour);
    }

    public FilteredList<Tour> getTours() {
        return filteredTours;
    }

    public void filterByName(String query) {
        if(query == null || query.isBlank()) {
            filteredTours.setPredicate(tour -> true);
        } else {
            filteredTours.setPredicate(tour -> tour.name().toLowerCase().contains(query.toLowerCase()));
        }
    }

    public void deleteTour(Tour tour) {
        tourManager.deleteTour(tour);
        if (selectedTour.get() == tour) {
            selectedTour.set(null); // Clear selection
        }
    }

    public int getPopularity(Tour tour) {
        if(tour == null) return 0;
        long count = logManager.getLogList().stream().filter(log -> log.tour().equals(tour)).count();

        if(count >= 10) return 5;
        else if(count >= 7) return 4;
        else if(count >= 4) return 3;
        else if(count >= 2) return 2;
        else if(count >= 1) return 1;
        else return 0;
    }

    public int getChildFriendliness(Tour tour) {
        if(tour == null) return 0;
        var logs = logManager.getLogList().stream().filter(log -> log.tour().equals(tour)).toList();
        if(logs.isEmpty()) return 0;

        double avgDifficulty = logs.stream().mapToInt(log -> switch(log.difficulty()) {
            case EASY -> 1;
            case MEDIUM -> 2;
            case HARD -> 3;
        }).average().orElse(2);

        double avgDistance = logs.stream().mapToDouble(log -> log.totalDistance()).average().orElse(0);
        double avgTime = logs.stream().mapToInt(log -> log.totalTime()).average().orElse(0);

        double score = 100 - (avgDifficulty * 20 + avgDistance * 2 + avgTime * 0.5);

        if(score >= 80) return 5;
        else if(score >= 60) return 4;
        else if(score >= 40) return 3;
        else if(score >= 20) return 2;
        else return 1;
    }

    public void filterByFullText(String query, TourLogManager logManager) {
        if(query == null || query.isBlank()) {
            filteredTours.setPredicate(tour -> true);
            return;
        }

        String lowerQuery = query.toLowerCase();

        filteredTours.setPredicate(tour -> {
            boolean matchesTourName = tour.name().toLowerCase().contains(lowerQuery);

            boolean matchesLog = logManager.getLogList().stream()
                    .filter(log -> log.tour().equals(tour))
                    .anyMatch(log ->
                            (log.comment() != null && log.comment().toLowerCase().contains(lowerQuery)) ||
                                    (log.username() != null && log.username().toLowerCase().contains(lowerQuery))
                    );

            return matchesTourName || matchesLog;
        });
    }

}
