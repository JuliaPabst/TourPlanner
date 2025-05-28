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
    @Column(name = "tour_log_id")
    private Integer tourLogId;

    @Column(name= "date")
    private LocalDate date;

    @Column(name= "username")
    private String username;

    @Column(name= "total_time")
    private int totalTime;

    @Column(name= "total_distance")
    private double totalDistance;

    @Enumerated(EnumType.STRING)
    @Column(name= "difficulty")
    private Difficulty difficulty;

    @Column(name= "rating")
    private int rating;

    @Column(name= "comment")
    private String comment;

    @ManyToOne
    @JoinColumn(name="tour_id")
    private Tour tour;

    public TourLog(LocalDate date, String username, int totalTime, double totalDistance, Difficulty difficulty, int rating, String comment, Tour savedTour) {
        this.date = date;
        this.username = username;
        this.totalTime = totalTime;
        this.totalDistance = totalDistance;
        this.difficulty = difficulty;
        this.rating = rating;
        this.comment = comment;
        this.tour = savedTour;
    }
}