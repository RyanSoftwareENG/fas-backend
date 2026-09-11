package com.admin.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "FEATURE",
        schema = "FAS_MANAGEMENT"
)
public class Feature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FEATURE_ID", nullable = false)
    private Long featureId;

    @Column(
            name = "FEATURE_CODE",
            nullable = false,
            unique = true,
            length = 150
    )
    private String featureCode;

    @Column(
            name = "FEATURE_NAME",
            nullable = false,
            length = 200
    )
    private String featureName;

    @Column(
            name = "DESCRIPTION",
            length = 500
    )
    private String description;

    @Column(
            name = "STATUS",
            nullable = false,
            length = 20
    )
    private String status;

    public Feature() {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}