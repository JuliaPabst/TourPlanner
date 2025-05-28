package org.tourplanner.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tourplanner.persistence.entity.Tour;
import org.tourplanner.persistence.entity.TourLog;
import org.tourplanner.persistence.repository.TourRepository;

@Service
public class TourManager {
    TourRepository tourRepo;

    @Getter
    private final ObservableList<Tour> tourList = FXCollections.observableArrayList();
    private final TourLogManager tourLogManager;

    @Autowired
    public TourManager(TourLogManager tourLogManager, TourRepository repository) {
        this.tourLogManager = tourLogManager;
        this.tourRepo = repository;

        // Load initial tours from the database
        tourList.setAll(tourRepo.findAll());
    }

    public void createNewTour(Tour newTour) {
        tourList.add(newTour);
        tourRepo.save(newTour);
    }

    public void replaceTour(Tour oldTour, Tour newTour) {
        int index = tourList.indexOf(oldTour);
        if (index >= 0) {
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
        }
    }

    public void deleteTour(Tour tour) {
        tourList.remove(tour);
        tourRepo.delete(tour); // Delete from DB
    }
}
