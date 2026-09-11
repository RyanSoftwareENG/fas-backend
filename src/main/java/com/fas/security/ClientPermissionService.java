package com.fas.security;

import com.admin.entity.FasRolePermission;
import com.admin.entity.FasUserRole;
import com.admin.repository.FasRolePermissionRepository;
import com.admin.repository.FasUserRoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClientPermissionService {

    private final FasUserRoleRepository userRoleRepository;

    private final FasRolePermissionRepository rolePermissionRepository;

    public ClientPermissionService(
            FasUserRoleRepository userRoleRepository,
            FasRolePermissionRepository rolePermissionRepository
    ) {
        this.userRoleRepository =
                userRoleRepository;

        this.rolePermissionRepository =
                rolePermissionRepository;
    }

    // =====================================================
    // التحقق من صلاحية مستخدم
    // =====================================================

    @Transactional(readOnly = true)
    public boolean hasPermission(
            Long userId,
            String permissionCode
    ) {

        if (userId == null ||
                userId <= 0) {

            return false;
        }

        if (permissionCode == null ||
                permissionCode.isBlank()) {

            return false;
        }

        String requiredPermission =
                permissionCode
                        .trim()
                        .toUpperCase();

        // =================================================
        // أدوار المستخدم
        // =================================================

        List<FasUserRole> userRoles =
                userRoleRepository
                        .findByUserIdWithRole(
                                userId
                        );

        if (userRoles == null ||
                userRoles.isEmpty()) {

            return false;
        }

        // =================================================
        // فحص كل دور نشط
        // =================================================

        for (
                FasUserRole userRole :
                userRoles
        ) {

            if (userRole == null ||
                    userRole.getRole() == null) {

                continue;
            }

            // الدور يجب أن يكون ACTIVE
            if (!"ACTIVE".equalsIgnoreCase(
                    userRole
                            .getRole()
                            .getStatus()
            )) {

                continue;
            }

            Long roleId =
                    userRole
                            .getRole()
                            .getRoleId();

            if (roleId == null ||
                    roleId <= 0) {

                continue;
            }

            // =================================================
            // صلاحيات الدور
            // =================================================

            List<FasRolePermission> rolePermissions =
                    rolePermissionRepository
                            .findByRoleIdWithPermission(
                                    roleId
                            );

            if (rolePermissions == null ||
                    rolePermissions.isEmpty()) {

                continue;
            }

            // =================================================
            // البحث عن الصلاحية
            // =================================================

            for (
                    FasRolePermission rolePermission :
                    rolePermissions
            ) {

                if (rolePermission == null ||
                        rolePermission.getPermission() == null) {

                    continue;
                }

                if (!"ACTIVE".equalsIgnoreCase(
                        rolePermission
                                .getPermission()
                                .getStatus()
                )) {

                    continue;
                }

                String code =
                        rolePermission
                                .getPermission()
                                .getPermissionCode();

                if (code != null &&
                        requiredPermission.equals(
                                code.trim().toUpperCase()
                        )) {

                    return true;
                }
            }
        }

        return false;
    }
}