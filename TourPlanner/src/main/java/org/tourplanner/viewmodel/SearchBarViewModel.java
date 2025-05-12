package org.tourplanner.viewmodel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SearchBarViewModel {
    private final StringProperty searchQuery = new SimpleStringProperty("");

    public StringProperty searchQueryProperty() {
        return searchQuery;
    }

    public String getSearchQuery() {
        return searchQuery.get();
    }

    public void performSearch() {
        //TODO: Implement search logic
        System.out.println("Search performed: " + getSearchQuery());
    }
}
