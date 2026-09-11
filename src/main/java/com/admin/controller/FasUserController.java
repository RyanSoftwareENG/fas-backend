package com.admin.controller;

import com.admin.dto.FasUserCreateRequest;
import com.admin.dto.FasUserResponse;
import com.admin.dto.FasUserUpdateRequest;
import com.admin.dto.UserRoleUpdateRequest;
import com.admin.entity.FasRole;
import com.admin.security.RequirePermission;
import com.admin.service.FasUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
public class FasUserController {

    private final FasUserService userService;

    public FasUserController(
            FasUserService userService
    ) {
        this.userService = userService;
    }

    // =========================================================
    // جميع المستخدمين
    // =========================================================

    @GetMapping
    @RequirePermission("USER_MANAGE")
    public ResponseEntity<List<FasUserResponse>> getAllUsers() {

        return ResponseEntity.ok(
                userService.getAllUsers()
        );
    }

    // =========================================================
    // مستخدمو عيادة محددة
    // =========================================================

    @GetMapping("/clinic/{clinicId}")
    @RequirePermission("USER_MANAGE")
    public ResponseEntity<List<FasUserResponse>> getUsersByClinic(
            @PathVariable Long clinicId
    ) {

        return ResponseEntity.ok(
                userService.getUsersByClinic(
                        clinicId
                )
        );
    }

    // =========================================================
    // مستخدم واحد
    // =========================================================

    @GetMapping("/{userId}")
    @RequirePermission("USER_MANAGE")
    public ResponseEntity<FasUserResponse> getUser(
            @PathVariable Long userId
    ) {

        return ResponseEntity.ok(
                userService.getUserById(
                        userId
                )
        );
    }

    // =========================================================
    // إنشاء مستخدم
    // =========================================================

    @PostMapping
    @RequirePermission("USER_MANAGE")
    public ResponseEntity<FasUserResponse> createUser(
            @RequestBody FasUserCreateRequest request
    ) {

        FasUserResponse response =
                userService.createUser(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // =========================================================
    // تعديل المستخدم
    // =========================================================

    @PutMapping("/{userId}")
    @RequirePermission("USER_MANAGE")
    public ResponseEntity<FasUserResponse> updateUser(
            @PathVariable Long userId,
            @RequestBody FasUserUpdateRequest request
    ) {

        return ResponseEntity.ok(
                userService.updateUser(
                        userId,
                        request
                )
        );
    }

    // =========================================================
    // تعليق المستخدم
    // =========================================================

    @PutMapping("/{userId}/suspend")
    @RequirePermission("USER_MANAGE")
    public ResponseEntity<FasUserResponse> suspendUser(
            @PathVariable Long userId
    ) {

        return ResponseEntity.ok(
                userService.suspendUser(userId)
        );
    }

    // =========================================================
    // إعادة تفعيل المستخدم
    // =========================================================

    @PutMapping("/{userId}/activate")
    @RequirePermission("USER_MANAGE")
    public ResponseEntity<FasUserResponse> activateUser(
            @PathVariable Long userId
    ) {

        return ResponseEntity.ok(
                userService.activateUser(userId)
        );
    }

    // =========================================================
    // جلب أدوار مستخدم محدد
    // =========================================================

    @GetMapping("/{userId}/roles")
    @RequirePermission("USER_MANAGE")
    public ResponseEntity<List<FasRole>> getUserRoles(
            @PathVariable Long userId
    ) {

        return ResponseEntity.ok(
                userService.getUserRoles(userId)
        );
    }

    // =========================================================
    // تعيين دور للمستخدم
    // =========================================================

    @PutMapping("/{userId}/role")
    @RequirePermission("USER_MANAGE")
    public ResponseEntity<Void> updateUserRole(
            @PathVariable Long userId,
            @RequestBody UserRoleUpdateRequest request
    ) {

        if (request == null ||
                request.getRoleId() == null) {

            throw new IllegalArgumentException(
                    "الدور مطلوب"
            );
        }

        userService.updateUserRole(
                userId,
                request.getRoleId()
        );

        return ResponseEntity.noContent().build();
    }
}