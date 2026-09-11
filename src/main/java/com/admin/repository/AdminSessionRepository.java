package com.admin.repository;

import com.admin.entity.AdminSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminSessionRepository
        extends JpaRepository<AdminSession, Long> {

    Optional<AdminSession> findByTokenHashAndRevoked(
            String tokenHash,
            Integer revoked
    );
}