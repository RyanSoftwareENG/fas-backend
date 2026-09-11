package com.admin.controller;

import com.admin.dto.AdminLoginRequest;
import com.admin.dto.AdminLoginResponse;
import com.admin.entity.AdminSession;
import com.admin.service.AdminSessionService;
import com.admin.service.FasAdminAuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/auth")
public class FasAdminAuthController {

    private final FasAdminAuthService authService;
    private final AdminSessionService adminSessionService;

    public FasAdminAuthController(
            FasAdminAuthService authService,
            AdminSessionService adminSessionService
    ) {
        this.authService = authService;
        this.adminSessionService = adminSessionService;
    }

    // =========================================================
    // LOGIN
    // =========================================================

    @PostMapping("/login")
    public ResponseEntity<AdminLoginResponse> login(
            @RequestBody AdminLoginRequest request
    ) {

        AdminLoginResponse response =
                authService.login(request);

        if (!response.isSuccess()) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(response);
        }

        return ResponseEntity.ok(response);
    }

    // =========================================================
    // LOGOUT
    // =========================================================

    @PostMapping("/logout")
    public ResponseEntity<AdminLoginResponse> logout(
            HttpServletRequest request
    ) {

        // =====================================================
        // الجلسة تم التحقق منها مسبقاً بواسطة AdminSessionFilter
        // =====================================================

        AdminSession session =
                (AdminSession) request.getAttribute(
                        "ADMIN_SESSION"
                );

        if (session == null) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(
                            new AdminLoginResponse(
                                    false,
                                    "لا توجد جلسة فعالة",
                                    null,
                                    null,
                                    null,
                                    null
                            )
                    );
        }

        // =====================================================
        // قراءة Token
        // =====================================================

        String authorization =
                request.getHeader("Authorization");

        if (authorization == null
                || !authorization.startsWith("Bearer ")) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(
                            new AdminLoginResponse(
                                    false,
                                    "رمز الجلسة غير موجود",
                                    null,
                                    null,
                                    null,
                                    null
                            )
                    );
        }

        String token =
                authorization.substring(7).trim();

        // =====================================================
        // إلغاء الجلسة
        // =====================================================

        boolean revoked =
                adminSessionService.revokeSession(token);

        if (!revoked) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(
                            new AdminLoginResponse(
                                    false,
                                    "تعذر إنهاء الجلسة",
                                    null,
                                    null,
                                    null,
                                    null
                            )
                    );
        }

        // =====================================================
        // نجاح تسجيل الخروج
        // =====================================================

        return ResponseEntity.ok(
                new AdminLoginResponse(
                        true,
                        "تم تسجيل الخروج بنجاح",
                        null,
                        null,
                        null,
                        null
                )
        );
    }
}