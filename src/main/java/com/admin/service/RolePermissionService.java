package com.admin.service;

import com.admin.dto.RolePermissionResponse;
import com.admin.dto.RolePermissionUpdateRequest;
import com.admin.entity.FasPermission;
import com.admin.entity.FasRole;
import com.admin.entity.FasRolePermission;
import com.admin.entity.FasRolePermissionId;
import com.admin.repository.FasPermissionRepository;
import com.admin.repository.FasRolePermissionRepository;
import com.admin.repository.FasRoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RolePermissionService {

    private final FasRoleRepository roleRepository;

    private final FasPermissionRepository permissionRepository;

    private final FasRolePermissionRepository
            rolePermissionRepository;

    public RolePermissionService(
            FasRoleRepository roleRepository,
            FasPermissionRepository permissionRepository,
            FasRolePermissionRepository rolePermissionRepository
    ) {
        this.roleRepository =
                roleRepository;

        this.permissionRepository =
                permissionRepository;

        this.rolePermissionRepository =
                rolePermissionRepository;
    }

    // =====================================================
    // الأدوار النشطة
    // =====================================================

    @Transactional(readOnly = true)
    public List<FasRole> getActiveRoles() {

        return roleRepository
                .findByStatusOrderByRoleIdAsc(
                        "ACTIVE"
                );
    }

    // =====================================================
    // إنشاء دور
    // =====================================================

    @Transactional
    public FasRole createRole(
            String roleName,
            String description
    ) {

        String normalizedName =
                normalizeRoleName(
                        roleName
                );

        validateRoleName(
                normalizedName
        );

        if (roleRepository
                .findByRoleNameIgnoreCase(
                        normalizedName
                )
                .isPresent()) {

            throw new IllegalArgumentException(
                    "اسم الدور مستخدم بالفعل: "
                            + normalizedName
            );
        }

        FasRole role =
                new FasRole();

        role.setRoleName(
                normalizedName
        );

        role.setDescription(
                normalizeDescription(
                        description
                )
        );

        role.setStatus(
                "ACTIVE"
        );

        return roleRepository.save(
                role
        );
    }

    // =====================================================
    // تعديل دور
    // =====================================================

    @Transactional
    public FasRole updateRole(
            Long roleId,
            String roleName,
            String description
    ) {

        validateRoleId(
                roleId
        );

        FasRole role =
                getRoleById(
                        roleId
                );

        if (!"ACTIVE".equalsIgnoreCase(
                role.getStatus()
        )) {

            throw new IllegalArgumentException(
                    "لا يمكن تعديل دور غير فعال."
            );
        }

        String normalizedName =
                normalizeRoleName(
                        roleName
                );

        validateRoleName(
                normalizedName
        );

        roleRepository
                .findByRoleNameIgnoreCase(
                        normalizedName
                )
                .ifPresent(existing -> {

                    if (!existing.getRoleId()
                            .equals(roleId)) {

                        throw new IllegalArgumentException(
                                "اسم الدور مستخدم بالفعل: "
                                        + normalizedName
                        );
                    }
                });

        role.setRoleName(
                normalizedName
        );

        role.setDescription(
                normalizeDescription(
                        description
                )
        );

        return roleRepository.save(
                role
        );
    }

    // =====================================================
    // تعطيل دور
    // =====================================================

    @Transactional
    public void disableRole(
            Long roleId
    ) {

        validateRoleId(
                roleId
        );

        FasRole role =
                getRoleById(
                        roleId
                );

        role.setStatus(
                "DISABLED"
        );

        roleRepository.save(
                role
        );
    }

    // =====================================================
    // تفعيل دور
    // =====================================================

    @Transactional
    public void enableRole(
            Long roleId
    ) {

        validateRoleId(
                roleId
        );

        FasRole role =
                getRoleById(
                        roleId
                );

        role.setStatus(
                "ACTIVE"
        );

        roleRepository.save(
                role
        );
    }

    // =====================================================
    // جميع الصلاحيات النشطة
    // =====================================================

    @Transactional(readOnly = true)
    public List<FasPermission> getActivePermissions() {

        return permissionRepository
                .findByStatusOrderByPermissionIdAsc(
                        "ACTIVE"
                );
    }

    // =====================================================
    // صلاحيات الدور
    // =====================================================

    @Transactional(readOnly = true)
    public List<RolePermissionResponse>
    getRolePermissions(
            Long roleId
    ) {

        validateRoleId(
                roleId
        );

        FasRole role =
                getRoleById(
                        roleId
                );

        List<FasPermission> allPermissions =
                permissionRepository
                        .findByStatusOrderByPermissionIdAsc(
                                "ACTIVE"
                        );

        List<FasRolePermission> assigned =
                rolePermissionRepository
                        .findByRoleIdWithPermission(
                                roleId
                        );

        Set<Long> assignedIds =
                new HashSet<>();

        for (
                FasRolePermission rolePermission :
                assigned
        ) {

            if (rolePermission.getPermission() != null &&
                    rolePermission
                            .getPermission()
                            .getPermissionId() != null) {

                assignedIds.add(
                        rolePermission
                                .getPermission()
                                .getPermissionId()
                );
            }
        }

        List<RolePermissionResponse> result =
                new ArrayList<>();

        for (
                FasPermission permission :
                allPermissions
        ) {

            result.add(
                    new RolePermissionResponse(
                            role.getRoleId(),
                            role.getRoleName(),
                            permission.getPermissionId(),
                            permission.getPermissionCode(),
                            permission.getPermissionName(),
                            assignedIds.contains(
                                    permission.getPermissionId()
                            )
                    )
            );
        }

        return result;
    }

    // =====================================================
    // تحديث صلاحيات الدور
    // =====================================================

    @Transactional
    public void updateRolePermissions(
            Long roleId,
            RolePermissionUpdateRequest request
    ) {

        validateRoleId(
                roleId
        );

        if (request == null) {

            throw new IllegalArgumentException(
                    "بيانات الصلاحيات مطلوبة."
            );
        }

        FasRole role =
                getRoleById(
                        roleId
                );

        if (!"ACTIVE".equalsIgnoreCase(
                role.getStatus()
        )) {

            throw new IllegalArgumentException(
                    "لا يمكن تعديل صلاحيات دور غير فعال."
            );
        }

        Set<Long> requestedIds =
                request.getPermissionIds() == null
                        ? new HashSet<>()
                        : new HashSet<>(
                        request.getPermissionIds()
                );

        List<FasPermission> activePermissions =
                permissionRepository
                        .findByStatusOrderByPermissionIdAsc(
                                "ACTIVE"
                        );

        Set<Long> activePermissionIds =
                new HashSet<>();

        for (
                FasPermission permission :
                activePermissions
        ) {

            activePermissionIds.add(
                    permission.getPermissionId()
            );
        }

        if (!activePermissionIds.containsAll(
                requestedIds
        )) {

            throw new IllegalArgumentException(
                    "تم إرسال صلاحية غير موجودة أو غير فعالة."
            );
        }

        // -------------------------------------------------
        // حذف العلاقات القديمة
        // -------------------------------------------------

        rolePermissionRepository.deleteByRoleId(
                roleId
        );

        if (requestedIds.isEmpty()) {
            return;
        }

        // -------------------------------------------------
        // جلب الصلاحيات المطلوبة
        // -------------------------------------------------

        List<FasPermission> requestedPermissions =
                permissionRepository.findAllById(
                        requestedIds
                );

        if (requestedPermissions.size()
                != requestedIds.size()) {

            throw new IllegalArgumentException(
                    "تعذر العثور على بعض الصلاحيات المطلوبة."
            );
        }

        // -------------------------------------------------
        // إنشاء العلاقات الجديدة
        // -------------------------------------------------

        for (
                FasPermission permission :
                requestedPermissions
        ) {

            FasRolePermission rolePermission =
                    new FasRolePermission();

            rolePermission.setId(
                    new FasRolePermissionId(
                            roleId,
                            permission.getPermissionId()
                    )
            );

            rolePermission.setRole(
                    role
            );

            rolePermission.setPermission(
                    permission
            );

            rolePermissionRepository.save(
                    rolePermission
            );
        }
    }

    // =====================================================
    // جلب الدور
    // =====================================================

    @Transactional(readOnly = true)
    public FasRole getRoleById(
            Long roleId
    ) {

        return roleRepository
                .findById(
                        roleId
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "الدور غير موجود: "
                                        + roleId
                        )
                );
    }

    // =====================================================
    // التحقق من ID
    // =====================================================

    private void validateRoleId(
            Long roleId
    ) {

        if (roleId == null ||
                roleId <= 0) {

            throw new IllegalArgumentException(
                    "معرف الدور غير صالح."
            );
        }
    }

    // =====================================================
    // تنظيف اسم الدور
    // =====================================================

    private String normalizeRoleName(
            String roleName
    ) {

        if (roleName == null) {
            return null;
        }

        return roleName.trim();
    }

    // =====================================================
    // تنظيف الوصف
    // =====================================================

    private String normalizeDescription(
            String description
    ) {

        if (description == null ||
                description.isBlank()) {

            return null;
        }

        return description.trim();
    }

    // =====================================================
    // التحقق من اسم الدور
    // =====================================================

    private void validateRoleName(
            String roleName
    ) {

        if (roleName == null ||
                roleName.isBlank()) {

            throw new IllegalArgumentException(
                    "اسم الدور مطلوب."
            );
        }

        if (roleName.length() > 100) {

            throw new IllegalArgumentException(
                    "اسم الدور يجب ألا يتجاوز 100 حرف."
            );
        }
    }
// =====================================================
// الأدوار المعطلة
// =====================================================

    @Transactional(readOnly = true)
    public List<FasRole> getDisabledRoles() {

        return roleRepository
                .findByStatusOrderByRoleIdAsc(
                        "DISABLED"
                );
    }
}