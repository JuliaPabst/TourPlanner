package org.tourplanner.viewmodel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.transformation.FilteredList;
import org.tourplanner.model.Route;
import org.tourplanner.service.RouteManager;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
public class MainViewModel {
    private final RouteManager routeManager;

    private final FilteredList<Route> filteredRoutes;

    public MainViewModel(RouteManager routeManager) {
        this.routeManager = routeManager;
        this.filteredRoutes = new FilteredList<>(routeManager.getRouteList(), p -> true);
    }

    public FilteredList<Route> getFilteredRoutes() {
        return filteredRoutes;
    }

    public void filterRoutesByName(String query) {
        if (query == null || query.isBlank()) {
            filteredRoutes.setPredicate(r -> true); // show all
        } else {
            filteredRoutes.setPredicate(route ->
                    route.routeName().toLowerCase().contains(query.toLowerCase())
            );
        }
    }

    private final StringProperty newRouteName = new SimpleStringProperty("Default Route");

    public String getNewRouteName() {
        return newRouteName.get();
    }

    public StringProperty newRouteNameProperty() {
        return newRouteName;
    }

    public void setNewRouteName(String name) {
        newRouteName.set(name);
    }

    private int newRouteDistance = 10;

    public int getNewRouteDistance() {
        return newRouteDistance;
    }

    public void setNewRouteDistance(int distance) {
        this.newRouteDistance = distance;
    }

    private Route selectedRoute = null;

    public void setSelectedRouteItem(Route selectedRoute) {
        this.selectedRoute = selectedRoute;
        newRouteName.set(selectedRoute.routeName());
        newRouteDistance = selectedRoute.distance();
    }

    private final PropertyChangeSupport routeCreatedEvent = new PropertyChangeSupport(this);

    public void addRouteCreatedListener(PropertyChangeListener listener) {
        routeCreatedEvent.addPropertyChangeListener(listener);
    }

    public void createNewRoute() {
        Route route = routeManager.createNewRoute(newRouteName.get(), newRouteDistance);
        routeCreatedEvent.firePropertyChange("newRoute", null, route);
        newRouteName.set("");
        newRouteDistance = 0;
    }
}


