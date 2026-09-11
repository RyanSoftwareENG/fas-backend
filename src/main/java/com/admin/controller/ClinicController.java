package com.admin.controller;

import com.admin.dto.ClinicCreateRequest;
import com.admin.dto.ClinicResponse;
import com.admin.dto.ClinicUpdateRequest;
import com.admin.security.RequirePermission;
import com.admin.service.ClinicService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/clinics")
public class ClinicController {

    private final ClinicService clinicService;

    public ClinicController(
            ClinicService clinicService
    ) {
        this.clinicService = clinicService;
    }

    // =========================================================
    // عرض جميع العيادات
    // =========================================================

    @GetMapping
    @RequirePermission("CLINIC_VIEW")
    public ResponseEntity<List<ClinicResponse>> getAllClinics() {

        return ResponseEntity.ok(
                clinicService.getAllClinics()
        );
    }

    // =========================================================
    // عرض عيادة واحدة
    // =========================================================

    @GetMapping("/{clinicId}")
    @RequirePermission("CLINIC_VIEW")
    public ResponseEntity<ClinicResponse> getClinic(
            @PathVariable Long clinicId
    ) {

        return ResponseEntity.ok(
                clinicService.getClinicById(
                        clinicId
                )
        );
    }

    // =========================================================
    // إنشاء عيادة
    // =========================================================

    @PostMapping
    @RequirePermission("CLINIC_MANAGE")
    public ResponseEntity<ClinicResponse> createClinic(
            @RequestBody ClinicCreateRequest request
    ) {

        ClinicResponse response =
                clinicService.createClinic(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // =========================================================
    // تعديل عيادة
    // =========================================================

    @PutMapping("/{clinicId}")
    @RequirePermission("CLINIC_MANAGE")
    public ResponseEntity<ClinicResponse> updateClinic(
            @PathVariable Long clinicId,
            @RequestBody ClinicUpdateRequest request
    ) {

        return ResponseEntity.ok(
                clinicService.updateClinic(
                        clinicId,
                        request
                )
        );
    }

    // =========================================================
    // تعليق العيادة
    // =========================================================

    @PutMapping("/{clinicId}/suspend")
    @RequirePermission("CLINIC_MANAGE")
    public ResponseEntity<ClinicResponse> suspendClinic(
            @PathVariable Long clinicId
    ) {

        return ResponseEntity.ok(
                clinicService.suspendClinic(
                        clinicId
                )
        );
    }

    // =========================================================
    // إعادة تفعيل العيادة
    // =========================================================

    @PutMapping("/{clinicId}/activate")
    @RequirePermission("CLINIC_MANAGE")
    public ResponseEntity<ClinicResponse> activateClinic(
            @PathVariable Long clinicId
    ) {

        return ResponseEntity.ok(
                clinicService.activateClinic(
                        clinicId
                )
        );
    }

}