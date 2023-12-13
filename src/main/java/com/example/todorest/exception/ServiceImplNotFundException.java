package com.example.todorest.exception;

public class ServiceImplNotFundException extends RuntimeException {
    public ServiceImplNotFundException() {
        super();
    }

    public ServiceImplNotFundException(String message) {
        super(message);
    }

    public ServiceImplNotFundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceImplNotFundException(Throwable cause) {
        super(cause);
    }

    protected ServiceImplNotFundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
