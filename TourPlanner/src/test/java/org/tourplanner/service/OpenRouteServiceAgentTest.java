package org.tourplanner.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tourplanner.persistence.entity.GeoCoord;

import static org.junit.jupiter.api.Assertions.*;

class OpenRouteServiceAgentTest {

    private OpenRouteServiceAgent serviceAgent;

    @BeforeEach
    void setUp() {
        serviceAgent = new OpenRouteServiceAgent("5b3ce3597851110001cf6248b8e1f20dec4249ed8b7c216a8d9057d5");
    }

    @Test
    void geoCode_whenFHTWPostal_thenFHTWGeoCoordsApproximate() {
        // Arrange
        String address = "Höchstädtplatz 6, 1200 Wien";

        // Act
        GeoCoord result = serviceAgent.geoCode(address);

        // Assert
        assertNotNull(result);
        assertEquals(48.239, result.lat(), 0.01); // Latitude tolerance
        assertEquals(16.378, result.lon(), 0.01); // Longitude tolerance
    }

    @Test
    void geoCode_whenZanoniAddress_thenZanoniGeoCoordsApproximate() {
        // Arrange
        String address = "Rotenturmstrasse 1, 1010 Wien";

        // Act
        GeoCoord result = serviceAgent.geoCode(address);

        // Assert
        assertNotNull(result);
        assertEquals(48.209, result.lat(), 0.01);
        assertEquals(16.373, result.lon(), 0.01);
    }

    @Test
    void directions_whenFHTWtoZanoni_thenReturnsRoute() {
        // Arrange
        GeoCoord start = new GeoCoord(16.378317, 48.238992);
        GeoCoord end = new GeoCoord(16.372924, 48.209379);

        // Act
        var result = serviceAgent.directions(OpenRouteServiceAgent.RouteType.DRIVING_CAR, start, end);

        // Assert
        assertNotNull(result);
        assertTrue(result.has("features"), "Response should have 'features' field");
        assertTrue(result.at("/features/0/properties/summary/distance").asDouble() > 0);
        assertTrue(result.at("/features/0/properties/summary/duration").asDouble() > 0);
    }
}
