package com.admin.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "CLINIC",
        schema = "FAS_MANAGEMENT"
)
public class Clinic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CLINIC_ID")
    private Long clinicId;

    @Column(
            name = "CLINIC_CODE",
            nullable = false,
            unique = true,
            length = 50
    )
    private String clinicCode;

    @Column(
            name = "CLINIC_NAME",
            nullable = false,
            length = 200
    )
    private String clinicName;

    @Column(
            name = "OWNER_NAME",
            length = 200
    )
    private String ownerName;

    @Column(
            name = "PHONE",
            length = 50
    )
    private String phone;

    @Column(
            name = "EMAIL",
            length = 200
    )
    private String email;

    @Column(
            name = "ADDRESS",
            length = 500
    )
    private String address;

    @Column(
            name = "STATUS",
            nullable = false,
            length = 20
    )
    private String status;

    @Column(
            name = "CREATED_AT",
            nullable = false
    )
    private LocalDateTime createdAt;

    @Column(
            name = "UPDATED_AT",
            nullable = false
    )
    private LocalDateTime updatedAt;

    @OneToOne(
            mappedBy = "clinic",
            fetch = FetchType.LAZY
    )
    private ClinicDatabase clinicDatabase;

    public Clinic() {
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

    public ClinicDatabase getClinicDatabase() {
        return clinicDatabase;
    }

    public void setClinicDatabase(ClinicDatabase clinicDatabase) {
        this.clinicDatabase = clinicDatabase;
    }
}