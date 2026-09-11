package com.admin.controller;

import com.admin.dto.AdminDashboardResponse;
import com.admin.security.RequirePermission;
import com.admin.service.AdminDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {

    private final AdminDashboardService dashboardService;

    public AdminDashboardController(
            AdminDashboardService dashboardService
    ) {
        this.dashboardService =
                dashboardService;
    }

    @GetMapping
    @RequirePermission("CLINIC_VIEW")
    public ResponseEntity<AdminDashboardResponse>
    getDashboard() {

        return ResponseEntity.ok(
                dashboardService.getDashboard()
        );
    }
}