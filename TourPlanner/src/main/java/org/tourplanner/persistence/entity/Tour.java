package org.tourplanner.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="tour")
public class Tour{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tour_id")
    private Integer tourId;

    @Column(name= "tour_name")
    private String tourName;

    @Column(name= "tour_description")
    private String tourDescription;

    @Column(name= "from_location")
    private String from;

    @Column(name= "to_location")
    private String to;

    @Enumerated(EnumType.STRING)
    @Column(name= "transport_type")
    private TransportType transportType;

    @Column(name= "distance")
    private int distance;

    @Column(name= "estimated_time")
    private int estimatedTime;

    @Column(name= "route_information")
    private String routeInformation;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL)
    private List<TourLog> tourLogs = new ArrayList<>();
}
