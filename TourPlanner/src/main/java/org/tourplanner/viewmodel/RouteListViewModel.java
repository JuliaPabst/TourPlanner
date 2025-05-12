package org.tourplanner.viewmodel;

import javafx.collections.ObservableList;
import org.tourplanner.model.Route;
import org.tourplanner.service.RouteManager;

public class RouteListViewModel {
    private final RouteManager routeManager;

    public RouteListViewModel(RouteManager routeManager) {
        this.routeManager = routeManager;
    }

    public ObservableList<Route> getRoutes() {
        return routeManager.getRouteList();
    }
}
