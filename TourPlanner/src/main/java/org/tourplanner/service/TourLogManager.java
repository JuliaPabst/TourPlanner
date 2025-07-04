package org.tourplanner.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tourplanner.persistence.entity.Tour;
import org.tourplanner.persistence.entity.TourLog;
import org.tourplanner.persistence.repository.TourLogRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

@Service
public class TourLogManager {
    private static final Logger log = LogManager.getLogger(TourLogManager.class);
    private final TourLogRepository tourLogRepo;

    @Getter
    private final ObservableList<TourLog> logList = FXCollections.observableArrayList();

    @Autowired
    public TourLogManager(TourLogRepository repository) {
        this.tourLogRepo = repository;
        log.debug("Initializing TourLogManager and loading existing logs");
        refreshLogList(); // Load from DB initially
        log.info("Loaded {} tour logs", logList.size());
    }

    public void addLog(TourLog logEntry) {
        log.info("Adding new tour log for tour id={} user={}", logEntry.getTour().getTourId(), logEntry.getUsername());
        TourLog savedLog = tourLogRepo.save(logEntry);
        logList.add(savedLog);
        log.debug("Tour log saved with id={}", savedLog.getTourLogId());
    }

    public void updateLog(TourLog oldLog, TourLog updatedLog) {
        log.info("Updating tour log id={}", oldLog.getTourLogId());
        updatedLog.setTourLogId(oldLog.getTourLogId());

        tourLogRepo.save(updatedLog);
        refreshLogList(); // Reload full list so FilteredList updates correctly
        log.debug("Tour log id={} updated", oldLog.getTourLogId());
    }

    public void deleteLog(TourLog logEntry) {
        log.info("Deleting tour log id={} from tour id={}", logEntry.getTourLogId(), logEntry.getTour().getTourId());
        tourLogRepo.delete(logEntry);     // Remove from DB
        logList.remove(logEntry);         // Remove from ObservableList
    }

    public void deleteLogsForTour(Tour tour) {
        if(tour == null || tour.getTourId() == null) return;

        List<TourLog> toDelete = logList.stream()
                .filter(l -> l.getTour() != null &&
                        tour.getTourId().equals(l.getTour().getTourId()))
                .toList();
        if(toDelete.isEmpty()) return;

        log.info("Deleting {} log(s) belonging to tour id={}",
                toDelete.size(), tour.getTourId());
        tourLogRepo.deleteAll(toDelete);
        logList.removeAll(toDelete);
    }

    public void refreshLogList() {
        log.debug("Refreshing tour log list from database");
        List<TourLog> allLogs = tourLogRepo.findAll();
        logList.setAll(allLogs);
        log.debug("Tour log list refreshed: {} entries", allLogs.size());
    }
}
