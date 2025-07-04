package org.tourplanner.service;

import org.junit.jupiter.api.*;
import org.tourplanner.persistence.entity.Tour;
import org.tourplanner.service.MapSnapshotService;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class MapSnapshotServiceTest {

    private MapSnapshotService mapSnapshotService;

    @BeforeEach
    void setup() throws Exception {
        mapSnapshotService = new MapSnapshotService();

        // Access and invoke private init() method
        var initMethod = MapSnapshotService.class.getDeclaredMethod("init");
        initMethod.setAccessible(true);
        initMethod.invoke(mapSnapshotService);
    }


    @Test
    void invalidateMapImage_deletesExistingFile() throws IOException {
        // Arrange
        Tour tour = new Tour();
        tour.setTourId(42);

        Path imgPath = Paths.get("maps/42.png");
        Files.createDirectories(imgPath.getParent());
        Files.createFile(imgPath);
        assertTrue(Files.exists(imgPath), "Test precondition: file should exist");

        // Act
        mapSnapshotService.invalidateMapImage(tour);

        // Assert
        assertFalse(Files.exists(imgPath), "The file should have been deleted");
    }

    @Test
    void ensureMapImage_skipsIfRouteInfoIsNullOrFileExists() throws IOException {
        // Arrange
        Tour tour = new Tour();
        tour.setTourId(99);
        tour.setRouteInformation(null);  // no route info
        Path path = Paths.get("maps/99.png");

        // Act
        mapSnapshotService.ensureMapImage(tour);

        // Assert
        assertFalse(Files.exists(path), "No image should be created if routeInformation is null");

        // Arrange again: simulate already existing image
        Files.createFile(path);

        // Act again: call should skip creation
        mapSnapshotService.ensureMapImage(tour);

        // Assert again: file still exists, nothing overwritten
        assertTrue(Files.exists(path), "Image file should remain unchanged when it already exists");
    }
}
