package com.admin.service;

import com.admin.entity.UserSession;
import com.admin.repository.UserSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class UserSessionService {

    private static final int TOKEN_BYTES = 48;

    /*
     * مدة الجلسة.
     * عدّلها لاحقًا حسب سياسة FAS.
     */
    private static final long SESSION_HOURS = 24;

    private final UserSessionRepository repository;

    private final SecureRandom secureRandom =
            new SecureRandom();

    public UserSessionService(
            UserSessionRepository repository
    ) {
        this.repository =
                repository;
    }

// =====================================================
// إنشاء جلسة
// =====================================================

    @Transactional
    public SessionCreationResult createSession(
            Long userId,
            Long clinicId,
            Long deviceId
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

        if (deviceId == null) {
            throw new IllegalArgumentException(
                    "معرف الجهاز مطلوب."
            );
        }

        LocalDateTime now =
                LocalDateTime.now();

        /*
         * إلغاء الجلسة السابقة لهذا المستخدم
         * على الجهاز نفسه.
         */
        repository
                .findByUserIdAndDeviceIdAndRevoked(
                        userId,
                        deviceId,
                        0
                )
                .ifPresent(existing -> {

                    existing.setRevoked(1);

                    existing.setLastActivityAt(
                            now
                    );

                    repository.save(existing);
                });

        // -------------------------------------------------
        // إنشاء Token عشوائي
        // -------------------------------------------------

        String token =
                generateToken();

        String tokenHash =
                hashToken(token);

        UserSession session =
                new UserSession();

        session.setUserId(
                userId
        );

        session.setClinicId(
                clinicId
        );

        session.setDeviceId(
                deviceId
        );

        session.setTokenHash(
                tokenHash
        );

        session.setCreatedAt(
                now
        );

        session.setExpiresAt(
                now.plusHours(
                        SESSION_HOURS
                )
        );

        session.setLastActivityAt(
                now
        );

        session.setRevoked(
                0
        );

        UserSession saved =
                repository.save(
                        session
                );

        return new SessionCreationResult(
                token,
                saved
        );
    }

// =====================================================
// التحقق من Token
// =====================================================

    @Transactional
    public UserSession validateToken(
            String token
    ) {

        if (token == null ||
                token.isBlank()) {

            return null;
        }

        String tokenHash =
                hashToken(
                        token.trim()
                );

        UserSession session =
                repository
                        .findByTokenHashAndRevoked(
                                tokenHash,
                                0
                        )
                        .orElse(null);

        if (session == null) {
            return null;
        }

        LocalDateTime now =
                LocalDateTime.now();

        if (session.getExpiresAt() == null ||
                !session.getExpiresAt().isAfter(now)) {

            session.setRevoked(1);

            session.setLastActivityAt(
                    now
            );

            repository.save(
                    session
            );

            return null;
        }

        session.setLastActivityAt(
                now
        );

        repository.save(
                session
        );

        return session;
    }

// =====================================================
// إلغاء جلسة
// =====================================================

    @Transactional
    public boolean revokeSession(
            String token
    ) {

        if (token == null ||
                token.isBlank()) {

            return false;
        }

        String tokenHash =
                hashToken(
                        token.trim()
                );

        UserSession session =
                repository
                        .findByTokenHashAndRevoked(
                                tokenHash,
                                0
                        )
                        .orElse(null);

        if (session == null) {
            return false;
        }

        session.setRevoked(1);

        session.setLastActivityAt(
                LocalDateTime.now()
        );

        repository.save(
                session
        );

        return true;
    }

// =====================================================
// إنشاء Token
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
// SHA-256
// =====================================================

    private String hashToken(
            String token
    ) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance(
                            "SHA-256"
                    );

            byte[] hash =
                    digest.digest(
                            token.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );

            return Base64
                    .getEncoder()
                    .encodeToString(hash);

        } catch (NoSuchAlgorithmException e) {

            throw new IllegalStateException(
                    "تعذر إنشاء هاش للجلسة.",
                    e
            );
        }
    }

// =====================================================
// Result
// =====================================================

    public record SessionCreationResult(
            String token,
            UserSession session
    ) {
    }
}
