package org.tourplanner.viewmodel;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.transformation.FilteredList;
import org.tourplanner.model.Route;
import org.tourplanner.service.RouteManager;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class RouteInputViewModel {
    private final StringProperty newRouteName = new SimpleStringProperty("Default Route");
    private final IntegerProperty newRouteDistance = new SimpleIntegerProperty(0);
    private final PropertyChangeSupport routeCreatedEvent = new PropertyChangeSupport(this);

    public StringProperty newRouteNameProperty() {
        return newRouteName;
    }

    public IntegerProperty newRouteDistanceProperty() {
        return newRouteDistance;
    }

    public void createNewRoute() {
        Route newRoute = new Route(newRouteName.get(), newRouteDistance.get());
        routeCreatedEvent.firePropertyChange("newRoute", null, newRoute);

        // REset the fields
        newRouteName.set("");
        newRouteDistance.set(0);
    }
}
