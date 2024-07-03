package com.inspirage.ilct.exceptions;

/**
 * @author chandrika.g
 * user
 * @ProjectName ilmct-backend
 * @since 03-11-2023
 */
public class ValidationException extends RuntimeException{
    public ValidationException(String message) {
        super(message);
    }
}
