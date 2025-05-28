package org.tourplanner.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tourplanner.persistence.entity.TourLog;
import org.tourplanner.persistence.repository.TourLogRepository;
import org.tourplanner.persistence.repository.TourRepository;

import java.util.List;

@Service
public class TourLogManager {
    TourLogRepository tourLogRepo;
    @Getter
    private final ObservableList<TourLog> logList = FXCollections.observableArrayList();

    @Autowired
    public TourLogManager(TourLogRepository repository) {
        this.tourLogRepo = repository;
        refreshLogList(); // Load from DB initially
    }

    public void addLog(TourLog log) {
        TourLog savedLog = tourLogRepo.save(log);
        logList.add(savedLog);
    }

    public void updateLog(TourLog oldLog, TourLog updatedLog) {
        updatedLog.setTourLogId(oldLog.getTourLogId());
        updatedLog.setTour(oldLog.getTour());

        tourLogRepo.save(updatedLog);
        refreshLogList(); // Reload full list so FilteredList updates correctly
    }

    public void deleteLog(TourLog log) {
        tourLogRepo.delete(log);     // Remove from DB
        logList.remove(log);         // Remove from ObservableList
    }

    public void refreshLogList() {
        List<TourLog> allLogs = tourLogRepo.findAll();
        logList.setAll(allLogs);
    }
}
