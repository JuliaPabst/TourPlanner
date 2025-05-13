package org.tourplanner.viewmodel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tourplanner.model.Route;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class RouteInputViewModelTest {

    private RouteInputViewModel viewModel;

    @BeforeEach
    void setUp() {
        viewModel = new RouteInputViewModel();
    }

    @Test
    void testInitialValues() {
        assertEquals("Default Route", viewModel.newRouteNameProperty().get());
        assertEquals(0, viewModel.newRouteDistanceProperty().get());
    }

    @Test
    void testCreateNewRouteFiresEvent() {
        AtomicReference<Route> createdRoute = new AtomicReference<>();

        viewModel.addRouteCreatedListener(evt -> {
            if ("newRoute".equals(evt.getPropertyName())) {
                createdRoute.set((Route) evt.getNewValue());
            }
        });

        viewModel.newRouteNameProperty().set("Alpen Route");
        viewModel.newRouteDistanceProperty().set(50);
        viewModel.createNewRoute();

        Route route = createdRoute.get();
        assertNotNull(route);
        assertEquals("Alpen Route", route.routeName());
        assertEquals(50, route.distance());
    }

    @Test
    void testFieldsResetAfterCreateNewRoute() {
        viewModel.newRouteNameProperty().set("Donau Tour");
        viewModel.newRouteDistanceProperty().set(80);
        viewModel.createNewRoute();

        assertEquals("", viewModel.newRouteNameProperty().get());
        assertEquals(0, viewModel.newRouteDistanceProperty().get());
    }
}
