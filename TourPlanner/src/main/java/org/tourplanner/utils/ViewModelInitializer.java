package org.tourplanner.utils;

import org.tourplanner.model.Tour;
import org.tourplanner.model.TourLog;
import org.tourplanner.view.util.ModalService;
import org.tourplanner.viewmodel.TourInputViewModel;
import org.tourplanner.viewmodel.TourLogInputViewModel;

public class ViewModelInitializer {

    public static void setupListeners(TourInputViewModel inputViewModel) {
        inputViewModel.addTourCreatedListener(evt -> {
            Tour created = (Tour) evt.getNewValue();
            ModalService.showInfoModal(
                    "Tour Created",
                    "Tour \"" + created.name() + "\" has been successfully created."
            );
        });

        inputViewModel.addTourEditedListener(evt -> {
            Tour updated = (Tour) evt.getNewValue();
            ModalService.showInfoModal(
                    "Tour Updated",
                    "Tour \"" + updated.name() + "\" has been successfully updated."
            );
        });
    }

    public static void setupListeners(TourLogInputViewModel logInputViewModel) {
        logInputViewModel.addLogCreatedListener(evt -> {
            TourLog created = (TourLog) evt.getNewValue();
            ModalService.showInfoModal(
                    "Log Created",
                    "Tour log for \"" + created.username() + "\" has been successfully created."
            );
        });

        logInputViewModel.addLogEditedListener(evt -> {
            TourLog updated = (TourLog) evt.getNewValue();
            ModalService.showInfoModal(
                    "Log Updated",
                    "Tour log for \"" + updated.username() + "\" has been successfully updated."
            );
        });
    }
}
