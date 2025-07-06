package org.tourplanner.exception;

// ViewModel / Service input checking
public class ValidationException extends RuntimeException {
    public ValidationException(String message) { super(message); }
    public ValidationException(String message, Throwable e) { super(message, e); }
}