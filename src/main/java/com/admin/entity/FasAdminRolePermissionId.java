package com.admin.entity;

import java.io.Serializable;
import java.util.Objects;

public class FasAdminRolePermissionId implements Serializable {

    private Long adminRoleId;
    private Long adminPermissionId;

    public FasAdminRolePermissionId() {
    }

    public FasAdminRolePermissionId(
            Long adminRoleId,
            Long adminPermissionId
    ) {
        this.adminRoleId = adminRoleId;
        this.adminPermissionId = adminPermissionId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof FasAdminRolePermissionId that))
            return false;

        return Objects.equals(
                adminRoleId,
                that.adminRoleId
        )
                &&
                Objects.equals(
                        adminPermissionId,
                        that.adminPermissionId
                );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                adminRoleId,
                adminPermissionId
        );
    }
}