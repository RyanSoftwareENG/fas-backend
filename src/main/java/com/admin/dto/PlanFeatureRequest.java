package com.admin.dto;

public class PlanFeatureRequest {

    private Long planId;
    private Long featureId;
    private Boolean enabled;
    private Integer featureLimit;

    public PlanFeatureRequest() {
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public Long getFeatureId() {
        return featureId;
    }

    public void setFeatureId(Long featureId) {
        this.featureId = featureId;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Integer getFeatureLimit() {
        return featureLimit;
    }

    public void setFeatureLimit(Integer featureLimit) {
        this.featureLimit = featureLimit;
    }
}