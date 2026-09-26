package com.admin.dto;

public class UserLoginResponse {

    private boolean success;
    private String message;

    /*
     * Session Token الحقيقي.
     */
    private String token;

    /*
     * Token مؤقت لتسجيل جهاز جديد.
     */
    private String setupToken;
    private String clinicName;
    private Long userId;
    private Long clinicId;
    private Long deviceId;

    private String username;
    private String fullName;
    private String roleName;

    private boolean firstLogin;
    private boolean needsSetup;

    public UserLoginResponse() {
    }

    public UserLoginResponse(
            boolean success,
            String message,
            String token,
            Long userId,
            Long clinicId,
            Long deviceId,
            String username,
            String fullName,
            String roleName,
            boolean firstLogin,
            boolean needsSetup
    ) {
        this.success = success;
        this.message = message;
        this.token = token;
        this.userId = userId;
        this.clinicId = clinicId;
        this.deviceId = deviceId;
        this.username = username;
        this.fullName = fullName;
        this.roleName = roleName;
        this.firstLogin = firstLogin;
        this.needsSetup = needsSetup;
        this.setupToken = null;
    }

    // =====================================================
    // Success
    // =====================================================

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    // =====================================================
    // Message
    // =====================================================

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // =====================================================
    // Session Token
    // =====================================================

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    // =====================================================
    // Setup Token
    // =====================================================

    public String getSetupToken() {
        return setupToken;
    }

    public void setSetupToken(String setupToken) {
        this.setupToken = setupToken;
    }

    // =====================================================
    // User ID
    // =====================================================

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    // =====================================================
    // Clinic ID
    // =====================================================

    public Long getClinicId() {
        return clinicId;
    }

    public void setClinicId(Long clinicId) {
        this.clinicId = clinicId;
    }

    // =====================================================
    // Device ID
    // =====================================================

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    // =====================================================
    // Username
    // =====================================================

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // =====================================================
    // Full Name
    // =====================================================

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    // =====================================================
    // Role
    // =====================================================

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    // =====================================================
    // First Login
    // =====================================================

    public boolean isFirstLogin() {
        return firstLogin;
    }

    public void setFirstLogin(boolean firstLogin) {
        this.firstLogin = firstLogin;
    }

    // =====================================================
    // Needs Setup
    // =====================================================

    public boolean isNeedsSetup() {
        return needsSetup;
    }

    public void setNeedsSetup(boolean needsSetup) {
        this.needsSetup = needsSetup;
    }

    // =====================================================
// Clinic Name
// =====================================================

    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }
}