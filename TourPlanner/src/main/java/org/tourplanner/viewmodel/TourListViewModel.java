package org.tourplanner.viewmodel;

import javafx.beans.property.*;
import javafx.collections.transformation.FilteredList;
import org.springframework.stereotype.Component;
import org.tourplanner.persistence.entity.Tour;
import org.tourplanner.service.TourManager;
import org.tourplanner.service.TourLogManager;
import org.tourplanner.service.TourMetricsCalculator;

@Component
public class TourListViewModel {
    private final FilteredList<Tour> filteredTours;
    private final TourManager tourManager;
    private final TourLogManager logManager;
    private final TourMetricsCalculator metricsCalculator;

    private final ObjectProperty<Tour> selectedTour = new SimpleObjectProperty<>();
    private final BooleanProperty showNoSelectionMessage = new SimpleBooleanProperty(true);

    // Display properties for View Binding
    private final StringProperty fromLabel = new SimpleStringProperty();
    private final StringProperty toLabel = new SimpleStringProperty();
    private final StringProperty transportTypeLabel = new SimpleStringProperty();
    private final StringProperty distanceLabel = new SimpleStringProperty();
    private final StringProperty timeLabel = new SimpleStringProperty();
    private final StringProperty descriptionText = new SimpleStringProperty();
    private final StringProperty popularityText = new SimpleStringProperty();
    private final StringProperty childFriendlyText = new SimpleStringProperty();

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

    public BooleanProperty showNoSelectionMessageProperty() { return showNoSelectionMessage; }

    public FilteredList<Tour> getTours() {
        return filteredTours;
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
            boolean matchesTourName = tour.getTourName().toLowerCase().contains(lowerQuery);

            boolean matchesLog = logManager.getLogList().stream()
                    .filter(log -> log.getTour().equals(tour))
                    .anyMatch(log ->
                            (log.getComment() != null && log.getComment().toLowerCase().contains(lowerQuery)) ||
                                    (log.getUsername() != null && log.getUsername().toLowerCase().contains(lowerQuery))
                    );

            return matchesTourName || matchesLog;
        });
    }

    // View Bindings
    public StringProperty fromLabelProperty() { return fromLabel; }
    public StringProperty toLabelProperty() { return toLabel; }
    public StringProperty transportTypeLabelProperty() { return transportTypeLabel; }
    public StringProperty distanceLabelProperty() { return distanceLabel; }
    public StringProperty timeLabelProperty() { return timeLabel; }
    public StringProperty descriptionTextProperty() { return descriptionText; }
    public StringProperty popularityTextProperty() { return popularityText; }
    public StringProperty childFriendlyTextProperty() { return childFriendlyText; }

    public void updateDisplayData(Tour tour) {
        if(tour == null) return;

        fromLabel.set("From: " + tour.getFrom());
        toLabel.set("To: " + tour.getTo());
        transportTypeLabel.set(tour.getTransportType().name());
        distanceLabel.set("Distance: " + tour.getDistance() + " km");
        timeLabel.set("Est. time: " + tour.getEstimatedTime() + " min");
        descriptionText.set(tour.getTourDescription());

        int popularity = getPopularity(tour);
        popularityText.set("Popularity: " + "★".repeat(popularity));

        int childFriendliness = getChildFriendliness(tour);
        childFriendlyText.set("Child-friendly: " + "★".repeat(childFriendliness));
    }
}
