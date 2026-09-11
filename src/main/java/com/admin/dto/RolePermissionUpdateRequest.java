package com.admin.dto;

import java.util.Set;

public class RolePermissionUpdateRequest {

    private Set<Long> permissionIds;

    public RolePermissionUpdateRequest() {
    }

    public Set<Long> getPermissionIds() {
        return permissionIds;
    }

    public void setPermissionIds(Set<Long> permissionIds) {
        this.permissionIds = permissionIds;
    }
}