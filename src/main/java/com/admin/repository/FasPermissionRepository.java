package com.admin.repository;

import com.admin.entity.FasPermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FasPermissionRepository
        extends JpaRepository<FasPermission, Long> {

    List<FasPermission> findByStatusOrderByPermissionIdAsc(
            String status
    );

    Optional<FasPermission> findByPermissionCode(
            String permissionCode
    );
    boolean existsByPermissionCode(
            String permissionCode
    );
    List<FasPermission> findAllByOrderByPermissionNameAsc();
}