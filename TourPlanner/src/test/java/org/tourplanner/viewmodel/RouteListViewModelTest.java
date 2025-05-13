package org.tourplanner.viewmodel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tourplanner.model.Route;
import org.tourplanner.service.RouteManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RouteListViewModelTest {

    private RouteListViewModel viewModel;

    @BeforeEach
    void setUp() {
        RouteManager routeManager = new RouteManager();

        // Add some test data
        routeManager.getRouteList().setAll(
                new Route("Vienna Tour", 10),
                new Route("Alps Adventure", 20),
                new Route("Danube Cruise", 30),
                new Route("City Walk", 5)
        );

        viewModel = new RouteListViewModel(routeManager);
    }

    @Test
    void testInitialListContainsAllRoutes() {
        List<Route> routes = viewModel.getRoutes();
        assertEquals(4, routes.size());
    }

    @Test
    void testFilterWithMatchingQuery() {
        viewModel.filterByName("danube");
        List<Route> filtered = viewModel.getRoutes();
        assertEquals(1, filtered.size());
        assertEquals("Danube Cruise", filtered.getFirst().routeName());
    }

    @Test
    void testFilterIsCaseInsensitive() {
        viewModel.filterByName("vienna");
        List<Route> filtered = viewModel.getRoutes();
        assertEquals(1, filtered.size());
        assertEquals("Vienna Tour", filtered.getFirst().routeName());
    }

    @Test
    void testFilterWithNoMatches() {
        viewModel.filterByName("Sahara");
        List<Route> filtered = viewModel.getRoutes();
        assertTrue(filtered.isEmpty());
    }

    @Test
    void testClearFilterShowsAllAgain() {
        viewModel.filterByName("alps");
        assertEquals(1, viewModel.getRoutes().size());

        viewModel.filterByName(""); // clear filter
        assertEquals(4, viewModel.getRoutes().size());
    }
}
