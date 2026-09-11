package com.admin.dto;

import java.time.LocalDateTime;

public class DeviceActivationCodeResponse {

    private Long activationCodeId;
    private Long subscriptionId;
    private Long clinicId;

    private String activationCode;

    private String status;

    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    public DeviceActivationCodeResponse() {
    }

    public DeviceActivationCodeResponse(
            Long activationCodeId,
            Long subscriptionId,
            Long clinicId,
            String activationCode,
            String status,
            LocalDateTime createdAt,
            LocalDateTime expiresAt
    ) {
        this.activationCodeId = activationCodeId;
        this.subscriptionId = subscriptionId;
        this.clinicId = clinicId;
        this.activationCode = activationCode;
        this.status = status;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    public Long getActivationCodeId() {
        return activationCodeId;
    }

    public Long getSubscriptionId() {
        return subscriptionId;
    }

    public Long getClinicId() {
        return clinicId;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

}
