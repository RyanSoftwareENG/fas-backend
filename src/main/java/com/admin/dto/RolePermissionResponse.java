package com.admin.dto;

public class RolePermissionResponse {

    private Long roleId;
    private String roleName;
    private Long permissionId;
    private String permissionCode;
    private String permissionName;
    private boolean assigned;

    public RolePermissionResponse() {
    }

    public RolePermissionResponse(
            Long roleId,
            String roleName,
            Long permissionId,
            String permissionCode,
            String permissionName,
            boolean assigned
    ) {
        this.roleId = roleId;
        this.roleName = roleName;
        this.permissionId = permissionId;
        this.permissionCode = permissionCode;
        this.permissionName = permissionName;
        this.assigned = assigned;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Long getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Long permissionId) {
        this.permissionId = permissionId;
    }

    public String getPermissionCode() {
        return permissionCode;
    }

    public void setPermissionCode(String permissionCode) {
        this.permissionCode = permissionCode;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    public boolean isAssigned() {
        return assigned;
    }

    public void setAssigned(boolean assigned) {
        this.assigned = assigned;
    }
}