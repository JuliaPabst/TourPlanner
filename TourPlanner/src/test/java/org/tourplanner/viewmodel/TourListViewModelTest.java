package org.tourplanner.viewmodel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tourplanner.model.Tour;
import org.tourplanner.service.TourManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TourListViewModelTest {

    private TourListViewModel viewModel;

    @BeforeEach
    void setUp() {
        TourManager tourManager = new TourManager();

        // Add some test data
        tourManager.getTourList().setAll(
                new Tour("Vienna Tour", 10),
                new Tour("Alps Adventure", 20),
                new Tour("Danube Cruise", 30),
                new Tour("City Walk", 5)
        );

        viewModel = new TourListViewModel(tourManager);
    }

    @Test
    void testInitialListContainsAllTours() {
        List<Tour> tours = viewModel.getTours();
        assertEquals(4, tours.size());
    }

    @Test
    void testFilterWithMatchingQuery() {
        viewModel.filterByName("danube");
        List<Tour> filtered = viewModel.getTours();
        assertEquals(1, filtered.size());
        assertEquals("Danube Cruise", filtered.getFirst().tourName());
    }

    @Test
    void testFilterIsCaseInsensitive() {
        viewModel.filterByName("vienna");
        List<Tour> filtered = viewModel.getTours();
        assertEquals(1, filtered.size());
        assertEquals("Vienna Tour", filtered.getFirst().tourName());
    }

    @Test
    void testFilterWithNoMatches() {
        viewModel.filterByName("Sahara");
        List<Tour> filtered = viewModel.getTours();
        assertTrue(filtered.isEmpty());
    }

    @Test
    void testClearFilterShowsAllAgain() {
        viewModel.filterByName("alps");
        assertEquals(1, viewModel.getTours().size());

        viewModel.filterByName(""); // clear filter
        assertEquals(4, viewModel.getTours().size());
    }
}
