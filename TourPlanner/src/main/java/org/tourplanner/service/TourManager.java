package org.tourplanner.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.tourplanner.model.Tour;
import org.tourplanner.model.TransportType;

import java.awt.*;

public class TourManager {
    private ObservableList<Tour> tourList = FXCollections.observableArrayList(
            new Tour(
                    "City Explorer",
                    "A scenic city tour through the historic center.",
                    "Vienna",
                    "Vienna",
                    TransportType.BIKE,
                    12,
                    60,
                    null // Replace with actual image if available
            ),
            new Tour(
                "Mountain Adventure",
                        "Hike from valley to summit with panoramic views.",
                        "Innsbruck",
                        "Hafelekarspitze",
                TransportType.HIKE,
                9,
                        180,
                        null // Replace with actual image if available
            )
    );

    // read the list of tours
    public ObservableList<Tour> getTourList() {
        return tourList;
    }

    // create a new tour
    public Tour createNewTour(String name, String tourDescription, String from, String to, TransportType transportType, int distance, int estimatedTime, Image routeInformation) {
        Tour newTour = new Tour(name, tourDescription, from, to, transportType, distance, estimatedTime, routeInformation);
        tourList.add(newTour);
        return newTour;
    }

}
