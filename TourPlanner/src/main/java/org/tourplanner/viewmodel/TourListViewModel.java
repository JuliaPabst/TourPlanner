package org.tourplanner.viewmodel;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.transformation.FilteredList;
import org.tourplanner.model.Tour;
import org.tourplanner.model.TourLog;
import org.tourplanner.model.Difficulty;
import org.tourplanner.service.TourManager;
import org.tourplanner.service.TourLogManager;
import org.tourplanner.service.TourMetricsCalculator;

public class TourListViewModel {
    private final FilteredList<Tour> filteredTours;
    private final TourManager tourManager;
    private final TourLogManager logManager;
    private final TourMetricsCalculator metricsCalculator;

    private final ObjectProperty<Tour> selectedTour = new SimpleObjectProperty<>();

    public TourListViewModel(TourManager tourManager, TourLogManager logManager) {
        this.filteredTours = new FilteredList<>(tourManager.getTourList(), p -> true);
        this.tourManager = tourManager;
        this.logManager = logManager;
        this.metricsCalculator = new TourMetricsCalculator();

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
        if (tour == null) return 0;
        return metricsCalculator.calculatePopularity(logManager.getLogList(), tour);
    }

    public int getChildFriendliness(Tour tour) {
        if(tour == null) return 0;
        return metricsCalculator.calculateChildFriendliness(logManager.getLogList(), tour);
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
