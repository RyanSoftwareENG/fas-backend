package com.admin.controller;

import com.admin.entity.SubscriptionRequest;
import com.admin.service.SubscriptionRequestService;

import com.fas.dto.SubscriptionRequestResponse;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("adminSubscriptionRequestController")
@RequestMapping("/api/admin/subscription-requests")
public class SubscriptionRequestController {

    private final SubscriptionRequestService requestService;

    public SubscriptionRequestController(
            SubscriptionRequestService requestService
    ) {
        this.requestService =
                requestService;
    }

    // =====================================================
    // جميع الطلبات المعلقة
    // GET /api/admin/subscription-requests/pending
    // =====================================================

    @GetMapping("/pending")
    public ResponseEntity<?> getPendingRequests(
            HttpServletRequest request
    ) {

        try {

            validateAdminSession(
                    request
            );

            List<SubscriptionRequest> requests =
                    requestService.getPendingRequests();

            return ResponseEntity.ok(
                    requests
            );

        } catch (SecurityException e) {

            return ResponseEntity
                    .status(
                            HttpStatus.FORBIDDEN
                    )
                    .body(
                            e.getMessage()
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
    // الموافقة على طلب
    // POST /api/admin/subscription-requests/{id}/approve
    // =====================================================

    @PostMapping("/{requestId}/approve")
    public ResponseEntity<?> approve(
            @PathVariable Long requestId,
            @RequestBody(required = false) String adminNotes,
            HttpServletRequest request
    ) {

        try {

            Long adminUserId =
                    validateAdminSession(
                            request
                    );

            SubscriptionRequest approvedRequest =
                    requestService.approve(
                            requestId,
                            adminUserId,
                            clean(adminNotes)
                    );

            return ResponseEntity.ok(
                    approvedRequest
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

        } catch (SecurityException e) {

            return ResponseEntity
                    .status(
                            HttpStatus.FORBIDDEN
                    )
                    .body(
                            e.getMessage()
                    );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(
                            HttpStatus.INTERNAL_SERVER_ERROR
                    )
                    .body(
                            "تعذر الموافقة على طلب الاشتراك."
                    );
        }
    }

    // =====================================================
    // رفض الطلب
    // POST /api/admin/subscription-requests/{id}/reject
    // =====================================================

    @PostMapping("/{requestId}/reject")
    public ResponseEntity<?> reject(
            @PathVariable Long requestId,
            @RequestBody(required = false) String adminNotes,
            HttpServletRequest request
    ) {

        try {

            Long adminUserId =
                    validateAdminSession(
                            request
                    );

            SubscriptionRequest rejectedRequest =
                    requestService.reject(
                            requestId,
                            adminUserId,
                            clean(adminNotes)
                    );

            return ResponseEntity.ok(
                    rejectedRequest
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

        } catch (SecurityException e) {

            return ResponseEntity
                    .status(
                            HttpStatus.FORBIDDEN
                    )
                    .body(
                            e.getMessage()
                    );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(
                            HttpStatus.INTERNAL_SERVER_ERROR
                    )
                    .body(
                            "تعذر رفض طلب الاشتراك."
                    );
        }
    }

    // =====================================================
    // التحقق من جلسة Admin
    // =====================================================

    private Long validateAdminSession(
            HttpServletRequest request
    ) {

        Object adminUserIdAttribute =
                request.getAttribute(
                        "ADMIN_USER_ID"
                );

        if (!(adminUserIdAttribute instanceof Number number)) {

            throw new SecurityException(
                    "جلسة مدير النظام غير صالحة."
            );
        }

        return number.longValue();
    }

    // =====================================================
    // تنظيف الملاحظات
    // =====================================================

    private String clean(
            String value
    ) {

        if (value == null) {
            return null;
        }

        String trimmed =
                value.trim();

        return trimmed.isBlank()
                ? null
                : trimmed;
    }
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
}