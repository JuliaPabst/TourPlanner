package org.tourplanner.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tourplanner.persistence.entity.Difficulty;
import org.tourplanner.persistence.entity.Tour;
import org.tourplanner.persistence.entity.TourLog;
import org.tourplanner.persistence.entity.TransportType;
import org.tourplanner.persistence.repository.TourLogRepository;
import org.tourplanner.persistence.repository.TourRepository;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class ReportServiceTest {

    private TourRepository tourRepository;
    private TourLogRepository logRepository;
    private ReportService reportService;

    @BeforeEach
    void setUp() throws IOException {
        tourRepository = mock(TourRepository.class);
        logRepository = mock(TourLogRepository.class);
        MapSnapshotService mapSnapshotService = mock(MapSnapshotService.class);
        reportService = new ReportService(tourRepository, logRepository, mapSnapshotService);

        // Create 'maps' directory
        Path mapDir = Path.of("maps");
        Files.createDirectories(mapDir);

        // Create a valid PNG file for tourId 99
        BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                image.setRGB(x, y, Color.GREEN.getRGB());
            }
        }

        ImageIO.write(image, "png", mapDir.resolve("99.png").toFile());
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(Path.of("maps", "99.png"));
    }

    @Test
    void generateTourReport_createsPdfFile() throws IOException {
        // Arrange
        Tour tour = new Tour();
        tour.setTourId(1);
        tour.setTourName("Test Tour");
        tour.setFrom("A");
        tour.setTo("B");
        tour.setDistance(10);
        tour.setEstimatedTime(20);
        tour.setTransportType(org.tourplanner.persistence.entity.TransportType.FOOT_HIKING);

        Path tempFile = Files.createTempFile("tourReport", ".pdf");

        when(logRepository.findByTourOrderByDate(tour)).thenReturn(Collections.emptyList());

        // Act
        reportService.generateTourReport(tour, tempFile);

        // Assert
        assertTrue(Files.exists(tempFile));

        Files.deleteIfExists(tempFile); // Cleanup
    }

    @Test
    void generateTourReport_includesMetadataAndLogEntries() throws IOException {
        // Arrange
        Tour tour = new Tour();
        tour.setTourId(99);
        tour.setTourName("Forest Hike");
        tour.setFrom("Woods");
        tour.setTo("Hill");
        tour.setDistance(12);
        tour.setEstimatedTime(90);
        tour.setTransportType(TransportType.DRIVING_CAR);

        TourLog log = new TourLog(LocalDate.of(2025, 7, 4), "Jules", 12, 90, Difficulty.MEDIUM, 4, "Nice ride", tour);

        when(logRepository.findByTourOrderByDate(tour)).thenReturn(List.of(log));
        when(logRepository.findAll()).thenReturn(List.of(log));

        Path tempFile = Files.createTempFile("tourReportFull", ".pdf");

        // Act
        reportService.generateTourReport(tour, tempFile);

        // Assert
        assertTrue(Files.exists(tempFile));

        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(tempFile.toFile()))) {
            String pageText = PdfTextExtractor.getTextFromPage(pdfDoc.getFirstPage());

            System.out.println(pageText);
            assertTrue(pageText.contains("Forest Hike"));
            assertTrue(pageText.contains("Woods"));
            assertTrue(pageText.contains("Hill"));
            assertTrue(pageText.contains("12"));      // Distance
            assertTrue(pageText.contains("90"));      // Time
            assertTrue(pageText.contains("Car"));    // Transport
            assertTrue(pageText.contains("Jules"));
            assertTrue(pageText.contains("Nice ride"));
            assertTrue(pageText.contains("Popularity"));
            assertTrue(pageText.contains("Child-friendly"));
        }

        // Cleanup
        Files.deleteIfExists(tempFile);
    }


    @Test
    void generateSummaryReport_createsPdfFileWithTable() throws IOException {
        // Arrange
        Tour tour = new Tour();
        tour.setTourName("Tour X");
        when(tourRepository.findAll()).thenReturn(List.of(tour));
        when(logRepository.findByTourOrderByDate(tour)).thenReturn(Collections.emptyList());

        Path tempFile = Files.createTempFile("summaryReport", ".pdf");

        // Act
        reportService.generateSummaryReport(tempFile);

        // Assert
        assertTrue(Files.exists(tempFile));

        Files.deleteIfExists(tempFile); // Cleanup
    }

    @Test
    void generateSummaryReport_includesCorrectAverageValues_inPdf() throws IOException {
        // Arrange
        Tour tour = new Tour();
        tour.setTourName("Sunny Trail");

        TourLog log1 = new TourLog();
        log1.setTour(tour);
        log1.setTotalDistance(10.0);
        log1.setTotalTime(30);
        log1.setRating(4);

        TourLog log2 = new TourLog();
        log2.setTour(tour);
        log2.setTotalDistance(20.0);
        log2.setTotalTime(90);
        log2.setRating(2);

        when(tourRepository.findAll()).thenReturn(List.of(tour));
        when(logRepository.findByTourOrderByDate(tour)).thenReturn(List.of(log1, log2));

        Path tempFile = Files.createTempFile("summaryReportContent", ".pdf");

        // Act
        reportService.generateSummaryReport(tempFile);

        // Extract and Assert PDF content
        try (PdfDocument pdfDoc = new PdfDocument(new com.itextpdf.kernel.pdf.PdfReader(tempFile.toFile()))) {
            String text = com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor.getTextFromPage(pdfDoc.getFirstPage());

            assertTrue(text.contains("Sunny Trail"));
            assertTrue(text.contains("15.0"));  // Avg distance
            assertTrue(text.contains("60.0"));  // Avg time
            assertTrue(text.contains("3.0"));   // Avg rating
        }

        Files.deleteIfExists(tempFile); // Cleanup
    }

}