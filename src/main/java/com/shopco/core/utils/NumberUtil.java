package com.shopco.core.utils;

import java.security.SecureRandom;
import java.util.Random;

public class NumberUtil {
    public static String generateNumericOtp() {
        Random random = new Random();
        int otpLength = 6;
        StringBuilder otpBuilder = new StringBuilder();

        for (int i = 0; i < otpLength; i++) {
            int digit = random.nextInt(10);
            otpBuilder.append(digit);
        }
        return otpBuilder.toString();
    }
}
