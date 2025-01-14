package com.eshop.client.util;

import java.security.SecureRandom;
import java.util.Random;

public class ReferralCodeGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    private static final int CODE_LENGTH = 8;
    private static final Random RANDOM = new SecureRandom();

    public static String generateReferralCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return code.toString();
    }
}
