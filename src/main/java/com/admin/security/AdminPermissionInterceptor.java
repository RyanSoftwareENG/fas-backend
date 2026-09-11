package com.admin.security;

import com.admin.service.AdminPermissionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
public class AdminPermissionInterceptor
        implements HandlerInterceptor {

    private final AdminPermissionService permissionService;

    public AdminPermissionInterceptor(
            AdminPermissionService permissionService
    ) {
        this.permissionService =
                permissionService;
    }

    // =====================================================
    // التحقق من الصلاحية
    // =====================================================

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) throws Exception {

        // -------------------------------------------------
        // يجب أن يكون الطلب Controller Method
        // -------------------------------------------------

        if (!(handler instanceof HandlerMethod handlerMethod)) {

            return true;
        }

        // -------------------------------------------------
        // قراءة RequirePermission
        // -------------------------------------------------

        RequirePermission annotation =
                handlerMethod.getMethodAnnotation(
                        RequirePermission.class
                );

        // -------------------------------------------------
        // لا توجد صلاحية مطلوبة
        // -------------------------------------------------

        if (annotation == null) {

            return true;
        }

        // -------------------------------------------------
        // ADMIN_USER_ID
        // -------------------------------------------------

        Object userIdAttribute =
                request.getAttribute(
                        "ADMIN_USER_ID"
                );

        if (!(userIdAttribute instanceof Number number)) {

            sendForbidden(
                    response,
                    "ADMIN_SESSION_INVALID",
                    "تعذر تحديد المستخدم الإداري."
            );

            return false;
        }

        Long adminUserId =
                number.longValue();

        // -------------------------------------------------
        // الصلاحية المطلوبة
        // -------------------------------------------------

        String requiredPermission =
                annotation.value();

        if (requiredPermission == null ||
                requiredPermission.isBlank()) {

            sendForbidden(
                    response,
                    "PERMISSION_NOT_DEFINED",
                    "الصلاحية المطلوبة غير معرفة."
            );

            return false;
        }

        // -------------------------------------------------
        // التحقق من الصلاحية
        // -------------------------------------------------

        boolean hasPermission =
                permissionService.hasPermission(
                        adminUserId,
                        requiredPermission
                );

        if (!hasPermission) {

            sendForbidden(
                    response,
                    "PERMISSION_DENIED",
                    "ليس لديك صلاحية لتنفيذ هذه العملية."
            );

            return false;
        }

        return true;
    }

    // =====================================================
    // 403 Forbidden
    // =====================================================

    private void sendForbidden(
            HttpServletResponse response,
            String code,
            String message
    ) throws IOException {

        response.setStatus(
                HttpServletResponse.SC_FORBIDDEN
        );

        response.setContentType(
                "application/json"
        );

        response.setCharacterEncoding(
                "UTF-8"
        );

        response.getWriter().write(
                """
                {
                    "success": false,
                    "code": "%s",
                    "message": "%s"
                }
                """.formatted(
                        escapeJson(code),
                        escapeJson(message)
                )
        );
    }

    // =====================================================
    // JSON Escape
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