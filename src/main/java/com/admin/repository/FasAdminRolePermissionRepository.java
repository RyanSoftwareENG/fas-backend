package com.admin.repository;

import com.admin.entity.FasAdminRolePermission;
import com.admin.entity.FasAdminRolePermissionId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FasAdminRolePermissionRepository
        extends JpaRepository<
        FasAdminRolePermission,
        FasAdminRolePermissionId> {

    List<FasAdminRolePermission> findByAdminRoleId(
            Long adminRoleId
    );

    List<FasAdminRolePermission> findByAdminPermissionId(
            Long adminPermissionId
    );
}