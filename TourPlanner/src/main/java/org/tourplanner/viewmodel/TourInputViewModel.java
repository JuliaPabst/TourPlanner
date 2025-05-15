package org.tourplanner.viewmodel;

import javafx.beans.property.*;
import org.tourplanner.model.Tour;
import org.tourplanner.model.TransportType;
import org.tourplanner.service.TourManager;

import java.awt.Image;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

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

    public void createNewTour() {
        Tour newTour = new Tour(
                name.get(),
                description.get(),
                from.get(),
                to.get(),
                transportType.get(),
                distance.get(),
                estimatedTime.get(),
                null
        );

        tourCreatedEvent.firePropertyChange("newTour", null, newTour);

        tourManager.createNewTour(newTour);

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
            nameProperty().set(tour.name());
            descriptionProperty().set(tour.tourDescription());
            fromProperty().set(tour.from());
            toProperty().set(tour.to());
            distanceProperty().set(tour.distance());
            estimatedTimeProperty().set(tour.estimatedTime());
            transportTypeProperty().set(tour.transportType());
        }
    }

    public void saveOrUpdateTour() {
        if (editingTour.get() == null) {
            createNewTour();
            return;
        }

        Tour updated = new Tour(
                nameProperty().get(),
                descriptionProperty().get(),
                fromProperty().get(),
                toProperty().get(),
                transportTypeProperty().get(),
                distanceProperty().get(),
                estimatedTimeProperty().get(),
                editingTour.get().routeInformation()
        );

        tourListViewModel.selectTour(updated);

        tourManager.replaceTour(editingTour.get(), updated);
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


    public void addTourCreatedListener(PropertyChangeListener listener) {
        tourCreatedEvent.addPropertyChangeListener(listener);
    }
}
