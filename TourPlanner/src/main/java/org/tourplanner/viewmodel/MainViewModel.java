package org.tourplanner.viewmodel;

import javafx.collections.ObservableList;
import org.springframework.stereotype.Component;
import org.tourplanner.persistence.entity.Tour;
import org.tourplanner.service.TourManager;

@Component
public class MainViewModel {
    private final TourManager tourManager;

    public MainViewModel(TourManager tourManager, TourInputViewModel tourInputViewModel) {
        this.tourManager = tourManager;


    }

    // create observable Array list here 
    public ObservableList<Tour> getTourList() {
        return tourManager.getTourList();
    }
}
