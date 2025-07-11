package org.tourplanner.viewmodel;

import com.fasterxml.jackson.databind.JsonNode;
import javafx.beans.property.*;
import org.springframework.stereotype.Component;
import org.tourplanner.persistence.entity.GeoCoord;
import org.tourplanner.service.OpenRouteServiceAgent;
import org.tourplanner.persistence.entity.Tour;
import org.tourplanner.persistence.entity.TransportType;
import org.tourplanner.service.TourManager;
import org.tourplanner.exception.ValidationException;
import org.tourplanner.exception.RoutingException;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

@Component
public class TourInputViewModel {
    private final TourManager tourManager;

    private final TourListViewModel tourListViewModel;

    private final OpenRouteServiceAgent orsAgent;

    private final ObjectProperty<Tour> editingTour = new SimpleObjectProperty<>(null);

    private String routeJsonString;

    private final StringProperty name = new SimpleStringProperty("");
    private final StringProperty description = new SimpleStringProperty("");
    private final StringProperty from = new SimpleStringProperty("");
    private final StringProperty to = new SimpleStringProperty("");
    private final ObjectProperty<TransportType> transportType = new SimpleObjectProperty<>(TransportType.FOOT_HIKING);
    private final IntegerProperty distance = new SimpleIntegerProperty(0);
    private final IntegerProperty estimatedTime = new SimpleIntegerProperty(0);

    private final PropertyChangeSupport tourCreatedEvent = new PropertyChangeSupport(this);
    private final PropertyChangeSupport tourEditedEvent = new PropertyChangeSupport(this);

    public TourInputViewModel(TourManager tourManager, TourListViewModel tourListViewModel, OpenRouteServiceAgent orsAgent) {
        this.tourManager = tourManager;
        this.tourListViewModel = tourListViewModel;
        this.orsAgent = orsAgent;
        this.routeJsonString = "";
    }

    public StringProperty nameProperty() { return name; }
    public StringProperty descriptionProperty() { return description; }
    public StringProperty fromProperty() { return from; }
    public StringProperty toProperty() { return to; }
    public ObjectProperty<TransportType> transportTypeProperty() { return transportType; }
    public IntegerProperty distanceProperty() { return distance; }
    public IntegerProperty estimatedTimeProperty() { return estimatedTime; }

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public void createNewTour() {
        validateInput();
        calculateAndSetRouteMetrics();

        Tour newTour = new Tour(
                null, // tourId
                name.get(),
                description.get(),
                from.get(),
                to.get(),
                transportType.get(),
                distance.get(),
                estimatedTime.get(),
                routeJsonString,
                new ArrayList<>()
        );


        tourManager.createNewTour(newTour);
        tourListViewModel.selectTour(newTour);

        fireTourCreated(newTour);

        // Reset fields
        name.set("");
        description.set("");
        from.set("");
        to.set("");
        transportType.set(TransportType.FOOT_HIKING);
        distance.set(0);
        estimatedTime.set(0);
    }

    public void startEditing(Tour tour) {
        editingTour.set(tour);
        if (tour != null) {
            nameProperty().set(tour.getTourName());
            descriptionProperty().set(tour.getTourDescription());
            fromProperty().set(tour.getFrom());
            toProperty().set(tour.getTo());
            distanceProperty().set(tour.getDistance());
            estimatedTimeProperty().set(tour.getEstimatedTime());
            transportTypeProperty().set(tour.getTransportType());
        }
    }

    public void saveOrUpdateTour() {
        validateInput();
        if (editingTour.get() == null) {
            createNewTour();
            return;
        }

        calculateAndSetRouteMetrics();

        Tour updatedTour = new Tour(
                null, // tourId
                name.get(),
                description.get(),
                from.get(),
                to.get(),
                transportType.get(),
                distance.get(),
                estimatedTime.get(),
                routeJsonString,
                new ArrayList<>()
        );


        Tour savedTour = tourManager.replaceTour(editingTour.get(), updatedTour);
        tourListViewModel.selectTour(savedTour);

        fireTourEdited(updatedTour);

        editingTour.set(null); // clear edit mode
        resetFields();
    }

    private void calculateAndSetRouteMetrics() {
        try {
            GeoCoord start = orsAgent.geoCode(from.get());
            GeoCoord end = orsAgent.geoCode(to.get());

            JsonNode routeJson = orsAgent.directions(mapToRouteType(transportType.get()), start, end);

            routeJsonString = routeJson.toString(); // Save the full JSON string for DB

            int newDistance = (int) (routeJson.get("features").get(0)
                    .get("properties").get("segments").get(0)
                    .get("distance").asDouble() / 1000);
            int newTime = (int) (routeJson.get("features").get(0)
                    .get("properties").get("segments").get(0)
                    .get("duration").asDouble() / 60);

            distance.set(newDistance);
            estimatedTime.set(newTime);
        } catch (RoutingException ex) {
            throw new ValidationException(ex.getMessage(), ex);
        }
    }


    private OpenRouteServiceAgent.RouteType mapToRouteType(TransportType transportType) {
        return switch (transportType) {
            case DRIVING_CAR -> OpenRouteServiceAgent.RouteType.DRIVING_CAR;
            case DRIVING_HGV -> OpenRouteServiceAgent.RouteType.DRIVING_HGV;
            case CYCLING_REGULAR -> OpenRouteServiceAgent.RouteType.CYCLING_REGULAR;
            case CYCLING_ROAD -> OpenRouteServiceAgent.RouteType.CYCLING_ROAD;
            case CYCLING_MOUNTAIN -> OpenRouteServiceAgent.RouteType.CYCLING_MOUNTAIN;
            case CYCLING_ELECTRIC -> OpenRouteServiceAgent.RouteType.CYCLING_ELECTRIC;
            case FOOT_WALKING -> OpenRouteServiceAgent.RouteType.FOOT_WALKING;
            case FOOT_HIKING -> OpenRouteServiceAgent.RouteType.FOOT_HIKING;
        };
    }

    public void resetFields() {
        nameProperty().set("");
        descriptionProperty().set("");
        fromProperty().set("");
        toProperty().set("");
        distanceProperty().set(0);
        estimatedTimeProperty().set(0);
        transportTypeProperty().set(TransportType.FOOT_HIKING);
        routeJsonString = "";
        editingTour.set(null);
    }

    private void validateInput() {
        if (name.get().isBlank() || from.get().isBlank() || to.get().isBlank()) {
            throw new ValidationException("Required fields must not be empty.");
        }
    }

    public void addTourCreatedListener(PropertyChangeListener listener) {
        tourCreatedEvent.addPropertyChangeListener(listener);
    }

    private void fireTourCreated(Tour createdTour) {
        tourCreatedEvent.firePropertyChange("newTour", null, createdTour);
    }

    public void addTourEditedListener(PropertyChangeListener listener) {
        tourEditedEvent.addPropertyChangeListener("tourEdited", listener);
    }

    private void fireTourEdited(Tour updatedTour) {
        tourEditedEvent.firePropertyChange("tourEdited", null, updatedTour);
    }
}
