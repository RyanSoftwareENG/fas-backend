package com.admin.controller;

import com.admin.dto.SoftwareVersionRequest;
import com.admin.dto.SoftwareVersionResponse;
import com.admin.service.SoftwareVersionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/versions")
public class SoftwareVersionController {

    private final SoftwareVersionService service;

    public SoftwareVersionController(
            SoftwareVersionService service
    ) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<SoftwareVersionResponse> create(
            @RequestBody SoftwareVersionRequest request
    ) {
        return ResponseEntity.ok(
                service.create(request)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<SoftwareVersionResponse> update(
            @PathVariable Long id,
            @RequestBody SoftwareVersionRequest request
    ) {
        return ResponseEntity.ok(
                service.update(id, request)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<SoftwareVersionResponse> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                service.getById(id)
        );
    }

    @GetMapping
    public ResponseEntity<List<SoftwareVersionResponse>> getAll() {

        return ResponseEntity.ok(
                service.getAll()
        );
    }

    @GetMapping("/application/{applicationName}")
    public ResponseEntity<List<SoftwareVersionResponse>>
    getByApplication(
            @PathVariable String applicationName
    ) {

        return ResponseEntity.ok(
                service.getByApplication(applicationName)
        );
    }

    @GetMapping("/latest/{applicationName}")
    public ResponseEntity<SoftwareVersionResponse> getLatest(
            @PathVariable String applicationName
    ) {

        return ResponseEntity.ok(
                service.getLatest(applicationName)
        );
    }

    @GetMapping("/latest-active/{applicationName}")
    public ResponseEntity<SoftwareVersionResponse> getLatestActive(
            @PathVariable String applicationName
    ) {

        return ResponseEntity.ok(
                service.getLatestActive(applicationName)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {

        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}