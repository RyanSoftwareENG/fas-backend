package com.fas.dto;

public class SubscriptionRequestCreateRequest {

    private Long planId;

    private String requestType;

    private String ownerNotes;

    public SubscriptionRequestCreateRequest() {
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

    public String getOwnerNotes() {
        return ownerNotes;
    }

    public void setOwnerNotes(
            String ownerNotes
    ) {
        this.ownerNotes = ownerNotes;
    }
}