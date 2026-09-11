package com.admin.controller;

import com.admin.dto.SubscriptionRequestCreateRequest;
import com.admin.dto.SubscriptionResponse;
import com.admin.service.SubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(
            SubscriptionService subscriptionService
    ) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping
    public ResponseEntity<SubscriptionResponse> create(
            @RequestBody SubscriptionRequestCreateRequest request
    ) {
        return ResponseEntity.ok(
                subscriptionService.create(request)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubscriptionResponse> update(
            @PathVariable Long id,
            @RequestBody SubscriptionRequestCreateRequest request
    ) {
        return ResponseEntity.ok(
                subscriptionService.update(id, request)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionResponse> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                subscriptionService.getById(id)
        );
    }

    @GetMapping("/clinic/{clinicId}")
    public ResponseEntity<List<SubscriptionResponse>> getByClinic(
            @PathVariable Long clinicId
    ) {
        return ResponseEntity.ok(
                subscriptionService.getByClinic(clinicId)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {
        subscriptionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}