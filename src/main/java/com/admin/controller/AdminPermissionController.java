package com.admin.controller;

import com.admin.security.RequirePermission;
import com.admin.service.AdminPermissionService;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Set;

@RestController
@RequestMapping("/api/admin/permissions")
public class AdminPermissionController {

    private final AdminPermissionService permissionService;

    public AdminPermissionController(
            AdminPermissionService permissionService
    ) {
        this.permissionService = permissionService;
    }

    @GetMapping("/my")
    @RequirePermission("CLINIC_VIEW")
    public Set<String> getMyPermissions(
            HttpServletRequest request
    ) {

        Long adminUserId =
                (Long) request.getAttribute(
                        "ADMIN_USER_ID"
                );

        return permissionService
                .getPermissionCodes(adminUserId);
    }

}