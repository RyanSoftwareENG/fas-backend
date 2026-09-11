package com.admin.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "DEVICE_ACTIVATION_CODE",
        schema = "FAS_MANAGEMENT"
)
public class DeviceActivationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ACTIVATION_CODE_ID")
    private Long activationCodeId;

    @Column(
            name = "SUBSCRIPTION_ID",
            nullable = false
    )
    private Long subscriptionId;

    @Column(
            name = "CLINIC_ID",
            nullable = false
    )
    private Long clinicId;

    @Column(
            name = "CODE_HASH",
            nullable = false,
            length = 500
    )
    private String codeHash;

    @Column(
            name = "STATUS",
            nullable = false,
            length = 20
    )
    private String status;

    @Column(
            name = "CREATED_AT",
            nullable = false
    )
    private LocalDateTime createdAt;

    @Column(name = "EXPIRES_AT")
    private LocalDateTime expiresAt;

    @Column(name = "USED_AT")
    private LocalDateTime usedAt;

    @Column(
            name = "INSTALLATION_ID",
            length = 200
    )
    private String installationId;

    public DeviceActivationCode() {
    }

    public Long getActivationCodeId() {
        return activationCodeId;
    }

    public void setActivationCodeId(Long activationCodeId) {
        this.activationCodeId = activationCodeId;
    }

    public Long getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Long subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public Long getClinicId() {
        return clinicId;
    }

    public void setClinicId(Long clinicId) {
        this.clinicId = clinicId;
    }

    public String getCodeHash() {
        return codeHash;
    }

    public void setCodeHash(String codeHash) {
        this.codeHash = codeHash;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public LocalDateTime getUsedAt() {
        return usedAt;
    }

    public void setUsedAt(LocalDateTime usedAt) {
        this.usedAt = usedAt;
    }

    public String getInstallationId() {
        return installationId;
    }

    public void setInstallationId(String installationId) {
        this.installationId = installationId;
    }

}
