package com.fas.controller;

import com.admin.entity.SubscriptionRequest;
import com.admin.service.SubscriptionRequestService;
import com.fas.dto.SubscriptionRequestCreateRequest;
import com.fas.dto.SubscriptionRequestResponse;
import com.fas.security.RequirePermission;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController("clientSubscriptionRequestController")
@RequestMapping("/api/subscription-requests")
public class SubscriptionRequestController {

    private final SubscriptionRequestService requestService;

    public SubscriptionRequestController(
            SubscriptionRequestService requestService
    ) {
        this.requestService =
                requestService;
    }

    // =====================================================
    // إنشاء طلب اشتراك / تجديد
    // POST /api/subscription-requests
    // =====================================================

    @PostMapping
    @RequirePermission("SUBSCRIPTION_REQUEST_CREATE")
    public ResponseEntity<?> createRequest(
            @RequestBody SubscriptionRequestCreateRequest request,
            HttpServletRequest httpRequest
    ) {

        try {

            if (request == null) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                "بيانات طلب الاشتراك مطلوبة."
                        );
            }

            Long clinicId =
                    getLongAttribute(
                            httpRequest,
                            "CLINIC_ID"
                    );

            if (clinicId == null) {

                return ResponseEntity
                        .status(
                                HttpStatus.UNAUTHORIZED
                        )
                        .body(
                                "تعذر تحديد عيادة المستخدم."
                        );
            }

            SubscriptionRequest savedRequest =
                    requestService.createRequest(
                            clinicId,
                            request.getPlanId(),
                            request.getRequestType(),
                            request.getOwnerNotes()
                    );

            return ResponseEntity
                    .status(
                            HttpStatus.CREATED
                    )
                    .body(
                            toResponse(
                                    savedRequest
                            )
                    );

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            e.getMessage()
                    );

        } catch (IllegalStateException e) {

            return ResponseEntity
                    .status(
                            HttpStatus.CONFLICT
                    )
                    .body(
                            e.getMessage()
                    );
        }
    }

    // =====================================================
    // عرض طلبات عيادتي
    // GET /api/subscription-requests
    // =====================================================

    @GetMapping
    @RequirePermission("SUBSCRIPTION_REQUEST_VIEW")
    public ResponseEntity<?> getMyRequests(
            HttpServletRequest httpRequest
    ) {

        try {

            Long clinicId =
                    getLongAttribute(
                            httpRequest,
                            "CLINIC_ID"
                    );

            if (clinicId == null) {

                return ResponseEntity
                        .status(
                                HttpStatus.UNAUTHORIZED
                        )
                        .body(
                                "تعذر تحديد عيادة المستخدم."
                        );
            }

            List<SubscriptionRequestResponse> responses =
                    requestService
                            .getClinicRequests(
                                    clinicId
                            )
                            .stream()
                            .map(
                                    this::toResponse
                            )
                            .collect(
                                    Collectors.toList()
                            );

            return ResponseEntity.ok(
                    responses
            );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(
                            HttpStatus.INTERNAL_SERVER_ERROR
                    )
                    .body(
                            "تعذر جلب طلبات الاشتراك."
                    );
        }
    }

    // =====================================================
    // تحويل Entity إلى DTO
    // =====================================================

    private SubscriptionRequestResponse toResponse(
            SubscriptionRequest request
    ) {

        SubscriptionRequestResponse response =
                new SubscriptionRequestResponse();

        response.setRequestId(
                request.getRequestId()
        );

        response.setPlanId(
                request.getPlanId()
        );

        response.setRequestType(
                request.getRequestType()
        );

        response.setRequestedAt(
                request.getRequestedAt()
        );

        response.setStatus(
                request.getStatus()
        );

        response.setReviewedAt(
                request.getReviewedAt()
        );

        response.setAdminNotes(
                request.getAdminNotes()
        );

        response.setOwnerNotes(
                request.getOwnerNotes()
        );

        return response;
    }

    // =====================================================
    // قراءة Request Attribute
    // =====================================================

    private Long getLongAttribute(
            HttpServletRequest request,
            String attributeName
    ) {

        Object value =
                request.getAttribute(
                        attributeName
                );

        if (!(value instanceof Number)) {

            return null;
        }

        return ((Number) value)
                .longValue();
    }
}
