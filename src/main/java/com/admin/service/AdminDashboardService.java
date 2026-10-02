package com.admin.service;

import com.admin.dto.AdminDashboardResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdminDashboardService {

    @PersistenceContext(unitName = "managementPersistenceUnit")
    private EntityManager entityManager;

    @Transactional(readOnly = true)
    public AdminDashboardResponse getDashboard() {

        AdminDashboardResponse response =
                new AdminDashboardResponse();

        // =====================================================
        // Clinics
        // =====================================================

        response.setTotalClinics(
                count("""
                        SELECT COUNT(*)
                        FROM FAS_MANAGEMENT.CLINIC
                        """)
        );

        response.setActiveClinics(
                count("""
                        SELECT COUNT(*)
                        FROM FAS_MANAGEMENT.CLINIC
                        WHERE STATUS = 'ACTIVE'
                        """)
        );

        response.setSuspendedClinics(
                count("""
                        SELECT COUNT(*)
                        FROM FAS_MANAGEMENT.CLINIC
                        WHERE STATUS = 'SUSPENDED'
                        """)
        );

        response.setClosedClinics(
                count("""
                        SELECT COUNT(*)
                        FROM FAS_MANAGEMENT.CLINIC
                        WHERE STATUS = 'CLOSED'
                        """)
        );

        // =====================================================
        // Users
        // =====================================================

        response.setTotalUsers(
                count("""
                        SELECT COUNT(*)
                        FROM FAS_MANAGEMENT.FAS_USER
                        """)
        );

        response.setActiveUsers(
                count("""
                        SELECT COUNT(*)
                        FROM FAS_MANAGEMENT.FAS_USER
                        WHERE STATUS = 'ACTIVE'
                        """)
        );

        response.setSuspendedUsers(
                count("""
                        SELECT COUNT(*)
                        FROM FAS_MANAGEMENT.FAS_USER
                        WHERE STATUS = 'SUSPENDED'
                        """)
        );

        response.setDisabledUsers(
                count("""
                        SELECT COUNT(*)
                        FROM FAS_MANAGEMENT.FAS_USER
                        WHERE STATUS = 'DISABLED'
                        """)
        );

        // =====================================================
        // Plans
        // =====================================================

        response.setTotalPlans(
                count("""
                        SELECT COUNT(*)
                        FROM FAS_MANAGEMENT.SUBSCRIPTION_PLAN
                        """)
        );

        response.setActivePlans(
                count("""
                        SELECT COUNT(*)
                        FROM FAS_MANAGEMENT.SUBSCRIPTION_PLAN
                        WHERE STATUS = 'ACTIVE'
                        """)
        );

        response.setDisabledPlans(
                count("""
                        SELECT COUNT(*)
                        FROM FAS_MANAGEMENT.SUBSCRIPTION_PLAN
                        WHERE STATUS = 'DISABLED'
                        """)
        );

        // =====================================================
        // Subscriptions
        // =====================================================

        response.setTotalSubscriptions(
                count("""
                        SELECT COUNT(*)
                        FROM FAS_MANAGEMENT.SUBSCRIPTION
                        """)
        );

        response.setPendingSubscriptions(
                countStatus(
                        "SUBSCRIPTION",
                        "PENDING"
                )
        );

        response.setActiveSubscriptions(
                countStatus(
                        "SUBSCRIPTION",
                        "ACTIVE"
                )
        );

        response.setExpiredSubscriptions(
                countStatus(
                        "SUBSCRIPTION",
                        "EXPIRED"
                )
        );

        response.setSuspendedSubscriptions(
                countStatus(
                        "SUBSCRIPTION",
                        "SUSPENDED"
                )
        );

        response.setCancelledSubscriptions(
                countStatus(
                        "SUBSCRIPTION",
                        "CANCELLED"
                )
        );

        // =====================================================
        // Devices
        // =====================================================

        response.setTotalDevices(
                count("""
                        SELECT COUNT(*)
                        FROM FAS_MANAGEMENT.DEVICE
                        """)
        );

        response.setActiveDevices(
                count("""
                        SELECT COUNT(*)
                        FROM FAS_MANAGEMENT.DEVICE
                        WHERE STATUS = 'ACTIVE'
                        """)
        );

        response.setBlockedDevices(
                count("""
                        SELECT COUNT(*)
                        FROM FAS_MANAGEMENT.DEVICE
                        WHERE STATUS = 'BLOCKED'
                        """)
        );

        response.setRevokedDevices(
                count("""
                        SELECT COUNT(*)
                        FROM FAS_MANAGEMENT.DEVICE
                        WHERE STATUS = 'REVOKED'
                        """)
        );

        // =====================================================
        // User Sessions
        // =====================================================

        response.setActiveUserSessions(
                count("""
                        SELECT COUNT(*)
                        FROM FAS_MANAGEMENT.USER_SESSION
                        WHERE REVOKED = 0
                          AND EXPIRES_AT > SYSTIMESTAMP
                        """)
        );

        // =====================================================
        // Admin Sessions
        // =====================================================

        response.setActiveAdminSessions(
                count("""
                        SELECT COUNT(*)
                        FROM FAS_MANAGEMENT.ADMIN_SESSION
                        WHERE REVOKED = 0
                          AND EXPIRES_AT > SYSTIMESTAMP
                        """)
        );

        // =====================================================
        // Admin Users
        // =====================================================

        response.setTotalAdminUsers(
                count("""
                        SELECT COUNT(*)
                        FROM FAS_MANAGEMENT.FAS_ADMIN_USER
                        """)
        );

        response.setActiveAdminUsers(
                count("""
                        SELECT COUNT(*)
                        FROM FAS_MANAGEMENT.FAS_ADMIN_USER
                        WHERE STATUS = 'ACTIVE'
                        """)
        );

        // =====================================================
        // Roles / Permissions
        // =====================================================

        response.setTotalRoles(
                count("""
                        SELECT COUNT(*)
                        FROM FAS_MANAGEMENT.FAS_ROLE
                        """)
        );

        response.setTotalPermissions(
                count("""
                        SELECT COUNT(*)
                        FROM FAS_MANAGEMENT.FAS_PERMISSION
                        """)
        );

        // =====================================================
        // Recent Activities
        // =====================================================

        response.setRecentActivities(
                loadRecentActivities()
        );

        // =====================================================
        // Recent Activations
        // =====================================================

        response.setRecentActivations(
                loadRecentActivations()
        );

        return response;
    }

    private long count(
            String sql
    ) {

        Number result =
                (Number) entityManager
                        .createNativeQuery(sql)
                        .getSingleResult();

        return result.longValue();
    }

    private long countStatus(
            String table,
            String status
    ) {

        String sql =
                """
                SELECT COUNT(*)
                FROM FAS_MANAGEMENT.%s
                WHERE STATUS = :status
                """.formatted(table);

        Number result =
                (Number) entityManager
                        .createNativeQuery(sql)
                        .setParameter(
                                "status",
                                status
                        )
                        .getSingleResult();

        return result.longValue();
    }

    private List<AdminDashboardResponse.ActivityItem>
    loadRecentActivities() {

        List<Object[]> result =
                entityManager
                        .createNativeQuery("""
                                SELECT
                                    LOG_ID,
                                    ADMIN_USER_ID,
                                    ACTION,
                                    ENTITY_NAME,
                                    ENTITY_ID,
                                    DETAILS,
                                    CREATED_AT
                                FROM
                                    FAS_MANAGEMENT.ADMIN_ACTIVITY_LOG
                                ORDER BY CREATED_AT DESC
                                FETCH FIRST 10 ROWS ONLY
                                """)
                        .getResultList();

        List<AdminDashboardResponse.ActivityItem>
                items =
                new ArrayList<>();

        for (Object[] row : result) {

            items.add(
                    new AdminDashboardResponse.ActivityItem(
                            toLong(row[0]),
                            toLong(row[1]),
                            toString(row[2]),
                            toString(row[3]),
                            toLong(row[4]),
                            toString(row[5]),
                            toDateTime(row[6])
                    )
            );
        }

        return items;
    }

    private List<AdminDashboardResponse.ActivationItem>
    loadRecentActivations() {

        List<Object[]> result =
                entityManager
                        .createNativeQuery("""
                                SELECT
                                    ACTIVATION_ID,
                                    SUBSCRIPTION_ID,
                                    ADMIN_USER_ID,
                                    PREVIOUS_STATUS,
                                    NEW_STATUS,
                                    NOTES,
                                    ACTIVATION_DATE
                                FROM
                                    FAS_MANAGEMENT.SUBSCRIPTION_ACTIVATION
                                ORDER BY ACTIVATION_DATE DESC
                                FETCH FIRST 10 ROWS ONLY
                                """)
                        .getResultList();

        List<AdminDashboardResponse.ActivationItem>
                items =
                new ArrayList<>();

        for (Object[] row : result) {

            items.add(
                    new AdminDashboardResponse.ActivationItem(
                            toLong(row[0]),
                            toLong(row[1]),
                            toLong(row[2]),
                            toString(row[3]),
                            toString(row[4]),
                            toString(row[5]),
                            toDateTime(row[6])
                    )
            );
        }

        return items;
    }

    private Long toLong(Object value) {

        if (value == null) {
            return null;
        }

        return ((Number) value).longValue();
    }

    private String toString(Object value) {

        return value == null
                ? null
                : String.valueOf(value);
    }

    private LocalDateTime toDateTime(
            Object value
    ) {

        if (value == null) {
            return null;
        }

        if (value instanceof Timestamp timestamp) {
            return timestamp.toLocalDateTime();
        }

        return null;
    }
}