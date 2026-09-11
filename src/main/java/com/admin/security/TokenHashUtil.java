package com.admin.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class TokenHashUtil {

    private TokenHashUtil() {
    }

    public static String sha256(String value) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash =
                    digest.digest(
                            value.getBytes(StandardCharsets.UTF_8)
                    );

            StringBuilder hex = new StringBuilder();

            for (byte b : hash) {
                hex.append(
                        String.format("%02x", b)
                );
            }

            return hex.toString();

        } catch (NoSuchAlgorithmException e) {

            throw new IllegalStateException(
                    "SHA-256 is not available",
                    e
            );
        }
    }
}