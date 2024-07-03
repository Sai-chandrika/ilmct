package com.inspirage.ilct.exceptions;

public class NotBlankException extends RuntimeException {

    private String message;
    public NotBlankException(String message) {
        super(message);
    }

}
