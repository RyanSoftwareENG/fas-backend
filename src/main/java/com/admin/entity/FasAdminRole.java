package com.admin.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "FAS_ADMIN_ROLE",
        schema = "FAS_MANAGEMENT"
)
public class FasAdminRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ADMIN_ROLE_ID")
    private Long adminRoleId;

    @Column(
            name = "ROLE_NAME",
            nullable = false,
            length = 100
    )
    private String roleName;

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

    public FasAdminRole() {
    }

    public Long getAdminRoleId() {
        return adminRoleId;
    }

    public void setAdminRoleId(Long adminRoleId) {
        this.adminRoleId = adminRoleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
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