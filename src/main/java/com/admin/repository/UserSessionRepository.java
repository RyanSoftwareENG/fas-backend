package com.admin.repository;

import com.admin.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserSessionRepository
        extends JpaRepository<UserSession, Long> {

    Optional<UserSession> findByTokenHash(
            String tokenHash
    );

    Optional<UserSession> findByTokenHashAndRevoked(
            String tokenHash,
            Integer revoked
    );

    Optional<UserSession> findBySessionIdAndRevoked(
            Long sessionId,
            Integer revoked
    );
     Optional<UserSession> findByUserIdAndDeviceIdAndRevoked( Long userId, Long deviceId, Integer revoked );

}
