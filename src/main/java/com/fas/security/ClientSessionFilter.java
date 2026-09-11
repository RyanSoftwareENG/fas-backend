package com.fas.security;

import com.admin.entity.UserSession;
import com.admin.service.UserSessionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import java.io.IOException;
import com.fas.config.ClinicContext;

@Component
@Order(Ordered.LOWEST_PRECEDENCE - 20)
public class ClientSessionFilter
        extends OncePerRequestFilter {


    // =====================================================
    // Request Attributes
    // =====================================================

    public static final String USER_ID_ATTRIBUTE =
            "USER_ID";

    public static final String CLINIC_ID_ATTRIBUTE =
            "CLINIC_ID";

    public static final String DEVICE_ID_ATTRIBUTE =
            "DEVICE_ID";

    // =====================================================
    // Service
    // =====================================================

    private final UserSessionService sessionService;

    // =====================================================
    // Constructor
    // =====================================================

    public ClientSessionFilter(
            UserSessionService sessionService
    ) {

        this.sessionService =
                sessionService;
    }

    // =====================================================
    // تحديد المسارات التي يعمل عليها الفلتر
    // =====================================================

    @Override
    protected boolean shouldNotFilter(
            HttpServletRequest request
    ) {

        String path =
                request.getRequestURI();

        // =====================================================
        // Admin
        // =====================================================

        if (path.startsWith(
                "/api/admin/"
        )) {

            return true;
        }

        // =====================================================
        // Login و Register Device
        // لا يحتاجان Session
        // =====================================================

        if (path.equals(
                "/api/auth/login"
        )) {

            return true;
        }

        if (path.equals(
                "/api/auth/register-device"
        )) {

            return true;
        }

        // =====================================================
        // Session
        // يجب أن يمر عبر ClientSessionFilter
        // =====================================================

        if (path.equals(
                "/api/auth/session"
        )) {

            return false;
        }

        // =====================================================
        // Logout
        // يحتاج Session
        // =====================================================

        if (path.equals(
                "/api/auth/logout"
        )) {

            return false;
        }

        // =====================================================
        // مسارات تطبيق العميل
        // =====================================================

        return !(
                path.equals("/api/clients")
                        || path.startsWith("/api/clients/")

                        || path.equals("/api/patient")
                        || path.startsWith("/api/patient/")

                        || path.equals("/api/food")
                        || path.startsWith("/api/food/")

                        || path.equals("/api/sessions")
                        || path.startsWith("/api/sessions/")

                        || path.equals("/api/session-report")
                        || path.startsWith("/api/session-report/")

                        || path.equals("/api/device")
                        || path.startsWith("/api/device/")

                        || path.equals("/api/subscription-requests")
                        || path.startsWith("/api/subscription-requests/")

                        || path.equals("/api/subscriptions")
                        || path.startsWith("/api/subscriptions/")
        );
    }

    // =====================================================
    // معالجة الطلب
    // =====================================================

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // =================================================
        // Authorization Header
        // =================================================

        String authorization =
                request.getHeader(
                        "Authorization"
                );

        /*
         * لا يوجد Token.
         *
         * لا نرفض هنا.
         * الـController / PermissionInterceptor
         * هو الذي يقرر هل الطلب يتطلب جلسة أم لا.
         */
        if (authorization == null ||
                authorization.isBlank()) {

            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }

        // =================================================
        // التحقق من Bearer
        // =================================================

        if (!authorization.startsWith(
                "Bearer "
        )) {

            sendUnauthorized(
                    response,
                    "صيغة Authorization غير صالحة."
            );

            return;
        }

        String token =
                authorization
                        .substring(
                                7
                        )
                        .trim();

        if (token.isBlank()) {

            sendUnauthorized(
                    response,
                    "رمز الجلسة مفقود."
            );

            return;
        }

        // =================================================
        // التحقق من الجلسة
        // =================================================

        UserSession session =
                sessionService.validateToken(
                        token
                );

        if (session == null) {

            sendUnauthorized(
                    response,
                    "الجلسة غير صالحة أو منتهية."
            );

            return;
        }

        // =================================================
        // التحقق من بيانات الجلسة
        // =================================================

        if (session.getUserId() == null) {

            sendUnauthorized(
                    response,
                    "معرف المستخدم داخل الجلسة غير صالح."
            );

            return;
        }

        if (session.getClinicId() == null) {

            sendUnauthorized(
                    response,
                    "معرف العيادة داخل الجلسة غير صالح."
            );

            return;
        }

        // =================================================
        // وضع بيانات الجلسة داخل الطلب
        // =================================================

        request.setAttribute(
                USER_ID_ATTRIBUTE,
                session.getUserId()
        );

        request.setAttribute(
                CLINIC_ID_ATTRIBUTE,
                session.getClinicId()
        );

        request.setAttribute(
                DEVICE_ID_ATTRIBUTE,
                session.getDeviceId()
        );

// =================================================
// تحديد العيادة الحالية
// =================================================

        ClinicContext.setClinicId(
                session.getClinicId()
        );

        try {

            // =================================================
            // متابعة الطلب
            // =================================================

            filterChain.doFilter(
                    request,
                    response
            );

        } finally {

            // =================================================
            // تنظيف ThreadLocal
            // =================================================

            ClinicContext.clear();
        }
    }

    // =====================================================
    // 401 Unauthorized
    // =====================================================

    private void sendUnauthorized(
            HttpServletResponse response,
            String message
    ) throws IOException {

        response.setStatus(
                HttpServletResponse.SC_UNAUTHORIZED
        );

        response.setContentType(
                "application/json;charset=UTF-8"
        );

        response.getWriter().write(
                """
                {
                    "success": false,
                    "message": "%s"
                }
                """.formatted(
                        message
                )
        );
    }
}