package org.tourplanner.viewmodel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tourplanner.model.Tour;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class TourInputViewModelTest {

    private TourInputViewModel viewModel;

    @BeforeEach
    void setUp() {
        viewModel = new TourInputViewModel();
    }

    @Test
    void testInitialValues() {
        assertEquals("Default Tour", viewModel.newTourNameProperty().get());
        assertEquals(0, viewModel.newTourDistanceProperty().get());
    }

    @Test
    void testCreateNewTourFiresEvent() {
        AtomicReference<Tour> createdTour = new AtomicReference<>();

        viewModel.addTourCreatedListener(evt -> {
            if ("newTour".equals(evt.getPropertyName())) {
                createdTour.set((Tour) evt.getNewValue());
            }
        });

        viewModel.newTourNameProperty().set("Alpen Tour");
        viewModel.newTourDistanceProperty().set(50);
        viewModel.createNewTour();

        Tour tour = createdTour.get();
        assertNotNull(tour);
        assertEquals("Alpen Tour", tour.tourName());
        assertEquals(50, tour.distance());
    }

    @Test
    void testFieldsResetAfterCreateNewTour() {
        viewModel.newTourNameProperty().set("Donau Tour");
        viewModel.newTourDistanceProperty().set(80);
        viewModel.createNewTour();

        assertEquals("", viewModel.newTourNameProperty().get());
        assertEquals(0, viewModel.newTourDistanceProperty().get());
    }
}
