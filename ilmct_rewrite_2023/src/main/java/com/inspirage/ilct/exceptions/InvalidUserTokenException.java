package com.inspirage.ilct.exceptions;

public class InvalidUserTokenException extends RuntimeException {
  public InvalidUserTokenException(String message) {
    super(message);
  }
}
