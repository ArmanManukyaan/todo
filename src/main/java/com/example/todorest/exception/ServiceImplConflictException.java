package com.example.todorest.exception;

public class ServiceImplConflictException extends RuntimeException {
    public ServiceImplConflictException() {
        super();
    }

    public ServiceImplConflictException(String message) {
        super(message);
    }

    public ServiceImplConflictException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceImplConflictException(Throwable cause) {
        super(cause);
    }

    protected ServiceImplConflictException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
