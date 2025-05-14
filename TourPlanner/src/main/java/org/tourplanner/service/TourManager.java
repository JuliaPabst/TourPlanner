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

    // Creates tour from individual values
    public Tour createNewTour(String name, String tourDescription, String from, String to,
                              TransportType transportType, int distance, int estimatedTime, Image routeInformation) {
        Tour newTour = new Tour(name, tourDescription, from, to, transportType, distance, estimatedTime, routeInformation);
        tourList.add(newTour);
        return newTour;
    }

    public Tour createNewTour(Tour tour) {
        tourList.add(tour);
        return tour;
    }
}
