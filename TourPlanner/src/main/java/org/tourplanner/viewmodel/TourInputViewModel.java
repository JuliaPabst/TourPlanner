package org.tourplanner.viewmodel;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.tourplanner.model.Tour;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class TourInputViewModel {
    private final StringProperty newTourName = new SimpleStringProperty("Default Tour");
    private final IntegerProperty newTourDistance = new SimpleIntegerProperty(0);
    private final PropertyChangeSupport tourCreatedEvent = new PropertyChangeSupport(this);

    public StringProperty newTourNameProperty() {
        return newTourName;
    }

    public IntegerProperty newTourDistanceProperty() {
        return newTourDistance;
    }

    public void createNewTour() {
        Tour newTour = new Tour(newTourName.get(), newTourDistance.get());
        tourCreatedEvent.firePropertyChange("newTour", null, newTour);

        // Reset the fields
        newTourName.set("");
        newTourDistance.set(0);
    }

    public void addTourCreatedListener(PropertyChangeListener listener) {
        tourCreatedEvent.addPropertyChangeListener(listener);
    }
}
