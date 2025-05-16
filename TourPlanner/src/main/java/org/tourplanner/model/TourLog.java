package org.tourplanner.model;

import java.time.LocalDate;

public record TourLog(
        LocalDate date,
        String username,
        int totalTime,
        double totalDistance,
        Difficulty difficulty,
        int rating,
        String comment,
        Tour tour
) {}