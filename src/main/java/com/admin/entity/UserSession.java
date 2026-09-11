package com.admin.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "USER_SESSION",
        schema = "FAS_MANAGEMENT"
)
public class UserSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SESSION_ID")
    private Long sessionId;

    @Column(
            name = "USER_ID",
            nullable = false
    )
    private Long userId;

    @Column(
            name = "CLINIC_ID",
            nullable = false
    )
    private Long clinicId;

    @Column(name = "DEVICE_ID")
    private Long deviceId;

    @Column(
            name = "TOKEN_HASH",
            nullable = false,
            length = 500,
            unique = true
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
    private Integer revoked;

    public UserSession() {
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getClinicId() {
        return clinicId;
    }

    public void setClinicId(Long clinicId) {
        this.clinicId = clinicId;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
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