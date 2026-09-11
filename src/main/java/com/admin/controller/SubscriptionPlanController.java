package com.admin.controller;

import com.admin.dto.SubscriptionPlanRequest;
import com.admin.dto.SubscriptionPlanResponse;
import com.admin.service.SubscriptionPlanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/subscription-plans")
public class SubscriptionPlanController {

    private final SubscriptionPlanService service;

    public SubscriptionPlanController(
            SubscriptionPlanService service
    ) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<SubscriptionPlanResponse> create(
            @RequestBody SubscriptionPlanRequest request
    ) {

        return ResponseEntity.ok(
                service.create(request)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubscriptionPlanResponse> update(
            @PathVariable Long id,
            @RequestBody SubscriptionPlanRequest request
    ) {

        return ResponseEntity.ok(
                service.update(id, request)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionPlanResponse> getById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                service.getById(id)
        );
    }

    @GetMapping
    public ResponseEntity<List<SubscriptionPlanResponse>> getAll() {

        return ResponseEntity.ok(
                service.getAll()
        );
    }

    @GetMapping("/active")
    public ResponseEntity<List<SubscriptionPlanResponse>> getActive() {

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