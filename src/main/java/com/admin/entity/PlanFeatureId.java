package com.admin.entity;

import java.io.Serializable;
import java.util.Objects;

public class PlanFeatureId implements Serializable {

    private Long planId;
    private Long featureId;

    public PlanFeatureId() {
    }

    public PlanFeatureId(
            Long planId,
            Long featureId
    ) {
        this.planId = planId;
        this.featureId = featureId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof PlanFeatureId that)) {
            return false;
        }

        return Objects.equals(planId, that.planId)
                && Objects.equals(featureId, that.featureId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(planId, featureId);
    }
}