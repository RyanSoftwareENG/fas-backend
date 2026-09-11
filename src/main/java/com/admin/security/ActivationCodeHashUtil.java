package com.admin.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class ActivationCodeHashUtil {

    private ActivationCodeHashUtil() {
    }

    public static String hash(
            String value
    ) {

        if (value == null) {
            throw new IllegalArgumentException(
                    "القيمة مطلوبة"
            );
        }

        try {

            MessageDigest digest =
                    MessageDigest.getInstance(
                            "SHA-256"
                    );

            byte[] bytes =
                    digest.digest(
                            value.trim()
                                    .getBytes(
                                            StandardCharsets.UTF_8
                                    )
                    );

            StringBuilder result =
                    new StringBuilder();

            for (byte b : bytes) {

                result.append(
                        String.format(
                                "%02x",
                                b
                        )
                );
            }

            return result.toString();

        } catch (NoSuchAlgorithmException e) {

            throw new IllegalStateException(
                    "SHA-256 غير متوفر",
                    e
            );
        }
    }
}
