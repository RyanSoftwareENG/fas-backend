package com.fas.controller;

import com.fas.entity.SessionReport;
import com.fas.service.SessionReportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/session-report")
public class SessionReportController {

    private final SessionReportService reportService;

    public SessionReportController(
            SessionReportService reportService) {

        this.reportService = reportService;
    }

    // =====================================================
    // GET
    // جميع التقارير
    // /api/session-report
    // =====================================================

    @GetMapping
    public ResponseEntity<List<SessionReport>> getAllReports() {

        List<SessionReport> reports =
                reportService.getAllReports();

        return ResponseEntity.ok(reports);
    }


    // =====================================================
    // GET
    // تقرير بواسطة Session ID
    // /api/session-report/session/{sessionId}
    // =====================================================

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<SessionReport> getReportBySessionId(
            @PathVariable Long sessionId) {

        SessionReport report =
                reportService.getReportBySessionId(sessionId);

        if (report == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(report);
    }

    // =====================================================
    // GET
    // تقرير بواسطة Report ID
    // /api/session-report/{reportId}
    // =====================================================

    @GetMapping("/{reportId:\\d+}")
    public ResponseEntity<SessionReport> getReportById(
            @PathVariable Long reportId) {

        SessionReport report =
                reportService.getReportById(reportId);

        if (report == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(report);
    }

    // =====================================================
    // POST
    // حفظ تقرير جديد
    // /api/session-report
    // =====================================================

    @PostMapping
    public ResponseEntity<SessionReport> saveReport(
            @RequestBody SessionReport report) {

        SessionReport savedReport =
                reportService.saveReport(report);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedReport);
    }

    // =====================================================
    // PUT
    // تحديث تقرير
    // /api/session-report/{reportId}
    // =====================================================

    @PutMapping("/{reportId:\\d+}")
    public ResponseEntity<SessionReport> updateReport(
            @PathVariable Long reportId,
            @RequestBody SessionReport report) {

        SessionReport updatedReport =
                reportService.updateReport(
                        reportId,
                        report
                );

        return ResponseEntity.ok(updatedReport);
    }

    // =====================================================
    // DELETE
    // حذف تقرير
    // /api/session-report/{reportId}
    // =====================================================

    @DeleteMapping("/{reportId:\\d+}")
    public ResponseEntity<Void> deleteReport(
            @PathVariable Long reportId) {

        reportService.deleteReport(reportId);

        return ResponseEntity.noContent().build();
    }
}