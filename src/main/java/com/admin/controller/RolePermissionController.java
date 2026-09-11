package com.admin.controller;

import com.admin.dto.RolePermissionResponse;
import com.admin.dto.RolePermissionUpdateRequest;
import com.admin.dto.RoleRequest;
import com.admin.entity.FasPermission;
import com.admin.entity.FasRole;
import com.admin.service.RolePermissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/roles")
public class RolePermissionController {

    private final RolePermissionService service;

    public RolePermissionController(
            RolePermissionService service
    ) {
        this.service = service;
    }

    // =====================================================
    // الأدوار النشطة
    // GET /api/admin/roles
    // =====================================================

    @GetMapping
    public ResponseEntity<List<FasRole>>
    getRoles() {

        return ResponseEntity.ok(
                service.getActiveRoles()
        );
    }

    // =====================================================
    // إنشاء دور
    // POST /api/admin/roles
    // =====================================================

    @PostMapping
    public ResponseEntity<FasRole>
    createRole(
            @RequestBody RoleRequest request
    ) {

        if (request == null) {

            throw new IllegalArgumentException(
                    "بيانات الدور مطلوبة."
            );
        }

        return ResponseEntity.ok(
                service.createRole(
                        request.getRoleName(),
                        request.getDescription()
                )
        );
    }

    // =====================================================
    // تعديل دور
    // PUT /api/admin/roles/{roleId}
    // =====================================================

    @PutMapping("/{roleId}")
    public ResponseEntity<FasRole>
    updateRole(
            @PathVariable Long roleId,
            @RequestBody RoleRequest request
    ) {

        if (request == null) {

            throw new IllegalArgumentException(
                    "بيانات الدور مطلوبة."
            );
        }

        return ResponseEntity.ok(
                service.updateRole(
                        roleId,
                        request.getRoleName(),
                        request.getDescription()
                )
        );
    }

    // =====================================================
    // تعطيل دور
    // DELETE /api/admin/roles/{roleId}
    // =====================================================

    @DeleteMapping("/{roleId}")
    public ResponseEntity<Void>
    disableRole(
            @PathVariable Long roleId
    ) {

        service.disableRole(
                roleId
        );

        return ResponseEntity.noContent()
                .build();
    }

    // =====================================================
    // إعادة تفعيل دور
    // PUT /api/admin/roles/{roleId}/enable
    // =====================================================

    @PutMapping("/{roleId}/enable")
    public ResponseEntity<Void>
    enableRole(
            @PathVariable Long roleId
    ) {

        service.enableRole(
                roleId
        );

        return ResponseEntity.noContent()
                .build();
    }

    // =====================================================
    // جميع الصلاحيات النشطة
    // GET /api/admin/roles/permissions
    // =====================================================

    @GetMapping("/permissions")
    public ResponseEntity<List<FasPermission>>
    getPermissions() {

        return ResponseEntity.ok(
                service.getActivePermissions()
        );
    }

    // =====================================================
    // صلاحيات دور محدد
    // GET /api/admin/roles/{roleId}/permissions
    // =====================================================

    @GetMapping("/{roleId}/permissions")
    public ResponseEntity<List<RolePermissionResponse>>
    getRolePermissions(
            @PathVariable Long roleId
    ) {

        return ResponseEntity.ok(
                service.getRolePermissions(
                        roleId
                )
        );
    }

    // =====================================================
    // تحديث صلاحيات الدور
    // PUT /api/admin/roles/{roleId}/permissions
    // =====================================================

    @PutMapping("/{roleId}/permissions")
    public ResponseEntity<Void>
    updateRolePermissions(
            @PathVariable Long roleId,
            @RequestBody RolePermissionUpdateRequest request
    ) {

        service.updateRolePermissions(
                roleId,
                request
        );

        return ResponseEntity.noContent()
                .build();
    }
// =====================================================
// الأدوار المعطلة
// GET /api/admin/roles/disabled
// =====================================================

    @GetMapping("/disabled")
    public ResponseEntity<List<FasRole>>
    getDisabledRoles() {

        return ResponseEntity.ok(
                service.getDisabledRoles()
        );
    }

}