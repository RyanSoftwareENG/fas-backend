package com.admin.dto;

import java.time.LocalDateTime;

public class SubscriptionActivationResponse {

    private Long activationId;
    private Long subscriptionId;
    private Long adminUserId;

    private LocalDateTime activationDate;

    private String previousStatus;
    private String newStatus;
    private String notes;

    public SubscriptionActivationResponse() {
    }

    public Long getActivationId() {
        return activationId;
    }

    public void setActivationId(Long activationId) {
        this.activationId = activationId;
    }

    public Long getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Long subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public Long getAdminUserId() {
        return adminUserId;
    }

    public void setAdminUserId(Long adminUserId) {
        this.adminUserId = adminUserId;
    }

    public LocalDateTime getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(LocalDateTime activationDate) {
        this.activationDate = activationDate;
    }

    public String getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(String previousStatus) {
        this.previousStatus = previousStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}