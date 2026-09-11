package com.admin.repository;

import com.admin.entity.FasAdminPermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FasAdminPermissionRepository
        extends JpaRepository<FasAdminPermission, Long> {

    Optional<FasAdminPermission> findByPermissionCode(
            String permissionCode
    );
}