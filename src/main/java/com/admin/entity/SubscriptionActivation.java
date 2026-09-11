package com.admin.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "SUBSCRIPTION_ACTIVATION",
        schema = "FAS_MANAGEMENT"
)
public class SubscriptionActivation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ACTIVATION_ID", nullable = false)
    private Long activationId;

    @Column(
            name = "SUBSCRIPTION_ID",
            nullable = false
    )
    private Long subscriptionId;

    @Column(
            name = "ADMIN_USER_ID",
            nullable = false
    )
    private Long adminUserId;

    @Column(
            name = "ACTIVATION_DATE",
            nullable = false
    )
    private LocalDateTime activationDate;

    @Column(
            name = "PREVIOUS_STATUS",
            length = 20
    )
    private String previousStatus;

    @Column(
            name = "NEW_STATUS",
            nullable = false,
            length = 20
    )
    private String newStatus;

    @Column(
            name = "NOTES",
            length = 1000
    )
    private String notes;

    public SubscriptionActivation() {
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