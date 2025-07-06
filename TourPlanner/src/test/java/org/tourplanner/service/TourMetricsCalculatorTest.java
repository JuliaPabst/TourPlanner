
package org.tourplanner.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tourplanner.persistence.entity.Difficulty;
import org.tourplanner.persistence.entity.Tour;
import org.tourplanner.persistence.entity.TourLog;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TourMetricsCalculatorTest {

    private TourMetricsCalculator calculator;
    private Tour tour;

    @BeforeEach
    void setup() {
        calculator = new TourMetricsCalculator();
        tour = new Tour();
        tour.setTourId(1);
    }

    @Test
    void calculatePopularity_returnsCorrectStarRating_whenSufficientLogsAndHighRating() {
        // Arrange
        List<TourLog> logs = List.of(
                new TourLog(LocalDate.now(), "User1", 30, 5.0, Difficulty.EASY, 5, "", tour),
                new TourLog(LocalDate.now(), "User2", 25, 6.0, Difficulty.EASY, 4, "", tour),
                new TourLog(LocalDate.now(), "User3", 20, 3.0, Difficulty.MEDIUM, 5, "", tour),
                new TourLog(LocalDate.now(), "User4", 15, 4.0, Difficulty.MEDIUM, 5, "", tour)
        );

        // Act
        int popularity = calculator.calculatePopularity(logs, tour);

        // Assert
        assertEquals(4, popularity);
    }

    @Test
    void calculateChildFriendliness_returnsCorrectScore_basedOnLogAttributes() {
        // Arrange
        List<TourLog> logs = List.of(
                new TourLog(LocalDate.now(), "User1", 30, 3.0, Difficulty.EASY, 5, "", tour),
                new TourLog(LocalDate.now(), "User2", 40, 4.0, Difficulty.MEDIUM, 4, "", tour),
                new TourLog(LocalDate.now(), "User3", 50, 2.5, Difficulty.EASY, 4, "", tour)
        );

        // Act
        int childFriendliness = calculator.calculateChildFriendliness(logs, tour);

        // Assert
        assertEquals(4, childFriendliness);
    }

    @Test
    void calculateChildFriendliness_returnsZero_whenNoLogsMatch() {
        // Arrange
        Tour anotherTour = new Tour();
        anotherTour.setTourId(2);

        List<TourLog> logs = List.of(
                new TourLog(LocalDate.now(), "User1", 30, 3.0, Difficulty.EASY, 5, "", anotherTour)
        );

        // Act
        int result = calculator.calculateChildFriendliness(logs, tour);

        // Assert
        assertEquals(0, result);
    }
}
