package com.admin.dto;

public class AdminLoginResponse {

    private boolean success;
    private String message;
    private Long adminUserId;
    private String username;
    private String fullName;
    private String token;

    public AdminLoginResponse() {
    }

    public AdminLoginResponse(
            boolean success,
            String message,
            Long adminUserId,
            String username,
            String fullName,
            String token
    ) {
        this.success = success;
        this.message = message;
        this.adminUserId = adminUserId;
        this.username = username;
        this.fullName = fullName;
        this.token = token;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Long getAdminUserId() {
        return adminUserId;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public String getToken() {
        return token;
    }
}