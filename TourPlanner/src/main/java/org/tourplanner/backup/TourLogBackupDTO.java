package org.tourplanner.backup;

public record TourLogBackupDTO(
        String date,
        String username,
        int totalTime,
        double totalDistance,
        String difficulty,
        int rating,
        String comment) {}