package org.tourplanner.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tourplanner.persistence.entity.Tour;
import org.tourplanner.persistence.entity.TourLog;
import org.tourplanner.persistence.repository.TourRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

@Service
public class TourManager {
    private static final Logger log = LogManager.getLogger(TourManager.class);
    private final TourRepository tourRepo;
    private final TourLogManager tourLogManager;

    @Getter
    private final ObservableList<Tour> tourList = FXCollections.observableArrayList();

    @Autowired
    public TourManager(TourLogManager tourLogManager, TourRepository repository) {
        this.tourLogManager = tourLogManager;
        this.tourRepo = repository;

        log.debug("Initializing TourManager and loading all tours from DB");
        List<Tour> allTours = tourRepo.findAll();
        // Load initial tours from the database
        tourList.setAll(allTours);
        log.info("Loaded {} tours", allTours.size());
    }

    public void createNewTour(Tour newTour) {
        log.info("Creating new tour: {}", newTour.getTourName());
        Tour savedTour = tourRepo.save(newTour);
        tourList.add(savedTour);
        log.debug("Tour saved with id {}", newTour.getTourId());
    }

    public void replaceTour(Tour oldTour, Tour newTour) {
        log.info("Replacing tour id={} name={}", oldTour.getTourId(), newTour.getTourName());
        int index = tourList.indexOf(oldTour);
        if(index < 0) {
            log.warn("Cannot replace tour: old tour not found in list (id={})", oldTour.getTourId());
            return;
        }

        // Ensure we update the existing DB entry, not insert a new one
        newTour.setTourId(oldTour.getTourId());
        // Persist updated tour
        Tour savedTour = tourRepo.save(newTour);
        tourList.set(index, savedTour);

        for (int i = 0; i < tourLogManager.getLogList().size(); i++) {
            TourLog log = tourLogManager.getLogList().get(i);
            if (log.getTour().equals(oldTour)) {
                TourLog updatedLog = new TourLog(
                        log.getDate(),
                        log.getUsername(),
                        log.getTotalTime(),
                        log.getTotalDistance(),
                        log.getDifficulty(),
                        log.getRating(),
                        log.getComment(),
                        savedTour
                );

                tourLogManager.updateLog(log, updatedLog);
            }
        }
        log.debug("Tour id={} updated and logs reassociated", oldTour.getTourId());
    }

    public void deleteTour(Tour tour) {
        log.info("Deleting tour id={} name={}", tour.getTourId(), tour.getTourName());
        tourList.remove(tour);
        tourRepo.delete(tour); // Delete from DB
    }
}
