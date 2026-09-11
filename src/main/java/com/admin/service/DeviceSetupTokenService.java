package com.admin.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DeviceSetupTokenService {

    /*
     * مدة صلاحية Setup Token.
     */
    private static final long SETUP_TOKEN_MINUTES = 5;

    /*
     * عدد البايتات العشوائية.
     * 32 بايت = 256-bit random token.
     */
    private static final int TOKEN_BYTES = 32;

    private final SecureRandom secureRandom =
            new SecureRandom();

    private final Map<String, PendingSetup> pendingSetups =
            new ConcurrentHashMap<>();

    // =====================================================
    // إنشاء Setup Token
    // =====================================================

    public String create(
            Long userId,
            Long clinicId,
            String installationId
    ) {

        if (userId == null) {
            throw new IllegalArgumentException(
                    "معرف المستخدم مطلوب."
            );
        }

        if (clinicId == null) {
            throw new IllegalArgumentException(
                    "معرف العيادة مطلوب."
            );
        }

        if (installationId == null ||
                installationId.isBlank()) {

            throw new IllegalArgumentException(
                    "معرف التثبيت مطلوب."
            );
        }

        /*
         * تنظيف الرموز القديمة من الذاكرة.
         */
        cleanupExpired();

        String token =
                generateToken();

        LocalDateTime expiresAt =
                LocalDateTime.now()
                        .plusMinutes(
                                SETUP_TOKEN_MINUTES
                        );

        PendingSetup setup =
                new PendingSetup(
                        userId,
                        clinicId,
                        installationId,
                        expiresAt
                );

        pendingSetups.put(
                token,
                setup
        );

        return token;
    }

    // =====================================================
    // الحصول على عملية Setup
    // =====================================================

    public PendingSetup get(
            String setupToken
    ) {

        if (setupToken == null ||
                setupToken.isBlank()) {

            return null;
        }

        PendingSetup setup =
                pendingSetups.get(
                        setupToken.trim()
                );

        if (setup == null) {
            return null;
        }

        if (!setup.expiresAt()
                .isAfter(LocalDateTime.now())) {

            pendingSetups.remove(
                    setupToken.trim()
            );

            return null;
        }

        return setup;
    }

    // =====================================================
    // إلغاء Setup Token
    // =====================================================

    public void invalidate(
            String setupToken
    ) {

        if (setupToken == null ||
                setupToken.isBlank()) {

            return;
        }

        pendingSetups.remove(
                setupToken.trim()
        );
    }

    // =====================================================
    // إنشاء Token عشوائي
    // =====================================================

    private String generateToken() {

        byte[] bytes =
                new byte[TOKEN_BYTES];

        secureRandom.nextBytes(
                bytes
        );

        return Base64
                .getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    // =====================================================
    // تنظيف الرموز المنتهية
    // =====================================================

    private void cleanupExpired() {

        LocalDateTime now =
                LocalDateTime.now();

        pendingSetups.entrySet()
                .removeIf(entry ->
                        !entry.getValue()
                                .expiresAt()
                                .isAfter(now)
                );
    }

    // =====================================================
    // Pending Setup
    // =====================================================

    public record PendingSetup(
            Long userId,
            Long clinicId,
            String installationId,
            LocalDateTime expiresAt
    ) {
    }
}