package com.inspirage.ilct.exceptions;

/**
 * @author chandrika.g
 * user
 * @ProjectName ilmct-backend
 * @since 06-11-2023
 */
public class UnauthorizedException extends RuntimeException{
    public UnauthorizedException(String message) {
        super(message);
    }
}
