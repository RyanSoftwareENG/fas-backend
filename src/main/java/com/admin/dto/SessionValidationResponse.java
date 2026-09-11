package com.admin.dto;

public class SessionValidationResponse {

    private boolean valid;
    private String message;

    private Long userId;
    private Long clinicId;
    private Long deviceId;

    private String username;
    private String fullName;
    private String roleName;

    public SessionValidationResponse() {
    }

    public SessionValidationResponse(
            boolean valid,
            String message,
            Long userId,
            Long clinicId,
            Long deviceId,
            String username,
            String fullName,
            String roleName
    ) {
        this.valid = valid;
        this.message = message;
        this.userId = userId;
        this.clinicId = clinicId;
        this.deviceId = deviceId;
        this.username = username;
        this.fullName = fullName;
        this.roleName = roleName;
    }

    public boolean isValid() {
        return valid;
    }

    public String getMessage() {
        return message;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getClinicId() {
        return clinicId;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public String getRoleName() {
        return roleName;
    }
}
