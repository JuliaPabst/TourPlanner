package org.tourplanner.viewmodel;

import javafx.beans.property.*;
import org.springframework.stereotype.Component;
import org.tourplanner.persistence.entity.Tour;
import org.tourplanner.persistence.entity.TransportType;
import org.tourplanner.service.TourManager;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

@Component
public class TourInputViewModel {
    private final TourManager tourManager;

    private final TourListViewModel tourListViewModel;

    private final ObjectProperty<Tour> editingTour = new SimpleObjectProperty<>(null);

    private final StringProperty name = new SimpleStringProperty("");
    private final StringProperty description = new SimpleStringProperty("");
    private final StringProperty from = new SimpleStringProperty("");
    private final StringProperty to = new SimpleStringProperty("");
    private final ObjectProperty<TransportType> transportType = new SimpleObjectProperty<>(TransportType.BIKE);
    private final IntegerProperty distance = new SimpleIntegerProperty(0);
    private final IntegerProperty estimatedTime = new SimpleIntegerProperty(0);

    private final PropertyChangeSupport tourCreatedEvent = new PropertyChangeSupport(this);
    private final PropertyChangeSupport tourEditedEvent = new PropertyChangeSupport(this);

    public TourInputViewModel(TourManager tourManager, TourListViewModel tourListViewModel) {
        this.tourManager = tourManager;
        this.tourListViewModel = tourListViewModel;
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

        Tour newTour = new Tour(
                null, // tourId (null for new)
                name.get(),
                description.get(),
                from.get(),
                to.get(),
                transportType.get(),
                distance.get(),
                estimatedTime.get(),
                null, // routeInformation
                new ArrayList<>() // tourLogs
        );

        tourManager.createNewTour(newTour);

        fireTourCreated(newTour);

        // Reset fields
        name.set("");
        description.set("");
        from.set("");
        to.set("");
        transportType.set(TransportType.BIKE);
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

        Tour updated = new Tour(
                null,
                nameProperty().get(),
                descriptionProperty().get(),
                fromProperty().get(),
                toProperty().get(),
                transportTypeProperty().get(),
                distanceProperty().get(),
                estimatedTimeProperty().get(),
                editingTour.get().getRouteInformation(),
                new ArrayList<>()
        );

        tourManager.replaceTour(editingTour.get(), updated);
        tourListViewModel.selectTour(updated);

        fireTourEdited(updated);

        editingTour.set(null); // clear edit mode
        resetFields();
    }

    public void resetFields() {
        nameProperty().set("");
        descriptionProperty().set("");
        fromProperty().set("");
        toProperty().set("");
        distanceProperty().set(0);
        estimatedTimeProperty().set(0);
        transportTypeProperty().set(TransportType.HIKE);
        editingTour.set(null);
    }

    private void validateInput() {
        if (name.get().isBlank() || from.get().isBlank() || to.get().isBlank()) {
            throw new IllegalArgumentException("Required fields must not be empty.");
        }

        if (distance.get() <= 0) {
            throw new IllegalArgumentException("Distance must be greater than 0.");
        }

        if (estimatedTime.get() <= 0) {
            throw new IllegalArgumentException("Estimated time must be greater than 0.");
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
