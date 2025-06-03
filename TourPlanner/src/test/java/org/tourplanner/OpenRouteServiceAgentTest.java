package org.tourplanner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OpenRouteServiceAgentTest {

    private OpenRouteServiceAgent serviceAgent;

    @BeforeEach
    void setUp() {
        serviceAgent = new OpenRouteServiceAgent("5b3ce3597851110001cf6248b8e1f20dec4249ed8b7c216a8d9057d5");
    }

    @Test
    void GeoCoord_whenFHTWPostal_thenFHTWGeoCoords(){
        // Arrange
        String postalAdresse = "Höchstädtplatz, 6, 1200 Wien";

        // Act
        GeoCoord result = serviceAgent.geoCoord(postalAdresse);

        // Assert
        assertNotNull(result);
        assertEquals(result.lat(), 16.378317);
        assertEquals(result.lon(), 48.238992);
    }

    @Test
    void GeoCoord_ZanoniPostal_thenZanoniGeoCoords(){
        // Arrange
        String postalAdresse = "Rotenturmstrasse 1, 1010 Wien";

        // Act
        GeoCoord result = serviceAgent.geoCoord(postalAdresse);

        // Assert
        assertNotNull(result);
        assertEquals(result.lat(), 16.372924);
        assertEquals(result.lon(), 48.209379);
    }

    @Test
    void directions_whenFHTWtoZanoni_thenOk(){
        // Arrange
        GeoCoord startCoord = new GeoCoord(16.378317, 48.238992);
        GeoCoord endCoord = new GeoCoord(16.372924, 48.209379);

        // Act
        var result = serviceAgent.directions(OpenRouteServiceAgent.RouteType.CAR, startCoord, endCoord);

        // Assert
         assertNotNull(result);
    }
}