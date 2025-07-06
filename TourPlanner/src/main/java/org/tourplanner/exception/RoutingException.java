package org.tourplanner.exception;

// OpenRouteServiceAgent & MapSnapshotService
public class RoutingException extends RuntimeException {
    public RoutingException(String message) { super(message); }
    public RoutingException(String message, Throwable e) { super(message, e); }
}
