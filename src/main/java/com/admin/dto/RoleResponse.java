package com.admin.dto;

public class RoleResponse {

    private Long roleId;
    private String roleName;
    private String description;
    private String status;

    public RoleResponse() {
    }

    public RoleResponse(
            Long roleId,
            String roleName,
            String description,
            String status
    ) {
        this.roleId = roleId;
        this.roleName = roleName;
        this.description = description;
        this.status = status;
    }

    public Long getRoleId() {
        return roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }
}