package com.admin.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class SubscriptionResponse {

    // =====================================================
    // Subscription
    // =====================================================

    private Long subscriptionId;

    private LocalDate startDate;
    private LocalDate endDate;

    private String status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // =====================================================
    // Clinic
    // =====================================================

    private Long clinicId;
    private String clinicName;

    // =====================================================
    // Plan
    // =====================================================

    private Long planId;
    private String planName;

    private BigDecimal price;
    private String currencyCode;
    private Integer durationDays;

    // =====================================================
    // Constructor
    // =====================================================

    public SubscriptionResponse() {
    }

    // =====================================================
    // Subscription ID
    // =====================================================

    public Long getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(
            Long subscriptionId
    ) {
        this.subscriptionId =
                subscriptionId;
    }

    // =====================================================
    // Clinic ID
    // =====================================================

    public Long getClinicId() {
        return clinicId;
    }

    public void setClinicId(
            Long clinicId
    ) {
        this.clinicId =
                clinicId;
    }

    // =====================================================
    // Clinic Name
    // =====================================================

    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(
            String clinicName
    ) {
        this.clinicName =
                clinicName;
    }

    // =====================================================
    // Plan ID
    // =====================================================

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(
            Long planId
    ) {
        this.planId =
                planId;
    }

    // =====================================================
    // Plan Name
    // =====================================================

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(
            String planName
    ) {
        this.planName =
                planName;
    }

    // =====================================================
    // Price
    // =====================================================

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(
            BigDecimal price
    ) {
        this.price =
                price;
    }

    // =====================================================
    // Currency
    // =====================================================

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(
            String currencyCode
    ) {
        this.currencyCode =
                currencyCode;
    }

    // =====================================================
    // Duration
    // =====================================================

    public Integer getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(
            Integer durationDays
    ) {
        this.durationDays =
                durationDays;
    }

    // =====================================================
    // Start Date
    // =====================================================

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(
            LocalDate startDate
    ) {
        this.startDate =
                startDate;
    }

    // =====================================================
    // End Date
    // =====================================================

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(
            LocalDate endDate
    ) {
        this.endDate =
                endDate;
    }

    // =====================================================
    // Status
    // =====================================================

    public String getStatus() {
        return status;
    }

    public void setStatus(
            String status
    ) {
        this.status =
                status;
    }

    // =====================================================
    // Created At
    // =====================================================

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            LocalDateTime createdAt
    ) {
        this.createdAt =
                createdAt;
    }

    // =====================================================
    // Updated At
    // =====================================================

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(
            LocalDateTime updatedAt
    ) {
        this.updatedAt =
                updatedAt;
    }
}