package org.tourplanner.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tourplanner.persistence.entity.Tour;
import org.tourplanner.persistence.entity.TourLog;

import java.util.List;

public interface TourLogRepository extends JpaRepository<TourLog, Integer> {
    List<TourLog> findByTourOrderByDate(Tour tour);
}
