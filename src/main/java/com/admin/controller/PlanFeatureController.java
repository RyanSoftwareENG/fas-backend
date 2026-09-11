package com.admin.controller;

import com.admin.dto.PlanFeatureRequest;
import com.admin.dto.PlanFeatureResponse;
import com.admin.service.PlanFeatureService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/plan-features")
public class PlanFeatureController {

    private final PlanFeatureService service;

    public PlanFeatureController(
            PlanFeatureService service
    ) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PlanFeatureResponse> assign(
            @RequestBody PlanFeatureRequest request
    ) {

        return ResponseEntity.ok(
                service.assign(request)
        );
    }

    @PutMapping("/{planId}/{featureId}")
    public ResponseEntity<PlanFeatureResponse> update(
            @PathVariable Long planId,
            @PathVariable Long featureId,
            @RequestBody PlanFeatureRequest request
    ) {

        return ResponseEntity.ok(
                service.update(
                        planId,
                        featureId,
                        request
                )
        );
    }

    @GetMapping("/plan/{planId}")
    public ResponseEntity<List<PlanFeatureResponse>> getByPlan(
            @PathVariable Long planId
    ) {

        return ResponseEntity.ok(
                service.getByPlan(planId)
        );
    }

    @GetMapping("/plan/{planId}/enabled")
    public ResponseEntity<List<PlanFeatureResponse>>
    getEnabledByPlan(
            @PathVariable Long planId
    ) {

        return ResponseEntity.ok(
                service.getEnabledByPlan(planId)
        );
    }

    @DeleteMapping("/{planId}/{featureId}")
    public ResponseEntity<Void> remove(
            @PathVariable Long planId,
            @PathVariable Long featureId
    ) {

        service.remove(
                planId,
                featureId
        );

        return ResponseEntity.noContent().build();
    }
}