package org.tourplanner.viewmodel;

import javafx.beans.property.*;
import org.tourplanner.model.Difficulty;
import org.tourplanner.model.Tour;
import org.tourplanner.model.TourLog;

import java.time.LocalDate;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class TourLogInputViewModel {
    private final TourLogListViewModel logListViewModel;
    private final TourListViewModel tourListViewModel;

    private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>(LocalDate.now());
    private final StringProperty username = new SimpleStringProperty("");
    private final IntegerProperty totalTime = new SimpleIntegerProperty(0);
    private final DoubleProperty totalDistance = new SimpleDoubleProperty(0.0);
    private final ObjectProperty<Difficulty> difficulty = new SimpleObjectProperty<>(Difficulty.MEDIUM);
    private final IntegerProperty rating = new SimpleIntegerProperty(3);
    private final StringProperty comment = new SimpleStringProperty("");

    private final ObjectProperty<TourLog> editingLog = new SimpleObjectProperty<>(null);

    private final PropertyChangeSupport logCreatedEvent = new PropertyChangeSupport(this);
    private final PropertyChangeSupport logEditedEvent = new PropertyChangeSupport(this);

    public TourLogInputViewModel(TourLogListViewModel logListViewModel, TourListViewModel tourListViewModel) {
        this.logListViewModel = logListViewModel;
        this.tourListViewModel = tourListViewModel;
    }

    public ObjectProperty<LocalDate> dateProperty() { return date; }
    public StringProperty usernameProperty() { return username; }
    public IntegerProperty totalTimeProperty() { return totalTime; }
    public DoubleProperty totalDistanceProperty() { return totalDistance; }
    public ObjectProperty<Difficulty> difficultyProperty() { return difficulty; }
    public IntegerProperty ratingProperty() { return rating; }
    public StringProperty commentProperty() { return comment; }

    public TourLogListViewModel getLogListViewModel() { return logListViewModel; }

    public void startEditing(TourLog log) {
        editingLog.set(log);
        if(log != null) {
            date.set(log.date());
            username.set(log.username());
            totalTime.set(log.totalTime());
            totalDistance.set(log.totalDistance());
            difficulty.set(log.difficulty());
            rating.set(log.rating());
            comment.set(log.comment());
        }
    }

    public void saveOrUpdateLog() {
        validateInput();

        Tour selectedTour = tourListViewModel.selectedTourProperty().get();
        if(selectedTour == null) {
            throw new IllegalStateException("No tour selected");
        }

        TourLog newLog = new TourLog(
                date.get(),
                username.get(),
                totalTime.get(),
                totalDistance.get(),
                difficulty.get(),
                rating.get(),
                comment.get(),
                selectedTour
        );

        if(editingLog.get() != null) {
            logListViewModel.updateLog(editingLog.get(), newLog);
            fireLogEdited(newLog);
        } else {
            logListViewModel.addLog(newLog);
            fireLogCreated(newLog);
        }

        clear();
    }

    private void validateInput() {
        if(username.get().isBlank()) {
            throw new IllegalStateException("Username cannot be empty");
        }
        if(totalTime.get() <= 0 || totalDistance.get() <= 0) {
            throw new IllegalStateException("Time and distance must be greater than 0");
        }
        if(rating.get() < 1 || rating.get() > 5) {
            throw new IllegalStateException("Rating must be between 1 and 5");
        }
    }

    public void addLogCreatedListener(PropertyChangeListener listener) {
        logCreatedEvent.addPropertyChangeListener("logCreated", listener);
    }

    public void addLogEditedListener(PropertyChangeListener listener) {
        logEditedEvent.addPropertyChangeListener("logEdited", listener);
    }

    private void fireLogCreated(TourLog createdLog) {
        logCreatedEvent.firePropertyChange("logCreated", null, createdLog);
    }

    private void fireLogEdited(TourLog editedLog) {
        logEditedEvent.firePropertyChange("logEdited", null, editedLog);
    }

    public void clear() {
        date.set(LocalDate.now());
        username.set("");
        totalTime.set(0);
        totalDistance.set(0.0);
        difficulty.set(Difficulty.MEDIUM);
        rating.set(3);
        comment.set("");
        editingLog.set(null);
    }
}
