package org.tourplanner.viewmodel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SearchBarViewModel {
    private final StringProperty searchQuery = new SimpleStringProperty("");
    private final RouteListViewModel routeListViewModel;

    public StringProperty searchQueryProperty() {
        return searchQuery;
    }

    public String getSearchQuery() {
        return searchQuery.get();
    }

    public SearchBarViewModel(RouteListViewModel routeListViewModel) {
        this.routeListViewModel = routeListViewModel;
    }

    public void performSearch() {
        routeListViewModel.filterByName(getSearchQuery());
    }
}
