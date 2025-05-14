package org.tourplanner.viewmodel;

import javafx.collections.ObservableList;
import org.tourplanner.model.Tour;
import org.tourplanner.service.TourManager;

public class MainViewModel {
    private final TourManager tourManager;

    public MainViewModel(TourManager tourManager, TourInputViewModel routeInputViewModel) {
        this.tourManager = tourManager;

        routeInputViewModel.addTourCreatedListener(evt -> {
            Tour tour = (Tour) evt.getNewValue();
            tourManager.createNewTour(tour.name(), tour.distance());
        });
    }

    public ObservableList<Tour> getTourList() {
        return tourManager.getTourList();
    }
}


