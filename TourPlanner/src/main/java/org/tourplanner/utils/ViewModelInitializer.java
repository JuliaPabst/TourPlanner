package org.tourplanner.utils;

import org.tourplanner.model.Tour;
import org.tourplanner.view.util.ModalService;
import org.tourplanner.viewmodel.TourInputViewModel;

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
}
