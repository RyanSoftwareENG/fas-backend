package com.admin.dto;

public class PlanFeatureResponse {

    private Long planId;
    private Long featureId;

    private String featureCode;
    private String featureName;

    private Boolean enabled;
    private Integer featureLimit;

    public PlanFeatureResponse() {
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

    public String getFeatureCode() {
        return featureCode;
    }

    public void setFeatureCode(String featureCode) {
        this.featureCode = featureCode;
    }

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
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