package com.inspirage.ilct.exceptions;

/**
 * @Author ➤➤➤ pavaniB
 * @Date ➤➤➤ 19/06/23
 * @Time ➤➤➤ 12:10 pm
 * @Project ➤➤➤ thrymr-spring-boot-generic-app
 */
public class InvalidDataException extends RuntimeException {
    public InvalidDataException(String message) {
        super(message);
    }
}
