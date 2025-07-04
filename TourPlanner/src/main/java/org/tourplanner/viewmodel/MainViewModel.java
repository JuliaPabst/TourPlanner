package org.tourplanner.viewmodel;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import org.springframework.stereotype.Component;
import org.tourplanner.persistence.entity.Tour;
import org.tourplanner.service.*;

import java.io.IOException;
import java.nio.file.Path;

@Component
public class MainViewModel {
    private final TourManager tourManager;
    private final DialogService dialog;
    private final ReportService reports;
    private final TourListViewModel tourListViewModel;
    private final BackupService backupService;
    private final TourLogManager tourLogManager;

    public MainViewModel(TourManager tourManager,
                         DialogService dialog,
                         ReportService reports,
                         TourListViewModel tourListViewModel,
                         BackupService backupService, TourLogManager tourLogManager) {
        this.tourManager = tourManager;
        this.dialog = dialog;
        this.reports = reports;
        this.tourListViewModel = tourListViewModel;
        this.backupService = backupService;
        this.tourLogManager = tourLogManager;
    }

    // create observable Array list here
    public ObservableList<Tour> getTourList() {
        return tourManager.getTourList();
    }

    public void createTourReport() {
        Tour selectedTour = tourListViewModel.getSelectedTour();
        if(selectedTour == null) {
            dialog.showMessageBox("No tour selected", null, "Please select a tour first.");
            return;
        }

        Path target = dialog.showFileSaveDialog(
                "Save Tour Report", "PDF file", "*.pdf",
                selectedTour.getTourName() + "-report.pdf", null);
        if(target == null) return;

        Task<Void> task = getVoidTask(selectedTour, target);
        new Thread(task, "report-task").start();
    }

    private Task<Void> getVoidTask(Tour selectedTour, Path target) {
        Task<Void> task = new Task<>() {
            @Override protected Void call() throws Exception {
                reports.generateTourReport(selectedTour, target);
                return null;
            }
        };
        task.setOnSucceeded(e -> Platform.runLater(() -> {
            try {
                dialog.showFile(target);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }));
        task.setOnFailed(e -> Platform.runLater(() ->
                dialog.showMessageBox("Error", null, task.getException().getMessage())));
        return task;
    }

    public void createSummaryReport() {
        Path target = dialog.showFileSaveDialog(
                "Save Summary Report", "PDF file", "*.pdf",
                "summary-report.pdf", null);
        if(target == null) return;

        Task<Void> task = new Task<>() {
            @Override protected Void call() throws Exception {
                reports.generateSummaryReport(target);
                return null;
            }
        };
        task.setOnSucceeded(e -> Platform.runLater(() -> {
            try {
                dialog.showFile(target);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }));
        task.setOnFailed(e -> Platform.runLater(() ->
                dialog.showMessageBox("Error", null, task.getException().getMessage())));
        new Thread(task,"summary-task").start();
    }

    public void exportTours() {
        Path target = dialog.showFileSaveDialog(
                "Export Tours", "JSON file", "*.json", "tours-backup.json", null);
        if(target == null) return;

        Task<Void> task = new Task<>() {
            @Override protected Void call() throws Exception {
                backupService.exportAllTours(target);
                return null;
            }
        };
        task.setOnSucceeded(e -> Platform.runLater(() -> {
            try {
                dialog.showFile(target);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }));
        task.setOnFailed(e -> Platform.runLater(() ->
                dialog.showMessageBox("Error", null, task.getException().getMessage())));
        new Thread(task, "export-task").start();
    }

    public void importTours() {
        Path src = dialog.showFileOpenDialog(
                "Import Tours", "JSON file", "*.json", null);
        if(src == null) return;

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                backupService.importTours(src);
                return null;
            }
        };
        task.setOnSucceeded(e -> Platform.runLater(() -> {
            // Reload TourManager to update the UI
            tourManager.reloadTours();
            tourLogManager.refreshLogList();
            dialog.showMessageBox("Import finished", null,
                    "Tours were successfully imported.");
        }));
        task.setOnFailed(e -> Platform.runLater(() ->
                dialog.showMessageBox("Error", null,
                        task.getException().getMessage())));
        new Thread(task, "import-task").start();
    }
}
