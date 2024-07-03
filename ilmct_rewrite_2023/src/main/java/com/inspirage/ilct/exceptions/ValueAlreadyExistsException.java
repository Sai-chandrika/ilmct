package com.inspirage.ilct.exceptions;

public class ValueAlreadyExistsException extends RuntimeException {
    private String message;
    public ValueAlreadyExistsException(String message) {
        super(message);
    }

}
