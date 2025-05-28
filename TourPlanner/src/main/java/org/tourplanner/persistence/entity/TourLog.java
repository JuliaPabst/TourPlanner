package org.tourplanner.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="tour_log")
public class TourLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //replace direct calls with getters and setters
    @Column(name= "date")
    private LocalDate date;
    @Column(name= "username")
    private String username;
    @Column(name= "totalTime")
    private int totalTime;
    @Column(name= "totalDistance")
    private double totalDistance;
    @Column(name= "difficulty")
    private Difficulty difficulty;
    @Column(name= "rating")
    private int rating;
    @Column(name= "comment")
    private String comment;
    @Column(name= "tourId")
    private Integer tourId;

    // build Constructor
    public TourLog(Tour tour) {}
}