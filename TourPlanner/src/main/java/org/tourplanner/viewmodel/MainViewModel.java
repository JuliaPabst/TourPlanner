package org.tourplanner.viewmodel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import org.tourplanner.model.Route;
import org.tourplanner.service.RouteManager;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class MainViewModel {
    private final RouteManager routeManager;

    public MainViewModel(RouteManager routeManager, RouteInputViewModel routeInputViewModel) {
        this.routeManager = routeManager;

        routeInputViewModel.addRouteCreatedListener(evt -> {
            Route route = (Route) evt.getNewValue();
            routeManager.createNewRoute(route.routeName(), route.distance());
        });
    }

    public ObservableList<Route> getRouteList() {
        return routeManager.getRouteList();
    }
}


