package com.admin.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "ADMIN_SESSION",
        schema = "FAS_MANAGEMENT"
)
public class AdminSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ADMIN_SESSION_ID")
    private Long adminSessionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "ADMIN_USER_ID",
            nullable = false
    )
    private FasAdminUser adminUser;

    @Column(
            name = "TOKEN_HASH",
            nullable = false,
            unique = true,
            length = 500
    )
    private String tokenHash;

    @Column(
            name = "CREATED_AT",
            nullable = false
    )
    private LocalDateTime createdAt;

    @Column(
            name = "EXPIRES_AT",
            nullable = false
    )
    private LocalDateTime expiresAt;

    @Column(name = "LAST_ACTIVITY_AT")
    private LocalDateTime lastActivityAt;

    @Column(
            name = "REVOKED",
            nullable = false
    )
    private Integer revoked = 0;

    // =====================================================
    // Constructors
    // =====================================================

    public AdminSession() {
    }

    // =====================================================
    // Getters / Setters
    // =====================================================

    public Long getAdminSessionId() {
        return adminSessionId;
    }

    public void setAdminSessionId(Long adminSessionId) {
        this.adminSessionId = adminSessionId;
    }

    public FasAdminUser getAdminUser() {
        return adminUser;
    }

    public void setAdminUser(FasAdminUser adminUser) {
        this.adminUser = adminUser;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public void setTokenHash(String tokenHash) {
        this.tokenHash = tokenHash;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public LocalDateTime getLastActivityAt() {
        return lastActivityAt;
    }

    public void setLastActivityAt(LocalDateTime lastActivityAt) {
        this.lastActivityAt = lastActivityAt;
    }

    public Integer getRevoked() {
        return revoked;
    }

    public void setRevoked(Integer revoked) {
        this.revoked = revoked;
    }
}