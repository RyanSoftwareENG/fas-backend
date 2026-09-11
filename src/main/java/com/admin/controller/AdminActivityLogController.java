package com.admin.controller;

import com.admin.dto.AdminActivityLogFilterRequest;
import com.admin.dto.AdminActivityLogResponse;
import com.admin.security.RequirePermission;
import com.admin.service.AdminActivityLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/activity-logs")
public class AdminActivityLogController {

    private final AdminActivityLogService service;

    public AdminActivityLogController(
            AdminActivityLogService service
    ) {
        this.service = service;
    }

    // =====================================================
    // جميع السجلات
    // =====================================================

    @GetMapping
    @RequirePermission("USER_MANAGE")
    public ResponseEntity<List<AdminActivityLogResponse>> getAll() {

        return ResponseEntity.ok(
                service.getAll()
        );
    }

    // =====================================================
    // البحث
    // =====================================================

    @PostMapping("/search")
    @RequirePermission("USER_MANAGE")
    public ResponseEntity<List<AdminActivityLogResponse>> search(
            @RequestBody AdminActivityLogFilterRequest request
    ) {

        return ResponseEntity.ok(
                service.search(request)
        );
    }

    // =====================================================
    // سجلات مدير معين
    // =====================================================

    @GetMapping("/admin/{adminUserId}")
    @RequirePermission("USER_MANAGE")
    public ResponseEntity<List<AdminActivityLogResponse>>
    getByAdminUser(
            @PathVariable Long adminUserId
    ) {

        return ResponseEntity.ok(
                service.getByAdminUser(adminUserId)
        );
    }
}