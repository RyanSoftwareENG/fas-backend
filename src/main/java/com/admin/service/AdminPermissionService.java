package com.admin.service;

import com.admin.entity.FasAdminRolePermission;
import com.admin.entity.FasAdminUserRole;
import com.admin.repository.FasAdminPermissionRepository;
import com.admin.repository.FasAdminRolePermissionRepository;
import com.admin.repository.FasAdminUserRoleRepository;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AdminPermissionService {

    // =====================================================
    // Permission Codes
    // =====================================================

    public static final String DEVICE_MANAGE =
            "DEVICE_MANAGE";

    public static final String SUBSCRIPTION_MANAGE =
            "SUBSCRIPTION_MANAGE";

    public static final String CLINIC_MANAGE =
            "CLINIC_MANAGE";

    public static final String USER_MANAGE =
            "USER_MANAGE";

    // =====================================================
    // Repositories
    // =====================================================

    private final FasAdminUserRoleRepository userRoleRepository;

    private final FasAdminRolePermissionRepository
            rolePermissionRepository;

    private final FasAdminPermissionRepository
            permissionRepository;

    // =====================================================
    // Constructor
    // =====================================================

    public AdminPermissionService(
            FasAdminUserRoleRepository userRoleRepository,
            FasAdminRolePermissionRepository rolePermissionRepository,
            FasAdminPermissionRepository permissionRepository
    ) {

        this.userRoleRepository =
                userRoleRepository;

        this.rolePermissionRepository =
                rolePermissionRepository;

        this.permissionRepository =
                permissionRepository;
    }

    // =====================================================
    // Get Permissions
    // =====================================================

    /**
     * جلب جميع أكواد الصلاحيات الفعالة
     * الخاصة بمدير معين.
     */
    public Set<String> getPermissionCodes(
            Long adminUserId
    ) {

        Set<String> permissionCodes =
                new HashSet<>();

        if (adminUserId == null) {
            return permissionCodes;
        }

        // -------------------------------------------------
        // 1. أدوار المدير
        // -------------------------------------------------

        List<FasAdminUserRole> userRoles =
                userRoleRepository
                        .findByAdminUserId(
                                adminUserId
                        );

        // -------------------------------------------------
        // 2. المرور على الأدوار
        // -------------------------------------------------

        for (
                FasAdminUserRole userRole :
                userRoles
        ) {

            Long roleId =
                    userRole.getAdminRoleId();

            if (roleId == null) {
                continue;
            }

            // -------------------------------------------------
            // 3. صلاحيات الدور
            // -------------------------------------------------

            List<FasAdminRolePermission> rolePermissions =
                    rolePermissionRepository
                            .findByAdminRoleId(
                                    roleId
                            );

            // -------------------------------------------------
            // 4. بيانات الصلاحيات
            // -------------------------------------------------

            for (
                    FasAdminRolePermission rolePermission :
                    rolePermissions
            ) {

                Long permissionId =
                        rolePermission
                                .getAdminPermissionId();

                if (permissionId == null) {
                    continue;
                }

                permissionRepository
                        .findById(
                                permissionId
                        )
                        .ifPresent(
                                permission -> {

                                    if (
                                            "ACTIVE".equalsIgnoreCase(
                                                    permission.getStatus()
                                            )
                                    ) {

                                        String code =
                                                permission
                                                        .getPermissionCode();

                                        if (
                                                code != null &&
                                                        !code.isBlank()
                                        ) {

                                            permissionCodes.add(
                                                    code.trim()
                                                            .toUpperCase()
                                            );
                                        }
                                    }
                                }
                        );
            }
        }

        return permissionCodes;
    }

    // =====================================================
    // Check Permission
    // =====================================================

    /**
     * التحقق من امتلاك المدير لصلاحية معينة.
     */
    public boolean hasPermission(
            Long adminUserId,
            String permissionCode
    ) {

        if (
                adminUserId == null ||
                        permissionCode == null ||
                        permissionCode.isBlank()
        ) {

            return false;
        }

        String normalizedCode =
                permissionCode
                        .trim()
                        .toUpperCase();

        return getPermissionCodes(
                adminUserId
        ).contains(
                normalizedCode
        );
    }

    // =====================================================
    // Device Permission
    // =====================================================

    /**
     * التحقق المباشر من صلاحية إدارة الأجهزة.
     *
     * لا يمنح الصلاحية من نفسه،
     * بل يتحقق من وجودها ضمن صلاحيات المدير.
     */
    public boolean canManageDevices(
            Long adminUserId
    ) {

        return hasPermission(
                adminUserId,
                DEVICE_MANAGE
        );
    }
}