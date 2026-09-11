package com.fas.controller;

import com.admin.dto.SubscriptionResponse;
import com.admin.entity.Subscription;
import com.admin.repository.SubscriptionRepository;
import com.fas.security.RequirePermission;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController("clientSubscriptionController")
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionController(
            SubscriptionRepository subscriptionRepository
    ) {
        this.subscriptionRepository =
                subscriptionRepository;
    }

    // =====================================================
    // جميع اشتراكات عيادتي
    // =====================================================

    @GetMapping
    @RequirePermission("SUBSCRIPTION_REQUEST_VIEW")
    public ResponseEntity<?> getMySubscriptions(
            HttpServletRequest request
    ) {

        Long clinicId =
                getClinicId(request);

        if (clinicId == null) {

            return ResponseEntity
                    .status(
                            HttpStatus.UNAUTHORIZED
                    )
                    .body(
                            "تعذر تحديد عيادة المستخدم."
                    );
        }

        List<SubscriptionResponse> result =
                subscriptionRepository
                        .findByClinic_ClinicIdOrderByEndDateDesc(
                                clinicId
                        )
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(
                result
        );
    }

    // =====================================================
    // الاشتراك الحالي
    // =====================================================

    @GetMapping("/current")
    @RequirePermission("SUBSCRIPTION_REQUEST_VIEW")
    public ResponseEntity<?> getCurrentSubscription(
            HttpServletRequest request
    ) {

        Long clinicId =
                getClinicId(request);

        if (clinicId == null) {

            return ResponseEntity
                    .status(
                            HttpStatus.UNAUTHORIZED
                    )
                    .body(
                            "تعذر تحديد عيادة المستخدم."
                    );
        }

        LocalDate today =
                LocalDate.now();

        Subscription subscription =
                subscriptionRepository
                        .findFirstByClinic_ClinicIdAndStatusOrderByEndDateDesc(
                                clinicId,
                                "ACTIVE"
                        )
                        .filter(
                                item ->
                                        item.getStartDate() == null
                                                ||
                                                !today.isBefore(
                                                        item.getStartDate()
                                                )
                        )
                        .filter(
                                item ->
                                        item.getEndDate() == null
                                                ||
                                                !today.isAfter(
                                                        item.getEndDate()
                                                )
                        )
                        .orElse(null);

        if (subscription == null) {

            return ResponseEntity.ok(
                    null
            );
        }

        return ResponseEntity.ok(
                toResponse(
                        subscription
                )
        );
    }

    // =====================================================
    // استخراج CLINIC_ID
    // =====================================================

    private Long getClinicId(
            HttpServletRequest request
    ) {

        Object value =
                request.getAttribute(
                        "CLINIC_ID"
                );

        if (!(value instanceof Number)) {

            return null;
        }

        return ((Number) value)
                .longValue();
    }

    // =====================================================
    // تحويل Entity إلى DTO
    // =====================================================

    private SubscriptionResponse toResponse(
            Subscription subscription
    ) {

        SubscriptionResponse response =
                new SubscriptionResponse();

        response.setSubscriptionId(
                subscription.getSubscriptionId()
        );

        response.setStartDate(
                subscription.getStartDate()
        );

        response.setEndDate(
                subscription.getEndDate()
        );

        response.setStatus(
                subscription.getStatus()
        );

        if (subscription.getClinic() != null) {

            response.setClinicId(
                    subscription
                            .getClinic()
                            .getClinicId()
            );

            response.setClinicName(
                    subscription
                            .getClinic()
                            .getClinicName()
            );
        }

        if (subscription.getPlan() != null) {

            response.setPlanId(
                    subscription
                            .getPlan()
                            .getPlanId()
            );

            response.setPlanName(
                    subscription
                            .getPlan()
                            .getPlanName()
            );

            response.setPrice(
                    subscription
                            .getPlan()
                            .getPrice()
            );

            response.setCurrencyCode(
                    subscription
                            .getPlan()
                            .getCurrencyCode()
            );

            response.setDurationDays(
                    subscription
                            .getPlan()
                            .getDurationDays()
            );
        }

        return response;
    }
}