package org.tourplanner.persistence.entity;

public record User(String userName, int totalDistance, int totalHikes, int totalRoutes) {
}