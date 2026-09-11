package com.admin.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "SUBSCRIPTION_REQUEST",
        schema = "FAS_MANAGEMENT"
)
public class SubscriptionRequest {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    @Column(name = "REQUEST_ID")
    private Long requestId;

    @Column(
            name = "CLINIC_ID",
            nullable = false
    )
    private Long clinicId;

    @Column(
            name = "PLAN_ID",
            nullable = false
    )
    private Long planId;

    @Column(
            name = "REQUEST_TYPE",
            nullable = false,
            length = 20
    )
    private String requestType;

    @Column(
            name = "REQUESTED_AT",
            nullable = false
    )
    private LocalDateTime requestedAt;

    @Column(
            name = "STATUS",
            nullable = false,
            length = 20
    )
    private String status;

    @Column(name = "REVIEWED_BY")
    private Long reviewedBy;

    @Column(name = "REVIEWED_AT")
    private LocalDateTime reviewedAt;

    @Column(
            name = "ADMIN_NOTES",
            length = 1000
    )
    private String adminNotes;

    @Column(
            name = "OWNER_NOTES",
            length = 1000
    )
    private String ownerNotes;

    public SubscriptionRequest() {
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Long getClinicId() {
        return clinicId;
    }

    public void setClinicId(Long clinicId) {
        this.clinicId = clinicId;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(
            LocalDateTime requestedAt
    ) {
        this.requestedAt = requestedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(Long reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(
            LocalDateTime reviewedAt
    ) {
        this.reviewedAt = reviewedAt;
    }

    public String getAdminNotes() {
        return adminNotes;
    }

    public void setAdminNotes(
            String adminNotes
    ) {
        this.adminNotes = adminNotes;
    }

    public String getOwnerNotes() {
        return ownerNotes;
    }

    public void setOwnerNotes(
            String ownerNotes
    ) {
        this.ownerNotes = ownerNotes;
    }
}