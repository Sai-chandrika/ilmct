package com.inspirage.ilct.exceptions;

/**
 * @author chandrika.g
 * user
 * @ProjectName ilmct-backend
 * @since 03-11-2023
 */
public class DuplicateRequestException extends RuntimeException{
    public DuplicateRequestException(String message) {
        super(message);
    }
}
