package com.admin.dto;

import java.time.LocalDateTime;

public class ClinicResponse {

    private Long clinicId;
    private String clinicCode;
    private String clinicName;
    private String ownerName;
    private String phone;
    private String email;
    private String address;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ClinicResponse() {
    }

    public ClinicResponse(
            Long clinicId,
            String clinicCode,
            String clinicName,
            String ownerName,
            String phone,
            String email,
            String address,
            String status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.clinicId = clinicId;
        this.clinicCode = clinicCode;
        this.clinicName = clinicName;
        this.ownerName = ownerName;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getClinicId() {
        return clinicId;
    }

    public void setClinicId(Long clinicId) {
        this.clinicId = clinicId;
    }

    public String getClinicCode() {
        return clinicCode;
    }

    public void setClinicCode(String clinicCode) {
        this.clinicCode = clinicCode;
    }

    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}