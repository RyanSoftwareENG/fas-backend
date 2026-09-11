package com.admin.entity;

import java.io.Serializable;
import java.util.Objects;

public class FasAdminUserRoleId implements Serializable {

    private Long adminUserId;
    private Long adminRoleId;

    public FasAdminUserRoleId() {
    }

    public FasAdminUserRoleId(
            Long adminUserId,
            Long adminRoleId
    ) {
        this.adminUserId = adminUserId;
        this.adminRoleId = adminRoleId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof FasAdminUserRoleId that))
            return false;

        return Objects.equals(
                adminUserId,
                that.adminUserId
        )
                &&
                Objects.equals(
                        adminRoleId,
                        that.adminRoleId
                );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                adminUserId,
                adminRoleId
        );
    }
}