package com.admin.service;

import com.admin.entity.FasAdminRolePermission;
import com.admin.entity.FasAdminUserRole;
import com.admin.entity.FasAdminPermission;
import com.admin.repository.FasAdminPermissionRepository;
import com.admin.repository.FasAdminRolePermissionRepository;
import com.admin.repository.FasAdminUserRoleRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AdminPermissionService {

    private final FasAdminUserRoleRepository userRoleRepository;
    private final FasAdminRolePermissionRepository rolePermissionRepository;
    private final FasAdminPermissionRepository permissionRepository;

    public AdminPermissionService(
            FasAdminUserRoleRepository userRoleRepository,
            FasAdminRolePermissionRepository rolePermissionRepository,
            FasAdminPermissionRepository permissionRepository
    ) {
        this.userRoleRepository = userRoleRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.permissionRepository = permissionRepository;
    }

    /**
     * جلب جميع أكواد الصلاحيات الخاصة بمدير معين
     */
    public Set<String> getPermissionCodes(Long adminUserId) {

        Set<String> permissionCodes = new HashSet<>();

        if (adminUserId == null) {
            return permissionCodes;
        }

        // 1. جلب أدوار المدير
        List<FasAdminUserRole> userRoles =
                userRoleRepository.findByAdminUserId(adminUserId);

        // 2. المرور على كل دور
        for (FasAdminUserRole userRole : userRoles) {

            Long roleId = userRole.getAdminRoleId();

            // 3. جلب صلاحيات الدور
            List<FasAdminRolePermission> rolePermissions =
                    rolePermissionRepository.findByAdminRoleId(roleId);

            // 4. جلب بيانات كل صلاحية
            for (FasAdminRolePermission rolePermission
                    : rolePermissions) {

                Long permissionId =
                        rolePermission.getAdminPermissionId();

                permissionRepository
                        .findById(permissionId)
                        .ifPresent(permission -> {

                            if ("ACTIVE".equalsIgnoreCase(
                                    permission.getStatus())) {

                                permissionCodes.add(
                                        permission.getPermissionCode()
                                );
                            }
                        });
            }
        }

        return permissionCodes;
    }

    /**
     * التحقق من امتلاك المدير لصلاحية معينة
     */
    public boolean hasPermission(
            Long adminUserId,
            String permissionCode
    ) {

        if (adminUserId == null
                || permissionCode == null
                || permissionCode.isBlank()) {

            return false;
        }

        return getPermissionCodes(adminUserId)
                .contains(permissionCode);
    }
}