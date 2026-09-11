package com.fas.controller;

import com.admin.entity.SubscriptionPlan;
import com.admin.repository.SubscriptionPlanRepository;
import com.fas.dto.SubscriptionPlanResponse;
import com.fas.security.RequirePermission;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("clientSubscriptionPlanController")
@RequestMapping("/api/subscription-plans")
public class SubscriptionPlanController {

    private final SubscriptionPlanRepository planRepository;

    public SubscriptionPlanController(
            SubscriptionPlanRepository planRepository
    ) {
        this.planRepository =
                planRepository;
    }

    // =====================================================
    // الخطط النشطة
    // =====================================================

    @GetMapping("/active")
    public ResponseEntity<List<SubscriptionPlanResponse>>
    getActivePlans() {

        List<SubscriptionPlanResponse> response =
                planRepository
                        .findByStatus("ACTIVE")
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(
                response
        );
    }

    // =====================================================
    // تحويل Entity -> DTO
    // =====================================================

    private SubscriptionPlanResponse toResponse(
            SubscriptionPlan plan
    ) {

        SubscriptionPlanResponse response =
                new SubscriptionPlanResponse();

        response.setPlanId(
                plan.getPlanId()
        );

        response.setPlanName(
                plan.getPlanName()
        );

        response.setDescription(
                plan.getDescription()
        );

        response.setPrice(
                plan.getPrice()
        );

        response.setCurrencyCode(
                plan.getCurrencyCode()
        );

        response.setDurationDays(
                plan.getDurationDays()
        );

        response.setStatus(
                plan.getStatus()
        );

        return response;
    }
}