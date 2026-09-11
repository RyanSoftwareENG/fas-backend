package com.admin.controller;

import com.admin.dto.SubscriptionActivationRequest;
import com.admin.dto.SubscriptionActivationResponse;
import com.admin.service.SubscriptionActivationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/subscription-activations")
public class SubscriptionActivationController {

    private final SubscriptionActivationService service;

    public SubscriptionActivationController(
            SubscriptionActivationService service
    ) {
        this.service = service;
    }

    @PostMapping("/change-status")
    public ResponseEntity<SubscriptionActivationResponse>
    changeStatus(
            @RequestBody SubscriptionActivationRequest request
    ) {

        return ResponseEntity.ok(
                service.changeStatus(request)
        );
    }

    @GetMapping("/subscription/{subscriptionId}")
    public ResponseEntity<List<SubscriptionActivationResponse>>
    getHistory(
            @PathVariable Long subscriptionId
    ) {

        return ResponseEntity.ok(
                service.getHistory(subscriptionId)
        );
    }
}