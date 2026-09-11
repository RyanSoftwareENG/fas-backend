package com.admin.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "FAS_USER_ROLE",
        schema = "FAS_MANAGEMENT"
)
public class FasUserRole {

    @EmbeddedId
    private FasUserRoleId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(
            name = "USER_ID",
            nullable = false
    )
    private FasUser user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("roleId")
    @JoinColumn(
            name = "ROLE_ID",
            nullable = false
    )
    private FasRole role;

    public FasUserRole() {
    }

    public FasUserRole(
            FasUser user,
            FasRole role
    ) {

        this.user = user;
        this.role = role;

        this.id =
                new FasUserRoleId(
                        user.getUserId(),
                        role.getRoleId()
                );
    }

    public FasUserRoleId getId() {
        return id;
    }

    public void setId(FasUserRoleId id) {
        this.id = id;
    }

    public FasUser getUser() {
        return user;
    }

    public void setUser(FasUser user) {
        this.user = user;
    }

    public FasRole getRole() {
        return role;
    }

    public void setRole(FasRole role) {
        this.role = role;
    }
}