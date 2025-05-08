package org.tourplanner.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.tourplanner.model.Route;

public class RouteManager {
    private ObservableList<Route> routeList = FXCollections.observableArrayList(
            new Route("Route 1", 5),
            new Route("Route 2", 10)
    );

    // read the list of tasks
    public ObservableList<Route> getRouteList() {
        return routeList;
    }

    // create a new task
    public Route createNewRoute(String routeName, int distance) {
        Route newRoute = new Route(routeName, distance);
        routeList.add(newRoute);
        return newRoute;
    }

}
