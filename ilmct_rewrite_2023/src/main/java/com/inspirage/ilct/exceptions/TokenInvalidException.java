package com.inspirage.ilct.exceptions;


public class TokenInvalidException extends RuntimeException {
    private String message;
    public TokenInvalidException(String message){
        super(message);
    }

}
