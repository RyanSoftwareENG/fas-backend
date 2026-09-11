package com.admin.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "FAS_ADMIN_ROLE_PERMISSION",
        schema = "FAS_MANAGEMENT"
)
@IdClass(FasAdminRolePermissionId.class)
public class FasAdminRolePermission {

    @Id
    @Column(name = "ADMIN_ROLE_ID")
    private Long adminRoleId;

    @Id
    @Column(name = "ADMIN_PERMISSION_ID")
    private Long adminPermissionId;

    public FasAdminRolePermission() {
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