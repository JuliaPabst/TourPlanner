package org.tourplanner.viewmodel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SearchBarViewModel {
    private final StringProperty searchQuery = new SimpleStringProperty("");
    private final TourListViewModel tourListViewModel;

    public StringProperty searchQueryProperty() {
        return searchQuery;
    }

    public String getSearchQuery() {
        return searchQuery.get();
    }

    public SearchBarViewModel(TourListViewModel tourListViewModel) {
        this.tourListViewModel = tourListViewModel;
    }

    public void performSearch() {
        tourListViewModel.filterByName(getSearchQuery());
    }
}
