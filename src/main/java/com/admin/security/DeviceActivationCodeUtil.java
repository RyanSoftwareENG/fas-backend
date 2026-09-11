package com.admin.security;

import java.security.SecureRandom;

public final class DeviceActivationCodeUtil {

    private static final SecureRandom RANDOM =
            new SecureRandom();

    private static final String CHARACTERS =
            "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    private DeviceActivationCodeUtil() {
    }

    public static String generate() {

        return "FAS-"
                + block()
                + "-"
                + block()
                + "-"
                + block();
    }

    private static String block() {

        StringBuilder result =
                new StringBuilder(6);

        for (int i = 0; i < 6; i++) {

            result.append(
                    CHARACTERS.charAt(
                            RANDOM.nextInt(
                                    CHARACTERS.length()
                            )
                    )
            );
        }

        return result.toString();
    }
}
