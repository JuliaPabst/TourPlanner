package org.tourplanner.exception;

// JPA / repository
public class PersistenceException extends RuntimeException {
    public PersistenceException(String message) { super(message); }
    public PersistenceException(String message, Throwable e) { super(message, e); }
}