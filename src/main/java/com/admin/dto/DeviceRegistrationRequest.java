package com.admin.dto;

public class DeviceRegistrationRequest {

    private String activationCode;
    private String installationId;
    private String deviceName;

    public DeviceRegistrationRequest() {
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(
            String activationCode
    ) {
        this.activationCode =
                activationCode;
    }

    public String getInstallationId() {
        return installationId;
    }

    public void setInstallationId(
            String installationId
    ) {
        this.installationId =
                installationId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(
            String deviceName
    ) {
        this.deviceName =
                deviceName;
    }
}
