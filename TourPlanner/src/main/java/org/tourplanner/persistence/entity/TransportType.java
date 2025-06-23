package org.tourplanner.persistence.entity;

public enum TransportType {

    DRIVING_CAR("Car"),
    DRIVING_HGV("Truck (HGV)"),
    CYCLING_REGULAR("Regular Bike"),
    CYCLING_ROAD("Road Bike"),
    CYCLING_MOUNTAIN("Mountain Bike"),
    CYCLING_ELECTRIC("Electric Bike"),
    FOOT_WALKING("Walking"),
    FOOT_HIKING("Hiking");

    private final String label;

    TransportType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
