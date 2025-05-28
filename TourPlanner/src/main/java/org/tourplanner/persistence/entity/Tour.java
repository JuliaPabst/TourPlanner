package org.tourplanner.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.awt.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="tour_log")
public class Tour{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name= "tourName")
    private String tourName;
    @Column(name= "tourDescription")
    private String tourDescription;
    @Column(name= "from")
    private String from;
    @Column(name= "to")
    private String to;
    @Column(name= "transportType")
    private TransportType transportType;
    @Column(name= "distance")
    private int distance;
    @Column(name= "estimatedTime")
    private int estimatedTime;
    @Column(name= "routeInformation")
    private Image routeInformation;
}
