package com.admin.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "DEVICE",
        schema = "FAS_MANAGEMENT"
)
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DEVICE_ID")
    private Long deviceId;

    @Column(
            name = "CLINIC_ID",
            nullable = false
    )
    private Long clinicId;

    @Column(
            name = "DEVICE_NAME",
            length = 200
    )
    private String deviceName;

    @Column(
            name = "INSTALLATION_ID",
            nullable = false,
            length = 200,
            unique = true
    )
    private String installationId;

    @Column(
            name = "STATUS",
            nullable = false,
            length = 20
    )
    private String status;

    @Column(
            name = "FIRST_SEEN_AT",
            nullable = false
    )
    private LocalDateTime firstSeenAt;

    @Column(name = "LAST_SEEN_AT")
    private LocalDateTime lastSeenAt;

    public Device() {
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public Long getClinicId() {
        return clinicId;
    }

    public void setClinicId(Long clinicId) {
        this.clinicId = clinicId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getInstallationId() {
        return installationId;
    }

    public void setInstallationId(String installationId) {
        this.installationId = installationId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getFirstSeenAt() {
        return firstSeenAt;
    }

    public void setFirstSeenAt(LocalDateTime firstSeenAt) {
        this.firstSeenAt = firstSeenAt;
    }

    public LocalDateTime getLastSeenAt() {
        return lastSeenAt;
    }

    public void setLastSeenAt(LocalDateTime lastSeenAt) {
        this.lastSeenAt = lastSeenAt;
    }
}