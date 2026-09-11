package com.fas.dto;

import java.time.LocalDateTime;

public class SubscriptionRequestResponse {

    private Long requestId;
    private Long planId;
    private String requestType;
    private LocalDateTime requestedAt;
    private String status;
    private LocalDateTime reviewedAt;
    private String adminNotes;
    private String ownerNotes;

    public SubscriptionRequestResponse() {
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
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

    public void setRequestType(
            String requestType
    ) {
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

    public void setStatus(
            String status
    ) {
        this.status = status;
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