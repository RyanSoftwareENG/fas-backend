package com.admin.dto;

public class AdminPermissionAssignmentRequest {

    private Long adminRoleId;
    private Long adminPermissionId;

    public AdminPermissionAssignmentRequest() {
    }

    public Long getAdminRoleId() {
        return adminRoleId;
    }

    public void setAdminRoleId(Long adminRoleId) {
        this.adminRoleId = adminRoleId;
    }

    public Long getAdminPermissionId() {
        return adminPermissionId;
    }

    public void setAdminPermissionId(Long adminPermissionId) {
        this.adminPermissionId = adminPermissionId;
    }
}