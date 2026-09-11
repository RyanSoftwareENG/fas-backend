package com.admin.dto;

public class PermissionCreateRequest {

    private String permissionCode;
    private String permissionName;
    private String description;

    public PermissionCreateRequest() {
    }

    public String getPermissionCode() {
        return permissionCode;
    }

    public void setPermissionCode(
            String permissionCode
    ) {
        this.permissionCode =
                permissionCode;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(
            String permissionName
    ) {
        this.permissionName =
                permissionName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(
            String description
    ) {
        this.description =
                description;
    }
}