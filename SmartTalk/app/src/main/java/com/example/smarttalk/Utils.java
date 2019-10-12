package com.example.smarttalk;

import java.security.SecureRandom;

public class Utils {

    public static String generateRandomString(int characterCount) {
        final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-_";
        final SecureRandom RANDOM = new SecureRandom();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < characterCount; ++i) {
            sb.append( ALPHABET.charAt( RANDOM.nextInt( ALPHABET.length() ) ) );
        }
        return sb.toString();
    }
}
