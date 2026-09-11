package com.admin.repository;

import com.admin.entity.FasUserRole;
import com.admin.entity.FasUserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FasUserRoleRepository
        extends JpaRepository<FasUserRole, FasUserRoleId> {

    // =========================================================
    // أدوار مستخدم محدد
    // =========================================================

    @Query("""
            SELECT ur
            FROM FasUserRole ur
            JOIN FETCH ur.role r
            WHERE ur.user.userId = :userId
            ORDER BY r.roleId
            """)
    List<FasUserRole> findByUserIdWithRole(
            @Param("userId") Long userId
    );

    // =========================================================
    // حذف جميع أدوار مستخدم
    // =========================================================

    @Transactional
    @Modifying
    @Query("""
            DELETE FROM FasUserRole ur
            WHERE ur.user.userId = :userId
            """)
    void deleteByUserId(
            @Param("userId") Long userId
    );
}