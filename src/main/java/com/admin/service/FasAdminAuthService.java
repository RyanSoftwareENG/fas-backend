package com.admin.service;

import com.admin.dto.AdminLoginRequest;
import com.admin.dto.AdminLoginResponse;
import com.admin.entity.FasAdminUser;
import com.admin.repository.FasAdminUserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class FasAdminAuthService {

    private final FasAdminUserRepository userRepository;
    private final AdminSessionService adminSessionService;

    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder(12);

    public FasAdminAuthService(
            FasAdminUserRepository userRepository,
            AdminSessionService adminSessionService
    ) {
        this.userRepository = userRepository;
        this.adminSessionService = adminSessionService;
    }

    public AdminLoginResponse login(
            AdminLoginRequest request
    ) {

        // =====================================================
        // 1. البحث عن المستخدم
        // =====================================================

        FasAdminUser user =
                userRepository
                        .findByUsername(request.getUsername())
                        .orElse(null);

        if (user == null) {

            return new AdminLoginResponse(
                    false,
                    "اسم المستخدم أو كلمة المرور غير صحيحة",
                    null,
                    null,
                    null,
                    null
            );
        }

        // =====================================================
        // 2. التحقق من حالة الحساب
        // =====================================================

        if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {

            return new AdminLoginResponse(
                    false,
                    "الحساب غير فعال",
                    null,
                    null,
                    null,
                    null
            );
        }

        // =====================================================
        // 3. التحقق من كلمة المرور باستخدام BCrypt
        // =====================================================

        boolean passwordMatches =
                passwordEncoder.matches(
                        request.getPassword(),
                        user.getPasswordHash()
                );

        if (!passwordMatches) {

            return new AdminLoginResponse(
                    false,
                    "اسم المستخدم أو كلمة المرور غير صحيحة",
                    null,
                    null,
                    null,
                    null
            );
        }

        // =====================================================
        // 4. إنشاء جلسة Admin
        // =====================================================
        // AdminSessionService:
        // - ينشئ Token عشوائي
        // - يحسب SHA-256 للـToken
        // - يخزن الـHash في ADMIN_SESSION
        // - يحدد مدة الجلسة
        // - يعيد الـToken الأصلي للعميل

        String token =
                adminSessionService.createSession(user);

        // =====================================================
        // 5. تحديث آخر تسجيل دخول
        // =====================================================

        user.setLastLoginAt(
                LocalDateTime.now()
        );

        userRepository.save(user);

        // =====================================================
        // 6. إرجاع نتيجة تسجيل الدخول
        // =====================================================

        return new AdminLoginResponse(
                true,
                "تم تسجيل الدخول بنجاح",
                user.getAdminUserId(),
                user.getUsername(),
                user.getFullName(),
                token
        );
    }
}