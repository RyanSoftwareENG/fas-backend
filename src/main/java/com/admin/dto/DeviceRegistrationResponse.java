package com.admin.dto;

public class DeviceRegistrationResponse {

    private boolean success;
    private String message;

    private Long deviceId;
    private Long clinicId;
    private String deviceName;
    private String installationId;
    private String status;

    public DeviceRegistrationResponse() {
    }

    public DeviceRegistrationResponse(
            boolean success,
            String message,
            Long deviceId,
            Long clinicId,
            String deviceName,
            String installationId,
            String status
    ) {
        this.success = success;
        this.message = message;
        this.deviceId = deviceId;
        this.clinicId = clinicId;
        this.deviceName = deviceName;
        this.installationId = installationId;
        this.status = status;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public Long getClinicId() {
        return clinicId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getInstallationId() {
        return installationId;
    }

    public String getStatus() {
        return status;
    }
}
