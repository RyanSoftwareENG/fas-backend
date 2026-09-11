package com.admin.repository;

import com.admin.entity.AdminActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AdminActivityLogRepository
        extends JpaRepository<AdminActivityLog, Long> {

    List<AdminActivityLog> findByAdminUserIdOrderByCreatedAtDesc(
            Long adminUserId
    );

    List<AdminActivityLog> findByOrderByCreatedAtDesc();

    @Query("""
            SELECT l
            FROM AdminActivityLog l
            WHERE (:adminUserId IS NULL
                   OR l.adminUserId = :adminUserId)
              AND (:fromDate IS NULL
                   OR l.createdAt >= :fromDate)
              AND (:toDate IS NULL
                   OR l.createdAt < :toDate)
              AND (
                    :keyword IS NULL
                    OR LOWER(l.action) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(l.entityName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(CAST(l.details AS String))
                       LIKE LOWER(CONCAT('%', :keyword, '%'))
                  )
            ORDER BY l.createdAt DESC
            """)
    List<AdminActivityLog> search(
            @Param("adminUserId") Long adminUserId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("keyword") String keyword
    );
}