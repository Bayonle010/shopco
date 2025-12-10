package com.shopco.payment.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class Base64FormatConversion {
    @Value("${monnify.apiKey}")
    private String apiKey;

    @Value("${monnify.secretKey}")
    private String secretKay;

    public  String returnEncodeCredentials() {
        return encodeCredentials(apiKey, secretKay);
    }

    private static String encodeCredentials(String apiKey, String apiSecret) {
        String credentials = apiKey + ":" + apiSecret;
        byte[] credentialsBytes = credentials.getBytes(StandardCharsets.UTF_8);
        byte[] base64CredentialsBytes = Base64.getEncoder().encode(credentialsBytes);
        return new String(base64CredentialsBytes, StandardCharsets.UTF_8);
    }
}
