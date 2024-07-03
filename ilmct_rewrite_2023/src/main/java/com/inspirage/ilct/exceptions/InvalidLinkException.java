package com.inspirage.ilct.exceptions;


public class InvalidLinkException extends RuntimeException {
    private String message;
    public InvalidLinkException(String message){
        super(message);
    }

}
