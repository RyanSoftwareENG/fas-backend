package com.fas.controller;

import com.fas.dto.*;
import com.fas.service.DashboardReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportsController {

    private final DashboardReportService dashboardReportService;

    public ReportsController(
            DashboardReportService dashboardReportService
    ) {
        this.dashboardReportService =
                dashboardReportService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> getDashboard() {

        return ResponseEntity.ok(
                dashboardReportService.getDashboard()
        );
    }
    @GetMapping("/sessions/trend")
    public ResponseEntity<List<SessionTrendResponse>> getSessionTrend(
            @RequestParam(defaultValue = "30") int days
    ) {

        return ResponseEntity.ok(
                dashboardReportService.getSessionTrend(days)
        );
    }
    @GetMapping("/nutrition-plans/status")
    public ResponseEntity<List<PlanStatusResponse>>
    getPlanStatuses() {

        return ResponseEntity.ok(
                dashboardReportService.getPlanStatuses()
        );
    }
    @GetMapping("/chronic-diseases")
    public ResponseEntity<List<ChronicDiseaseReportResponse>>
    getChronicDiseaseStatistics() {

        return ResponseEntity.ok(
                dashboardReportService
                        .getChronicDiseaseStatistics()
        );
    }
    @GetMapping("/allergies")
    public ResponseEntity<List<AllergyReportResponse>>
    getAllergyStatistics() {

        return ResponseEntity.ok(
                dashboardReportService
                        .getAllergyStatistics()
        );
    }
    @GetMapping("/body-progress/{clientId}")
    public ResponseEntity<List<BodyProgressResponse>>
    getBodyProgress(
            @PathVariable Long clientId
    ) {

        return ResponseEntity.ok(
                dashboardReportService
                        .getBodyProgress(clientId)
        );
    }
    @GetMapping("/body-progress/{clientId}/summary")
    public ResponseEntity<PatientProgressSummary>
    getPatientProgressSummary(
            @PathVariable Long clientId
    ) {

        return ResponseEntity.ok(
                dashboardReportService
                        .getPatientProgressSummary(clientId)
        );
    }
    @GetMapping("/sessions/recent")
    public ResponseEntity<List<RecentSessionResponse>>
    getRecentSessions(
            @RequestParam(defaultValue = "10") int limit
    ) {

        return ResponseEntity.ok(
                dashboardReportService
                        .getRecentSessions(limit)
        );
    }
    @GetMapping("/revenue/trend")
    public ResponseEntity<List<RevenueTrendResponse>>
    getRevenueTrend(
            @RequestParam(defaultValue = "30") int days
    ) {

        return ResponseEntity.ok(
                dashboardReportService
                        .getRevenueTrend(days)
        );
    }
    @GetMapping("/alerts")
    public ResponseEntity<List<ReportAlertResponse>>
    getAlerts() {

        return ResponseEntity.ok(
                dashboardReportService.getAlerts()
        );
    }
    @GetMapping("/dashboard/full")
    public ResponseEntity<FullDashboardResponse> getFullDashboard(
            @RequestParam(defaultValue = "30") int days,
            @RequestParam(defaultValue = "10") int recentLimit
    ) {

        return ResponseEntity.ok(
                dashboardReportService.getFullDashboard(
                        days,
                        recentLimit
                )
        );
    }
}