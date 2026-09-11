package com.admin.repository;

import com.admin.entity.FasRolePermission;
import com.admin.entity.FasRolePermissionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FasRolePermissionRepository
        extends JpaRepository<FasRolePermission, FasRolePermissionId> {

    // =====================================================
    // جلب صلاحيات دور محدد
    // =====================================================

    @Query("""
            SELECT rp
            FROM FasRolePermission rp
            JOIN FETCH rp.permission p
            WHERE rp.role.roleId = :roleId
              AND p.status = 'ACTIVE'
            ORDER BY p.permissionId
            """)
    List<FasRolePermission> findByRoleIdWithPermission(
            @Param("roleId") Long roleId
    );

    // =====================================================
    // حذف جميع صلاحيات الدور
    // =====================================================

    @Transactional
    @Modifying
    @Query("""
            DELETE FROM FasRolePermission rp
            WHERE rp.role.roleId = :roleId
            """)
    int deleteByRoleId(
            @Param("roleId") Long roleId
    );
}
