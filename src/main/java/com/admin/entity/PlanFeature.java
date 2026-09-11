package com.admin.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "PLAN_FEATURE",
        schema = "FAS_MANAGEMENT"
)
@IdClass(PlanFeatureId.class)
public class PlanFeature {

    @Id
    @Column(name = "PLAN_ID", nullable = false)
    private Long planId;

    @Id
    @Column(name = "FEATURE_ID", nullable = false)
    private Long featureId;

    @Column(
            name = "ENABLED",
            nullable = false
    )
    private Boolean enabled;

    @Column(name = "FEATURE_LIMIT")
    private Integer featureLimit;

    public PlanFeature() {
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