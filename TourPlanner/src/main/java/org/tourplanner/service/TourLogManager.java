package org.tourplanner.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.tourplanner.model.TourLog;

public class TourLogManager {
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
