package org.tourplanner.viewmodel;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.transformation.FilteredList;
import org.tourplanner.model.Tour;
import org.tourplanner.service.TourManager;

public class TourListViewModel {
    private final FilteredList<Tour> filteredTours;
    private final TourManager tourManager;

    public TourListViewModel(TourManager tourManager) {
        this.filteredTours = new FilteredList<>(tourManager.getTourList(), p -> true);
        this.tourManager = tourManager;

        if (!filteredTours.isEmpty()) {
            selectedTour.set(filteredTours.getFirst());
        }
    }

    private final ObjectProperty<Tour> selectedTour = new SimpleObjectProperty<>();

    public ObjectProperty<Tour> selectedTourProperty() {
        return selectedTour;
    }

    public void selectTour(Tour tour) {
        selectedTour.set(tour);
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

    public void deleteTour(Tour tour) {
        tourManager.deleteTour(tour);
        if (selectedTour.get() == tour) {
            selectedTour.set(null); // Clear selection
        }
    }
}
