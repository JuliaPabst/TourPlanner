package org.tourplanner.viewmodel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.springframework.stereotype.Component;
import org.tourplanner.service.TourLogManager;

@Component
public class SearchBarViewModel {
    private final StringProperty searchQuery = new SimpleStringProperty("");
    private final TourListViewModel tourListViewModel;
    private final TourLogManager tourLogManager;

    public SearchBarViewModel(TourListViewModel tourListViewModel, TourLogManager tourLogManager) {
        this.tourListViewModel = tourListViewModel;
        this.tourLogManager = tourLogManager;
    }

    public StringProperty searchQueryProperty() {
        return searchQuery;
    }

    public String getSearchQuery() {
        return searchQuery.get();
    }

    public void performSearch() {
        tourListViewModel.filterByFullText(getSearchQuery(), tourLogManager);
    }
}
