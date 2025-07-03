package org.tourplanner.backup;

import java.util.List;

public record TourBackupDTO(
        String tourName,
        String tourDescription,
        String from,
        String to,
        String transportType,
        List<TourLogBackupDTO> tourLogs) {}