package com.nouraschool.domain.exception.errors;

public class ServiceException extends RuntimeException {
    private final int statusCode;
    public ServiceException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}
