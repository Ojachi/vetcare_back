package com.vetcare_back.exception;

public class EmailAlreadyExistsExeption extends RuntimeException {
    public EmailAlreadyExistsExeption(String message) {
        super(message);
    }
}
