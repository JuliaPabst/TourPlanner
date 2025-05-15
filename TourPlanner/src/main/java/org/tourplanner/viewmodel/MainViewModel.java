package org.tourplanner.viewmodel;

import javafx.collections.ObservableList;
import org.tourplanner.model.Tour;
import org.tourplanner.service.TourManager;

public class MainViewModel {
    private final TourManager tourManager;

    public MainViewModel(TourManager tourManager, TourInputViewModel tourInputViewModel) {
        this.tourManager = tourManager;


    }

    public ObservableList<Tour> getTourList() {
        return tourManager.getTourList();
    }
}
