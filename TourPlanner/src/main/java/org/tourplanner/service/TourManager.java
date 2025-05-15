package org.tourplanner.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.tourplanner.model.Tour;
import org.tourplanner.model.TransportType;

import java.awt.*;

public class TourManager {

    private final ObservableList<Tour> tourList = FXCollections.observableArrayList(
            new Tour(
                    "City Explorer",
                    "A scenic city tour through the historic center.",
                    "Vienna",
                    "Vienna",
                    TransportType.BIKE,
                    12,
                    60,
                    null
            ),
            new Tour(
                    "Mountain Adventure",
                    "Hike from valley to summit with panoramic views.",
                    "Innsbruck",
                    "Hafelekarspitze",
                    TransportType.HIKE,
                    9,
                    180,
                    null
            )
    );

    public ObservableList<Tour> getTourList() {
        return tourList;
    }

    public Tour createNewTour(Tour newTour) {
        tourList.add(newTour);
        return newTour;
    }

    public void replaceTour(Tour oldTour, Tour newTour) {
        int index = tourList.indexOf(oldTour);
        if (index >= 0) {
            tourList.set(index, newTour);
        }
    }
}
