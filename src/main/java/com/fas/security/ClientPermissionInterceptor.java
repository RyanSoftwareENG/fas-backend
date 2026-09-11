package com.fas.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
public class ClientPermissionInterceptor
        implements HandlerInterceptor {

    private final ClientPermissionService permissionService;

    public ClientPermissionInterceptor(
            ClientPermissionService permissionService
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

        /*
         * نتأكد أن الطلب متجه إلى Method داخل Controller.
         */
        if (!(handler instanceof HandlerMethod handlerMethod)) {

            return true;
        }

        /*
         * قراءة @RequirePermission من الـEndpoint.
         */
        RequirePermission annotation =
                handlerMethod.getMethodAnnotation(
                        RequirePermission.class
                );

        /*
         * لا توجد صلاحية مطلوبة.
         */
        if (annotation == null) {

            return true;
        }

        // =================================================
        // الحصول على USER_ID
        // =================================================

        Object userIdAttribute =
                request.getAttribute(
                        ClientSessionFilter.USER_ID_ATTRIBUTE
                );

        if (!(userIdAttribute instanceof Long userId)) {

            sendUnauthorized(
                    response,
                    "تعذر تحديد مستخدم العيادة."
            );

            return false;
        }

        // =================================================
        // الصلاحية المطلوبة
        // =================================================

        String requiredPermission =
                annotation.value();

        if (requiredPermission == null ||
                requiredPermission.isBlank()) {

            sendForbidden(
                    response,
                    "الصلاحية المطلوبة غير معرفة."
            );

            return false;
        }

        // =================================================
        // التحقق من الصلاحية
        // =================================================

        boolean hasPermission =
                permissionService.hasPermission(
                        userId,
                        requiredPermission
                );

        if (!hasPermission) {

            sendForbidden(
                    response,
                    "ليس لديك صلاحية لتنفيذ هذه العملية."
            );

            return false;
        }

        return true;
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
    // 403 Forbidden
    // =====================================================

    private void sendForbidden(
            HttpServletResponse response,
            String message
    ) throws IOException {

        response.setStatus(
                HttpServletResponse.SC_FORBIDDEN
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
    // حماية النص داخل JSON
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