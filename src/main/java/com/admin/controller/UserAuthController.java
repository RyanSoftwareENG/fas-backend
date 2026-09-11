package com.admin.controller;

import com.admin.dto.DeviceRegistrationRequest;
import com.admin.dto.SessionValidationResponse;
import com.admin.dto.UserLoginRequest;
import com.admin.dto.UserLoginResponse;
import com.admin.service.SessionValidationService;
import com.admin.service.UserAuthService;
import com.admin.service.UserSessionService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class UserAuthController {

    private final UserAuthService userAuthService;

    private final UserSessionService userSessionService;

    private final SessionValidationService
            sessionValidationService;

    public UserAuthController(
            UserAuthService userAuthService,
            UserSessionService userSessionService,
            SessionValidationService sessionValidationService
    ) {

        this.userAuthService =
                userAuthService;

        this.userSessionService =
                userSessionService;

        this.sessionValidationService =
                sessionValidationService;
    }

    // =====================================================
    // Login
    // =====================================================

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> login(
            @RequestBody UserLoginRequest request
    ) {

        UserLoginResponse response =
                userAuthService.login(
                        request
                );

        if (response == null) {

            return ResponseEntity
                    .status(
                            HttpStatus.INTERNAL_SERVER_ERROR
                    )
                    .body(
                            new UserLoginResponse(
                                    false,
                                    "تعذر إتمام عملية تسجيل الدخول.",
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    false,
                                    false
                            )
                    );
        }

        if (!response.isSuccess()) {

            return ResponseEntity
                    .status(
                            HttpStatus.UNAUTHORIZED
                    )
                    .body(
                            response
                    );
        }

        /*
         * حالتان تصلان هنا:
         *
         * 1. جهاز مسجل:
         *    token موجود
         *    setupToken = null
         *
         * 2. جهاز جديد:
         *    token = null
         *    setupToken موجود
         *    needsSetup = true
         */
        return ResponseEntity.ok(
                response
        );
    }

    // =====================================================
    // Register Device
    // =====================================================

    @PostMapping("/register-device")
    public ResponseEntity<UserLoginResponse> registerDevice(
            @RequestHeader(
                    value = "X-Device-Setup-Token",
                    required = false
            )
            String setupToken,

            @RequestBody
            DeviceRegistrationRequest request
    ) {

        UserLoginResponse response =
                userAuthService.registerDevice(
                        setupToken,
                        request
                );

        if (response == null) {

            return ResponseEntity
                    .status(
                            HttpStatus.INTERNAL_SERVER_ERROR
                    )
                    .body(
                            new UserLoginResponse(
                                    false,
                                    "تعذر إتمام تسجيل الجهاز.",
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    false,
                                    false
                            )
                    );
        }

        if (!response.isSuccess()) {

            return ResponseEntity
                    .status(
                            HttpStatus.UNAUTHORIZED
                    )
                    .body(
                            response
                    );
        }

        /*
         * هنا يجب أن يحتوي response على:
         *
         * token      = Session Token الحقيقي
         * setupToken = null
         * deviceId   = الجهاز الجديد
         * needsSetup = false
         */
        return ResponseEntity.ok(
                response
        );
    }

    // =====================================================
    // Logout
    // =====================================================

    @PostMapping("/logout")
    public ResponseEntity<UserLoginResponse> logout(
            HttpServletRequest request
    ) {

        String token =
                extractBearerToken(
                        request
                );

        if (token == null) {

            return ResponseEntity
                    .status(
                            HttpStatus.UNAUTHORIZED
                    )
                    .body(
                            new UserLoginResponse(
                                    false,
                                    "رمز الجلسة غير موجود.",
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    false,
                                    false
                            )
                    );
        }

        boolean revoked =
                userSessionService.revokeSession(
                        token
                );

        if (!revoked) {

            return ResponseEntity
                    .status(
                            HttpStatus.UNAUTHORIZED
                    )
                    .body(
                            new UserLoginResponse(
                                    false,
                                    "تعذر إنهاء جلسة المستخدم.",
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    false,
                                    false
                            )
                    );
        }

        return ResponseEntity.ok(
                new UserLoginResponse(
                        true,
                        "تم تسجيل الخروج بنجاح.",
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        false,
                        false
                )
        );
    }

    // =====================================================
    // Validate Session
    // =====================================================

    @GetMapping("/session")
    public ResponseEntity<SessionValidationResponse>
    validateSession(
            HttpServletRequest request
    ) {

        String token =
                extractBearerToken(
                        request
                );

        if (token == null) {

            return ResponseEntity
                    .status(
                            HttpStatus.UNAUTHORIZED
                    )
                    .body(
                            new SessionValidationResponse(
                                    false,
                                    "رمز الجلسة غير موجود.",
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null
                            )
                    );
        }

        SessionValidationResponse response =
                sessionValidationService.validate(
                        token
                );

        if (!response.isValid()) {

            return ResponseEntity
                    .status(
                            HttpStatus.UNAUTHORIZED
                    )
                    .body(
                            response
                    );
        }

        return ResponseEntity.ok(
                response
        );
    }

    // =====================================================
    // استخراج Bearer Token
    // =====================================================

    private String extractBearerToken(
            HttpServletRequest request
    ) {

        String authorization =
                request.getHeader(
                        "Authorization"
                );

        if (authorization == null ||
                !authorization.startsWith(
                        "Bearer "
                )) {

            return null;
        }

        String token =
                authorization
                        .substring(7)
                        .trim();

        return token.isBlank()
                ? null
                : token;
    }
}