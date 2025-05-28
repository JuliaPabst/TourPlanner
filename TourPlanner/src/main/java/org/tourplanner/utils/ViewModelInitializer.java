package org.tourplanner.utils;

import org.tourplanner.persistence.entity.Tour;
import org.tourplanner.persistence.entity.TourLog;
import org.tourplanner.view.util.ModalService;
import org.tourplanner.viewmodel.TourInputViewModel;
import org.tourplanner.viewmodel.TourLogInputViewModel;

public class ViewModelInitializer {

    public static void setupListeners(TourInputViewModel inputViewModel) {
        inputViewModel.addTourCreatedListener(evt -> {
            Tour created = (Tour) evt.getNewValue();
            ModalService.showInfoModal(
                    "Tour Created",
                    "Tour \"" + created.getTourName() + "\" has been successfully created."
            );
        });

        inputViewModel.addTourEditedListener(evt -> {
            Tour updated = (Tour) evt.getNewValue();
            ModalService.showInfoModal(
                    "Tour Updated",
                    "Tour \"" + updated.getTourName() + "\" has been successfully updated."
            );
        });
    }

    public static void setupListeners(TourLogInputViewModel logInputViewModel) {
        logInputViewModel.addLogCreatedListener(evt -> {
            TourLog created = (TourLog) evt.getNewValue();
            ModalService.showInfoModal(
                    "Log Created",
                    "Tour log for \"" + created.getUsername() + "\" has been successfully created."
            );
        });

        logInputViewModel.addLogEditedListener(evt -> {
            TourLog updated = (TourLog) evt.getNewValue();
            ModalService.showInfoModal(
                    "Log Updated",
                    "Tour log for \"" + updated.getUsername() + "\" has been successfully updated."
            );
        });
    }
}
