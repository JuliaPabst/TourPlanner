package org.tourplanner.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tourplanner.persistence.entity.Tour;

public interface TourRepository extends JpaRepository<Tour, Integer> {
}
