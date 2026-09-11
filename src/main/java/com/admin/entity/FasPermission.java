package com.admin.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "FAS_PERMISSION",
        schema = "FAS_MANAGEMENT"
)
public class FasPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "PERMISSION_ID",
            nullable = false
    )
    private Long permissionId;

    @Column(
            name = "PERMISSION_CODE",
            nullable = false,
            unique = true,
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

    public FasPermission() {
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