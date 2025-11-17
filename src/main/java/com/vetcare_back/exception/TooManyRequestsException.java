package com.vetcare_back.exception;

public class TooManyRequestsException extends RuntimeException {
    
    public TooManyRequestsException(String message) {
        super(message);
    }
}