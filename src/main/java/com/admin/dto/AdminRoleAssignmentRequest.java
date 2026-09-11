package com.admin.dto;

public class AdminRoleAssignmentRequest {

    private Long adminUserId;
    private Long adminRoleId;

    public AdminRoleAssignmentRequest() {
    }

    public Long getAdminUserId() {
        return adminUserId;
    }

    public void setAdminUserId(Long adminUserId) {
        this.adminUserId = adminUserId;
    }

    public Long getAdminRoleId() {
        return adminRoleId;
    }

    public void setAdminRoleId(Long adminRoleId) {
        this.adminRoleId = adminRoleId;
    }
}