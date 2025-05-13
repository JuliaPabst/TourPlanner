package org.tourplanner.viewmodel;

import javafx.collections.ObservableList;
import org.tourplanner.model.Route;
import org.tourplanner.service.RouteManager;

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


