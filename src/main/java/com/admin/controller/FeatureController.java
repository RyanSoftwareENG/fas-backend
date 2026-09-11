package com.admin.controller;

import com.admin.dto.FeatureRequest;
import com.admin.dto.FeatureResponse;
import com.admin.service.FeatureService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/features")
public class FeatureController {

    private final FeatureService service;

    public FeatureController(
            FeatureService service
    ) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<FeatureResponse> create(
            @RequestBody FeatureRequest request
    ) {

        return ResponseEntity.ok(
                service.create(request)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<FeatureResponse> update(
            @PathVariable Long id,
            @RequestBody FeatureRequest request
    ) {

        return ResponseEntity.ok(
                service.update(id, request)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeatureResponse> getById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                service.getById(id)
        );
    }

    @GetMapping
    public ResponseEntity<List<FeatureResponse>> getAll() {

        return ResponseEntity.ok(
                service.getAll()
        );
    }

    @GetMapping("/active")
    public ResponseEntity<List<FeatureResponse>> getActive() {

        return ResponseEntity.ok(
                service.getActive()
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