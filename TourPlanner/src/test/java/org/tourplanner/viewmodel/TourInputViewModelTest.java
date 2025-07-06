package org.tourplanner.viewmodel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.tourplanner.persistence.entity.GeoCoord;
import org.tourplanner.persistence.entity.Tour;
import org.tourplanner.persistence.entity.TransportType;
import org.tourplanner.service.OpenRouteServiceAgent;
import org.tourplanner.service.TourManager;
import org.tourplanner.exception.ValidationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TourInputViewModelTest {

    private TourManager tourManager;
    private OpenRouteServiceAgent orsAgent;
    private TourInputViewModel viewModel;

    @BeforeEach
    void setUp() {
        tourManager = mock(TourManager.class);
        TourListViewModel tourListViewModel = mock(TourListViewModel.class);
        orsAgent = mock(OpenRouteServiceAgent.class);
        viewModel = new TourInputViewModel(tourManager, tourListViewModel, orsAgent);
    }

    @Test
    void createNewTour_callsTourManagerWithValidTour() {
        // Arrange
        viewModel.nameProperty().set("New Tour");
        viewModel.descriptionProperty().set("Nice trip");
        viewModel.fromProperty().set("Vienna");
        viewModel.toProperty().set("Graz");
        viewModel.transportTypeProperty().set(TransportType.DRIVING_CAR);

        when(orsAgent.geoCode("Vienna")).thenReturn(new GeoCoord(48.2082, 16.3738));
        when(orsAgent.geoCode("Graz")).thenReturn(new GeoCoord(47.0707, 15.4395));

        JsonNode dummyRoute = JsonNodeFactory.instance.objectNode();
        ObjectNode featuresNode = ((ObjectNode) dummyRoute).putArray("features").addObject();
        ObjectNode properties = featuresNode.putObject("properties");
        ObjectNode segment = properties.putArray("segments").addObject();
        segment.put("distance", 120000); // meters
        segment.put("duration", 7200);   // seconds
        when(orsAgent.directions(any(), any(), any())).thenReturn(dummyRoute);

        // Act
        viewModel.createNewTour();

        // Assert
        ArgumentCaptor<Tour> captor = ArgumentCaptor.forClass(Tour.class);
        verify(tourManager).createNewTour(captor.capture());

        Tour created = captor.getValue();
        assertEquals("New Tour", created.getTourName());
        assertEquals(120, created.getDistance());       // km
        assertEquals(120, created.getEstimatedTime());  // min
    }

    @Test
    void createNewTour_throwsExceptionIfFieldsAreBlank() {
        viewModel.nameProperty().set("");
        viewModel.fromProperty().set("");
        viewModel.toProperty().set("");

        Exception exception = assertThrows(ValidationException.class, viewModel::createNewTour);
        assertEquals("Required fields must not be empty.", exception.getMessage());
    }
}
