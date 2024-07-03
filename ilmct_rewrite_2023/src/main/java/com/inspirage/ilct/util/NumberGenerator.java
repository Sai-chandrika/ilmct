package com.inspirage.ilct.util;


import org.apache.commons.lang3.RandomStringUtils;

public class NumberGenerator {

  public static String generateUserSesssionToken() {
    return RandomStringUtils.randomAlphabetic(64);
  }
}
