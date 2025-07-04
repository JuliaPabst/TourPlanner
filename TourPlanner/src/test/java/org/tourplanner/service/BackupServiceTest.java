package org.tourplanner.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.tourplanner.backup.TourBackupDTO;
import org.tourplanner.backup.TourLogBackupDTO;
import org.tourplanner.persistence.entity.*;
import org.tourplanner.persistence.repository.TourLogRepository;
import org.tourplanner.persistence.repository.TourRepository;
import org.tourplanner.service.BackupService;
import org.tourplanner.service.OpenRouteServiceAgent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BackupServiceTest {

    private TourRepository tourRepo;
    private TourLogRepository logRepo;
    private OpenRouteServiceAgent ors;
    private BackupService backupService;

    @BeforeEach
    void setup() {
        tourRepo = mock(TourRepository.class);
        logRepo = mock(TourLogRepository.class);
        ors = mock(OpenRouteServiceAgent.class);
        backupService = new BackupService(tourRepo, logRepo, ors);
    }

    @Test
    void exportAllTours_shouldSerializeAndWriteDataToFile() throws IOException {
        // Arrange
        Tour tour = new Tour();
        tour.setTourName("Tour A");
        tour.setTourDescription("Test Tour");
        tour.setFrom("Vienna");
        tour.setTo("Graz");
        tour.setTransportType(TransportType.FOOT_HIKING);

        TourLog log = new TourLog(LocalDate.now(), "Alice", 90, 10.5, Difficulty.MEDIUM, 4, "Great!", tour);

        when(tourRepo.findAll()).thenReturn(List.of(tour));
        when(logRepo.findByTourOrderByDate(tour)).thenReturn(List.of(log));

        Path tempFile = Path.of(File.createTempFile("test-export", ".json").toURI());

        // Act
        backupService.exportAllTours(tempFile);

        // Assert
        String content = new String(java.nio.file.Files.readAllBytes(tempFile));
        assertTrue(content.contains("Tour A"));
        assertTrue(content.contains("Alice"));
    }

    @Test
    void importTours_shouldParseJsonAndSaveToursAndLogs() throws IOException {
        // Arrange
        Path mockJsonFile = Path.of("mock-path.json");

        // Prepare fake JSON file content
        File tempFile = File.createTempFile("import-test", ".json");
        ObjectMapper mapper = new ObjectMapper();
        TourBackupDTO dto = new TourBackupDTO(
                "Tour X", "Desc", "From", "To", "FOOT_HIKING",
                List.of(new TourLogBackupDTO("2024-01-01", "Bob", 60, 5.0, "EASY", 5, "Nice tour"))
        );
        mapper.writeValue(tempFile, List.of(dto));

        // GeoCoord and direction mock
        when(ors.geoCode("From")).thenReturn(new GeoCoord(1, 1));
        when(ors.geoCode("To")).thenReturn(new GeoCoord(2, 2));
        ObjectNode directions = mapper.createObjectNode();
        ObjectNode features = mapper.createObjectNode();
        ObjectNode properties = mapper.createObjectNode();
        ObjectNode segment = mapper.createObjectNode();
        segment.put("distance", 5000.0);
        segment.put("duration", 1800.0);
        properties.set("segments", mapper.createArrayNode().add(segment));
        features.set("properties", properties);
        directions.set("features", mapper.createArrayNode().add(features));
        when(ors.directions(any(), any(), any())).thenReturn(directions);

        // Act
        backupService.importTours(tempFile.toPath());

        // Assert
        verify(tourRepo, times(1)).save(any(Tour.class));
        verify(logRepo, times(1)).save(any(TourLog.class));
    }
}
