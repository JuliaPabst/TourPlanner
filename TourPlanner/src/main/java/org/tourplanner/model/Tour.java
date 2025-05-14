package org.tourplanner.model;

import java.awt.*;

public record Tour(String name, String tourDescription, String from, String to, TransportType transportType, int distance,
                   int estimatedTime, Image routeInformation) {
}