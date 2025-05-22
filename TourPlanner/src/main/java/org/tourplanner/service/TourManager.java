package org.tourplanner.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.tourplanner.model.Tour;
import org.tourplanner.model.TourLog;
import org.tourplanner.model.TransportType;

import java.awt.*;

public class TourManager {

    private final ObservableList<Tour> tourList;
    private final TourLogManager tourLogManager;

    public TourManager(TourLogManager tourLogManager) {
        this.tourLogManager = tourLogManager;

        this.tourList = FXCollections.observableArrayList(
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
    }

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

        for(int i = 0; i < tourLogManager.getLogList().size(); i++) {
            TourLog log = tourLogManager.getLogList().get(i);
            if(log.tour().equals(oldTour)) {
                TourLog updatedLog = new TourLog(
                        log.date(),
                        log.username(),
                        log.totalTime(),
                        log.totalDistance(),
                        log.difficulty(),
                        log.rating(),
                        log.comment(),
                        newTour
                );
                tourLogManager.updateLog(log, updatedLog);
            }
        }
    }

    public void deleteTour(Tour tour) {
        tourList.remove(tour);
    }
}
