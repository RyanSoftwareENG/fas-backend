package com.admin.security;

import com.admin.entity.AdminSession;
import com.admin.service.AdminSessionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AdminSessionFilter
        extends OncePerRequestFilter {

    private final AdminSessionService adminSessionService;

    public AdminSessionFilter(
            AdminSessionService adminSessionService
    ) {
        this.adminSessionService =
                adminSessionService;
    }

    // =====================================================
    // هذا الفلتر يعمل فقط على /api/admin/**
    // =====================================================

    @Override
    protected boolean shouldNotFilter(
            HttpServletRequest request
    ) {

        String requestURI =
                request.getRequestURI();

        return !requestURI.startsWith(
                "/api/admin/"
        );
    }

    // =====================================================
    // معالجة طلبات Admin
    // =====================================================

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String requestURI =
                request.getRequestURI();

        // =================================================
        // Login الخاص بالمدير لا يحتاج Session
        // =================================================

        if (requestURI.equals(
                "/api/admin/auth/login"
        )) {

            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }

        // =================================================
        // Authorization Header
        // =================================================

        String authorization =
                request.getHeader(
                        "Authorization"
                );

        if (authorization == null ||
                !authorization.startsWith(
                        "Bearer "
                )) {

            sendUnauthorized(
                    response,
                    "يجب تسجيل الدخول أولاً"
            );

            return;
        }

        // =================================================
        // استخراج Token
        // =================================================

        String token =
                authorization
                        .substring(7)
                        .trim();

        if (token.isBlank()) {

            sendUnauthorized(
                    response,
                    "رمز الجلسة غير صالح"
            );

            return;
        }

        // =================================================
        // التحقق من جلسة المدير
        // =================================================

        AdminSession session =
                adminSessionService.validateSession(
                        token
                );

        if (session == null) {

            sendUnauthorized(
                    response,
                    "الجلسة غير صالحة أو منتهية"
            );

            return;
        }

        // =================================================
        // وضع بيانات المدير داخل Request
        // =================================================

        request.setAttribute(
                "ADMIN_SESSION",
                session
        );

        if (session.getAdminUser() != null) {

            request.setAttribute(
                    "ADMIN_USER_ID",
                    session
                            .getAdminUser()
                            .getAdminUserId()
            );
        }

        // =================================================
        // متابعة الطلب
        // =================================================

        filterChain.doFilter(
                request,
                response
        );
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
                        escapeJson(message)
                )
        );
    }

    // =====================================================
    // حماية JSON
    // =====================================================

    private String escapeJson(
            String value
    ) {

        if (value == null) {
            return "";
        }

        return value
                .replace(
                        "\\",
                        "\\\\"
                )
                .replace(
                        "\"",
                        "\\\""
                )
                .replace(
                        "\r",
                        "\\r"
                )
                .replace(
                        "\n",
                        "\\n"
                );
    }
}