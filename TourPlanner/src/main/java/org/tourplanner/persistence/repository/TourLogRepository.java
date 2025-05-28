package org.tourplanner.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tourplanner.persistence.entity.TourLog;

public interface TourLogRepository extends JpaRepository<TourLog, Integer> {
}
