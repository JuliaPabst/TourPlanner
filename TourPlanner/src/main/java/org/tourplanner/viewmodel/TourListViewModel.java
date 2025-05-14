package org.tourplanner.viewmodel;

import javafx.collections.transformation.FilteredList;
import org.tourplanner.model.Tour;
import org.tourplanner.service.TourManager;

public class TourListViewModel {
    private final FilteredList<Tour> filteredTours;

    public TourListViewModel(TourManager tourManager) {
        this.filteredTours = new FilteredList<>(tourManager.getTourList(), p -> true);
    }

    public FilteredList<Tour> getTours() {
        return filteredTours;
    }

    public void filterByName(String query) {
        if(query == null || query.isBlank()) {
            filteredTours.setPredicate(tour -> true);
        } else {
            filteredTours.setPredicate(tour -> tour.name().toLowerCase().contains(query.toLowerCase()));
        }
    }
}
