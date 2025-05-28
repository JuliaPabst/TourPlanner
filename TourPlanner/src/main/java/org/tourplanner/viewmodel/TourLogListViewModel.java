package org.tourplanner.viewmodel;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.transformation.FilteredList;
import org.springframework.stereotype.Component;
import org.tourplanner.persistence.entity.Tour;
import org.tourplanner.persistence.entity.TourLog;
import org.tourplanner.service.TourLogManager;

@Component
public class TourLogListViewModel {
    private final TourLogManager logManager;
    private final TourListViewModel tourListViewModel;
    private final FilteredList<TourLog> filteredLogs;
    private final ObjectProperty<TourLog> selectedLog = new SimpleObjectProperty<>();

    public TourLogListViewModel(TourLogManager logManager, TourListViewModel tourListViewModel) {
        this.logManager = logManager;
        this.tourListViewModel = tourListViewModel;

        this.filteredLogs = new FilteredList<>(logManager.getLogList(), this::filterBySelectedTour);

        // Update filter when selected tour changes
        tourListViewModel.selectedTourProperty().addListener((obs, oldTour, newTour) -> {
            filteredLogs.setPredicate(this::filterBySelectedTour);
        });
    }

    private boolean filterBySelectedTour(TourLog log) {
        Tour selectedTour = tourListViewModel.selectedTourProperty().get();
        return selectedTour != null && log.tour().equals(selectedTour);
    }

    public FilteredList<TourLog> getLogs() {
        return filteredLogs;
    }

    public ObjectProperty<TourLog> selectedLogProperty() {
        return selectedLog;
    }

    public void selectLog(TourLog log) {
        selectedLog.set(log);
    }

    public void deleteLog(TourLog log) {
        logManager.deleteLog(log);
        if(selectedLog.get() == log) {
            selectedLog.set(null);
        }
    }

    public void addLog(TourLog log) {
        logManager.addLog(log);
        selectLog(log);
    }

    public void updateLog(TourLog oldLog, TourLog newLog) {
        logManager.updateLog(oldLog, newLog);
        selectLog(newLog);
    }
}
