package com.admin.entity;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class FasUserRoleId implements Serializable {

    private Long userId;
    private Long roleId;

    public FasUserRoleId() {
    }

    public FasUserRoleId(
            Long userId,
            Long roleId
    ) {
        this.userId = userId;
        this.roleId = roleId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (!(o instanceof FasUserRoleId that)) {
            return false;
        }

        return Objects.equals(userId, that.userId)
                && Objects.equals(roleId, that.roleId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(
                userId,
                roleId
        );
    }
}