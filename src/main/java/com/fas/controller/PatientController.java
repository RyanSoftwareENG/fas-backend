package com.fas.controller;

import com.fas.entity.Allergy;
import com.fas.entity.ChronicDisease;
import com.fas.entity.Client;
import com.fas.security.RequirePermission;
import com.fas.service.ClientService;
import com.fas.service.PatientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/patient")
public class PatientController {

    private final ClientService clientService;
    private final PatientService patientService;

    public PatientController(
            ClientService clientService,
            PatientService patientService
    ) {

        this.clientService =
                clientService;

        this.patientService =
                patientService;
    }

    // =====================================================
    // حفظ بيانات المريض كاملة
    // POST /api/patient
    // =====================================================

    @PostMapping
    @RequirePermission("PATIENT_CREATE")
    public ResponseEntity<Map<String, Object>>
    savePatient(
            @RequestBody Client patient
    ) {

        Client savedPatient =
                clientService.saveClient(
                        patient
                );

        patientService.savePatientHealthInfo(
                patient
        );

        Map<String, Object> response =
                new HashMap<>();

        response.put(
                "success",
                true
        );

        response.put(
                "clientID",
                savedPatient.getClientID()
        );

        response.put(
                "message",
                "تم حفظ بيانات المريض بنجاح"
        );

        return ResponseEntity
                .status(
                        HttpStatus.CREATED
                )
                .body(
                        response
                );
    }

    // =====================================================
    // جميع أنواع الحساسية
    // GET /api/patient/allergies
    // =====================================================

    @GetMapping("/allergies")
    @RequirePermission("ALLERGY_LIST")
    public ResponseEntity<List<Allergy>>
    getAllAllergies() {

        List<Allergy> allergies =
                patientService.getAllAllergies();

        return ResponseEntity.ok(
                allergies
        );
    }

    // =====================================================
    // تعديل اسم الحساسية
    // PUT /api/patient/allergies/{id}
    // =====================================================

    @PutMapping("/allergies/{id}")
    @RequirePermission("ALLERGY_UPDATE")
    public ResponseEntity<Allergy>
    updateAllergy(
            @PathVariable Long id,
            @RequestBody String newName
    ) {

        newName =
                newName
                        .replace(
                                "\"",
                                ""
                        )
                        .trim();

        Allergy updated =
                patientService.updateAllergy(
                        id,
                        newName
                );

        return ResponseEntity.ok(
                updated
        );
    }

    // =====================================================
    // جميع الأمراض المزمنة
    // GET /api/patient/diseases
    // =====================================================

    @GetMapping("/diseases")
    @RequirePermission("DISEASE_LIST")
    public ResponseEntity<List<ChronicDisease>>
    getAllChronicDiseases() {

        List<ChronicDisease> diseases =
                patientService
                        .getAllChronicDiseases();

        return ResponseEntity.ok(
                diseases
        );
    }

    // =====================================================
    // تعديل اسم المرض
    // PUT /api/patient/diseases/{id}
    // =====================================================

    @PutMapping("/diseases/{id}")
    @RequirePermission("DISEASE_UPDATE")
    public ResponseEntity<ChronicDisease>
    updateDisease(
            @PathVariable Long id,
            @RequestBody String newName
    ) {

        newName =
                newName
                        .replace(
                                "\"",
                                ""
                        )
                        .trim();

        ChronicDisease updated =
                patientService.updateDisease(
                        id,
                        newName
                );

        return ResponseEntity.ok(
                updated
        );
    }
}