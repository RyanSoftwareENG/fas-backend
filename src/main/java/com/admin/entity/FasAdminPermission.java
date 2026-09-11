package com.admin.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "FAS_ADMIN_PERMISSION",
        schema = "FAS_MANAGEMENT"
)
public class FasAdminPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ADMIN_PERMISSION_ID")
    private Long adminPermissionId;

    @Column(
            name = "PERMISSION_CODE",
            nullable = false,
            length = 150
    )
    private String permissionCode;

    @Column(
            name = "PERMISSION_NAME",
            nullable = false,
            length = 200
    )
    private String permissionName;

    @Column(
            name = "DESCRIPTION",
            length = 500
    )
    private String description;

    @Column(
            name = "STATUS",
            nullable = false,
            length = 20
    )
    private String status;

    public FasAdminPermission() {
    }

    public Long getAdminPermissionId() {
        return adminPermissionId;
    }

    public void setAdminPermissionId(Long adminPermissionId) {
        this.adminPermissionId = adminPermissionId;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}