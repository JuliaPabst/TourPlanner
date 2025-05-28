package org.tourplanner.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tourplanner.persistence.entity.TourLog;

@Service
public class TourLogManager {
    @Autowired
    public TourLogManager() {}

    private final ObservableList<TourLog> logList = FXCollections.observableArrayList();

    public ObservableList<TourLog> getLogList() {
        return logList;
    }

    public void addLog(TourLog log) {
        logList.add(log);
    }

    public void updateLog(TourLog oldLog, TourLog updatedLog) {
        int index = logList.indexOf(oldLog);
        if(index >= 0) {
            logList.set(index, updatedLog);
        }
    }

    public void deleteLog(TourLog log) {
        logList.remove(log);
    }
}
