package com.vetcare_back.exception;

public class UserNotFoundExeption extends RuntimeException {
  public UserNotFoundExeption(String message) {
    super(message);
  }
}
