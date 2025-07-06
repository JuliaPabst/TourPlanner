package org.tourplanner.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tourplanner.exception.PersistenceException;
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
        try {
            TourLog savedLog = tourLogRepo.save(logEntry);
            logList.add(savedLog);
            log.debug("Tour log saved with id={}", savedLog.getTourLogId());
        } catch(RuntimeException ex) {
            log.error("DB error while saving tour log", ex);
            throw new PersistenceException("Could not save tour llog to database", ex);
        }
    }

    public void updateLog(TourLog oldLog, TourLog updatedLog) {
        log.info("Updating tour log id={}", oldLog.getTourLogId());
        try {
            updatedLog.setTourLogId(oldLog.getTourLogId());
            tourLogRepo.save(updatedLog);
            refreshLogList(); // Reload full list so FilteredList updates correctly
            log.debug("Tour log id={} updated", oldLog.getTourLogId());
        } catch(RuntimeException ex) {
            log.error("DB error while updating tour log", ex);
            throw new PersistenceException("Could not update tour log to database", ex);
        }
    }

    public void deleteLog(TourLog logEntry) {
        log.info("Deleting tour log id={} from tour id={}", logEntry.getTourLogId(), logEntry.getTour().getTourId());
        try {
            tourLogRepo.delete(logEntry);     // Remove from DB
            logList.remove(logEntry);         // Remove from ObservableList
            log.debug("Tour log id={} deleted", logEntry.getTourLogId());
        } catch(RuntimeException ex) {
            log.error("DB error while deleting tour log", ex);
            throw new PersistenceException("Could not delete tour log from database", ex);
        }
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

        try {
            tourLogRepo.deleteAll(toDelete);
            logList.removeAll(toDelete);
            log.debug("Deleted {} logs for tour id={}", toDelete.size(), tour.getTourId());
        } catch(RuntimeException ex) {
            log.error("DB error while deleting tour logs for tour id={}", tour.getTourId(), ex);
            throw new PersistenceException("Could not delete tour logs for tour from database", ex);
        }
    }

    public void refreshLogList() {
        log.debug("Refreshing tour log list from database");
        try {
            List<TourLog> allLogs = tourLogRepo.findAll();
            logList.setAll(allLogs);
            log.debug("Tour log list refreshed: {} entries", allLogs.size());
        } catch(RuntimeException ex) {
            log.error("DB error while refreshing tour log list", ex);
            throw new PersistenceException("Could not refresh tour log list from database", ex);
        }
    }
}
