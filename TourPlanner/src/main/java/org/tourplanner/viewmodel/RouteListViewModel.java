package org.tourplanner.viewmodel;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import org.tourplanner.model.Route;
import org.tourplanner.service.RouteManager;

public class RouteListViewModel {
    private final FilteredList<Route> filteredRoutes;

    public RouteListViewModel(RouteManager routeManager) {
        this.filteredRoutes = new FilteredList<>(routeManager.getRouteList(), p -> true);
    }

    public FilteredList<Route> getRoutes() {
        return filteredRoutes;
    }

    public void filterByName(String query) {
        if(query == null || query.isBlank()) {
            filteredRoutes.setPredicate(route -> true);
        } else {
            filteredRoutes.setPredicate(route -> route.routeName().toLowerCase().contains(query.toLowerCase()));
        }
    }
}
