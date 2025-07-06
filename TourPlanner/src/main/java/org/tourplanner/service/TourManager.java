package org.tourplanner.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tourplanner.exception.PersistenceException;
import org.tourplanner.persistence.entity.Tour;
import org.tourplanner.persistence.entity.TourLog;
import org.tourplanner.persistence.repository.TourRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Objects;

@Service
public class TourManager {
    private static final Logger log = LogManager.getLogger(TourManager.class);
    private final TourRepository tourRepository;
    private final TourLogManager tourLogManager;
    private final MapSnapshotService mapSnapshotService;

    @Getter
    private final ObservableList<Tour> tourList = FXCollections.observableArrayList();

    @Autowired
    public TourManager(TourLogManager tourLogManager,
                       TourRepository tourRepository,
                       MapSnapshotService mapSnapshotService) {
        this.tourLogManager = tourLogManager;
        this.tourRepository = tourRepository;
        this.mapSnapshotService = mapSnapshotService;

        log.debug("Initializing TourManager and loading all tours from DB");
        List<Tour> allTours = tourRepository.findAll();
        // Load initial tours from the database
        tourList.setAll(allTours);
        log.info("Loaded {} tours", allTours.size());
    }

    public void createNewTour(Tour newTour) {
        log.info("Creating new tour: {}", newTour.getTourName());
        try{
            Tour savedTour = tourRepository.save(newTour);
            tourList.add(savedTour);
            log.debug("Tour saved with id {}", newTour.getTourId());
        } catch(RuntimeException ex) {
            log.error("DB error while saving new tour", ex);
            throw new PersistenceException("Could not save tour to database", ex);
        }
    }

    public void replaceTour(Tour oldTour, Tour newTour) {
        log.info("Replacing tour id={} name={}", oldTour.getTourId(), newTour.getTourName());
        int index = tourList.indexOf(oldTour);
        if(index < 0) {
            log.warn("Cannot replace tour: old tour not found in list (id={})", oldTour.getTourId());
            return;
        }

        try {
            // Invalidate cached map if route changed
            if(!Objects.equals(oldTour.getRouteInformation(), newTour.getRouteInformation())) {
                mapSnapshotService.invalidateMapImage(oldTour);
            }

            // Persist updated tour
            newTour.setTourId(oldTour.getTourId());
            Tour savedTour = tourRepository.save(newTour);
            tourList.set(index, savedTour);

            for(TourLog logEntry : tourLogManager.getLogList()) {
                if(logEntry.getTour().equals(oldTour)) {
                    TourLog patched = new TourLog(
                            logEntry.getDate(),
                            logEntry.getUsername(),
                            logEntry.getTotalTime(),
                            logEntry.getTotalDistance(),
                            logEntry.getDifficulty(),
                            logEntry.getRating(),
                            logEntry.getComment(),
                            savedTour
                    );

                    tourLogManager.updateLog(logEntry, patched);
                }
            }
            log.debug("Tour id={} updated and logs reassociated", oldTour.getTourId());
        } catch(RuntimeException ex) {
            log.error("DB error while replacing tour", ex);
            throw new PersistenceException("Could not replace tour in database", ex);
        }
    }

    public void deleteTour(Tour tour) {
        log.info("Deleting tour id={} name={}", tour.getTourId(), tour.getTourName());
        try {
            mapSnapshotService.invalidateMapImage(tour); // delete PNG as well
            tourLogManager.deleteLogsForTour(tour);
            tourList.remove(tour);
            tourRepository.delete(tour); // Delete from DB
        } catch(RuntimeException ex) {
            log.error("DB error while deleting tour", ex);
            throw new PersistenceException("Could not delete tour from database", ex);
        }
    }

    public void reloadTours() {
        log.info("Refreshing tour list from database");
        try {
            List<Tour> reloadedTours = tourRepository.findAll();
            tourList.setAll(reloadedTours);
            log.debug("Tour list refreshed, {} tours loaded", reloadedTours.size());
        } catch(RuntimeException ex) {
            log.error("DB error while reloading tour list", ex);
            throw new PersistenceException("Could not reload tour list from database", ex);
        }
    }
}
