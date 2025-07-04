package org.tourplanner.viewmodel;

import javafx.beans.property.*;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.FilteredList;
import org.springframework.stereotype.Component;
import org.tourplanner.persistence.entity.Tour;
import org.tourplanner.persistence.entity.TourLog;
import org.tourplanner.service.TourManager;
import org.tourplanner.service.TourLogManager;
import org.tourplanner.service.TourMetricsCalculator;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class TourListViewModel {
    private final FilteredList<Tour> filteredTours;
    private final TourManager tourManager;
    private final TourLogManager logManager;
    private final TourMetricsCalculator metricsCalculator;

    private final ObjectProperty<Tour> selectedTour = new SimpleObjectProperty<>();
    private final BooleanProperty showNoSelectionMessage = new SimpleBooleanProperty(true);

    // UI‑refresh trigger for controllers (increments whenever logs change)
    private final IntegerProperty refreshToken = new SimpleIntegerProperty(0);

    // Display properties for View Binding
    private final StringProperty fromLabel = new SimpleStringProperty();
    private final StringProperty toLabel = new SimpleStringProperty();
    private final StringProperty transportTypeLabel = new SimpleStringProperty();
    private final StringProperty distanceLabel = new SimpleStringProperty();
    private final StringProperty timeLabel = new SimpleStringProperty();
    private final StringProperty descriptionText = new SimpleStringProperty();
    private final StringProperty popularityText = new SimpleStringProperty();
    private final StringProperty childFriendlyText = new SimpleStringProperty();
    private final StringProperty mapHtmlContent = new SimpleStringProperty();

    public TourListViewModel(TourManager tourManager, TourLogManager logManager) {
        this.filteredTours = new FilteredList<>(tourManager.getTourList(), p -> true);
        this.tourManager = tourManager;
        this.logManager = logManager;
        this.metricsCalculator = new TourMetricsCalculator();

        // Whenever a log changes, trigger a UI refresh token so Controllers can rebuild list items
        this.logManager.getLogList().addListener((ListChangeListener<TourLog>) c ->
                refreshToken.set(refreshToken.get() + 1));

        if (!filteredTours.isEmpty()) {
            selectedTour.set(filteredTours.get(0));
        }
    }

    // -- basic state --
    public ObjectProperty<Tour> selectedTourProperty() { return selectedTour; }
    public void selectTour(Tour tour) { selectedTour.set(tour); }
    public Tour getSelectedTour() { return selectedTour.get(); }

    public BooleanProperty showNoSelectionMessageProperty() { return showNoSelectionMessage; }
    public FilteredList<Tour> getTours() { return filteredTours; }
    public ReadOnlyIntegerProperty refreshTokenProperty() { return refreshToken; }

    // --- commands exposed to controllers ---
    public void clearDisplayData() {
        fromLabel.set("");
        toLabel.set("");
        transportTypeLabel.set("");
        distanceLabel.set("");
        timeLabel.set("");
        descriptionText.set("");
        popularityText.set("");
        childFriendlyText.set("");
        mapHtmlContent.set("<html><body><p>No map selected</p></body></html>");
    }

    public void deleteTour(Tour tour) {
        tourManager.deleteTour(tour);
        if (selectedTour.get() == tour) {
            selectedTour.set(null);
            clearDisplayData();
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
                    .filter(log -> log.getTour() != null &&
                            tour.getTourId() != null &&
                            tour.getTourId().equals(log.getTour().getTourId()))
                    .anyMatch(log ->
                            (log.getComment() != null && log.getComment().toLowerCase().contains(lowerQuery)) ||
                                    (log.getUsername() != null && log.getUsername().toLowerCase().contains(lowerQuery))
                    );

            return matchesTourName || matchesLog;
        });
    }

    // --- view binding getters ---
    public StringProperty fromLabelProperty() { return fromLabel; }
    public StringProperty toLabelProperty() { return toLabel; }
    public StringProperty transportTypeLabelProperty() { return transportTypeLabel; }
    public StringProperty distanceLabelProperty() { return distanceLabel; }
    public StringProperty timeLabelProperty() { return timeLabel; }
    public StringProperty descriptionTextProperty() { return descriptionText; }
    public StringProperty popularityTextProperty() { return popularityText; }
    public StringProperty childFriendlyTextProperty() { return childFriendlyText; }
    public StringProperty mapHtmlContentProperty() { return mapHtmlContent; }

    // --- helper to project Tour -> display fields ---
    public void updateDisplayData(Tour tour) {
        if (tour == null) return;

        fromLabel.set("From: " + tour.getFrom());
        toLabel.set("To: " + tour.getTo());
        transportTypeLabel.set(tour.getTransportType().getLabel());
        distanceLabel.set("Distance: " + tour.getDistance() + " km");
        timeLabel.set("Est. time: " + tour.getEstimatedTime() + " min");
        descriptionText.set(tour.getTourDescription());

        int popularity = getPopularity(tour);
        popularityText.set("Popularity: " + "★".repeat(popularity));

        int childFriendliness = getChildFriendliness(tour);
        childFriendlyText.set("Child-friendly: " + "★".repeat(childFriendliness));

        mapHtmlContent.set(generateMapHtml(tour.getRouteInformation()));
    }

    private String generateMapHtml(String routeJson) {
        if(routeJson == null || routeJson.isBlank()) {
            return "<html><body><p>No map available</p></body></html>";
        }

        try {
            URL url = getClass().getResource("/leaflet.html");
            if(url == null) throw new IOException("leaflet.html not found in resources.");

            String content = Files.readString(Paths.get(url.toURI()), StandardCharsets.UTF_8);
            return content.replace("{MY_DIRECTIONS}", routeJson);
        } catch(IOException | URISyntaxException e) {
            e.printStackTrace();
            return "<html><body><p>Error loading map</p></body></html>";
        }
    }

    // --- presentation helpers (used by controllers) ---
    public String formatDistanceAndTime(Tour tour) {
        return tour.getDistance() + " km | " + tour.getEstimatedTime() + " min";
    }

    public String formatPopularity(Tour tour) {
        return "Popularity: " + "★".repeat(getPopularity(tour));
    }
}