package com.admin.service;

import com.admin.entity.AdminSession;
import com.admin.entity.FasAdminUser;
import com.admin.repository.AdminSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class AdminSessionService {

    private final AdminSessionRepository sessionRepository;

    private final SecureRandom secureRandom =
            new SecureRandom();

    // مدة الجلسة: 8 ساعات
    private static final long SESSION_HOURS = 8;

    public AdminSessionService(
            AdminSessionRepository sessionRepository
    ) {
        this.sessionRepository = sessionRepository;
    }

    // =====================================================
    // إنشاء Session
    // =====================================================

    @Transactional
    public String createSession(FasAdminUser user) {

        // إنشاء Token عشوائي
        byte[] tokenBytes = new byte[48];

        secureRandom.nextBytes(tokenBytes);

        String token =
                Base64.getUrlEncoder()
                        .withoutPadding()
                        .encodeToString(tokenBytes);

        // لا نخزن Token الحقيقي
        String tokenHash = sha256(token);

        LocalDateTime now =
                LocalDateTime.now();

        AdminSession session =
                new AdminSession();

        session.setAdminUser(user);
        session.setTokenHash(tokenHash);
        session.setCreatedAt(now);
        session.setExpiresAt(
                now.plusHours(SESSION_HOURS)
        );
        session.setLastActivityAt(now);
        session.setRevoked(0);

        sessionRepository.save(session);

        // نعيد Token الحقيقي للـClient فقط
        return token;
    }

    // =====================================================
    // التحقق من Session
    // =====================================================

    @Transactional
    public AdminSession validateSession(String token) {

        if (token == null || token.isBlank()) {
            return null;
        }

        String tokenHash = sha256(token);

        AdminSession session =
                sessionRepository
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

        // انتهت الجلسة
        if (!session.getExpiresAt().isAfter(now)) {

            session.setRevoked(1);

            sessionRepository.save(session);

            return null;
        }

        // تحديث آخر نشاط
        session.setLastActivityAt(now);

        sessionRepository.save(session);

        return session;
    }

    // =====================================================
    // Logout
    // =====================================================

    @Transactional
    public boolean revokeSession(String token) {

        if (token == null || token.isBlank()) {
            return false;
        }

        String tokenHash = sha256(token);

        AdminSession session =
                sessionRepository
                        .findByTokenHashAndRevoked(
                                tokenHash,
                                0
                        )
                        .orElse(null);

        if (session == null) {
            return false;
        }

        session.setRevoked(1);

        sessionRepository.save(session);

        return true;
    }

    // =====================================================
    // SHA-256
    // =====================================================

    private String sha256(String value) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash =
                    digest.digest(
                            value.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );

            StringBuilder hex =
                    new StringBuilder();

            for (byte b : hash) {

                String hexByte =
                        Integer.toHexString(
                                0xff & b
                        );

                if (hexByte.length() == 1) {
                    hex.append('0');
                }

                hex.append(hexByte);
            }

            return hex.toString();

        } catch (NoSuchAlgorithmException e) {

            throw new IllegalStateException(
                    "SHA-256 algorithm is not available",
                    e
            );
        }
    }
}