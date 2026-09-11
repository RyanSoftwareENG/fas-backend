package com.admin.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "FAS_ROLE_PERMISSION",
        schema = "FAS_MANAGEMENT"
)
public class FasRolePermission {

    @EmbeddedId
    private FasRolePermissionId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("roleId")
    @JoinColumn(
            name = "ROLE_ID",
            nullable = false
    )
    private FasRole role;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("permissionId")
    @JoinColumn(
            name = "PERMISSION_ID",
            nullable = false
    )
    private FasPermission permission;

    public FasRolePermission() {
    }

    public FasRolePermission(
            FasRole role,
            FasPermission permission
    ) {
        this.role = role;
        this.permission = permission;

        this.id = new FasRolePermissionId(
                role.getRoleId(),
                permission.getPermissionId()
        );
    }

    public FasRolePermissionId getId() {
        return id;
    }

    public void setId(FasRolePermissionId id) {
        this.id = id;
    }

    public FasRole getRole() {
        return role;
    }

    public void setRole(FasRole role) {
        this.role = role;
    }

    public FasPermission getPermission() {
        return permission;
    }

    public void setPermission(FasPermission permission) {
        this.permission = permission;
    }
}