package com.admin.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "FAS_ADMIN_USER_ROLE",
        schema = "FAS_MANAGEMENT"
)
@IdClass(FasAdminUserRoleId.class)
public class FasAdminUserRole {

    @Id
    @Column(name = "ADMIN_USER_ID")
    private Long adminUserId;

    @Id
    @Column(name = "ADMIN_ROLE_ID")
    private Long adminRoleId;

    public FasAdminUserRole() {
    }

    public Long getAdminUserId() {
        return adminUserId;
    }

    public void setAdminUserId(Long adminUserId) {
        this.adminUserId = adminUserId;
    }

    public Long getAdminRoleId() {
        return adminRoleId;
    }

    public void setAdminRoleId(Long adminRoleId) {
        this.adminRoleId = adminRoleId;
    }
}