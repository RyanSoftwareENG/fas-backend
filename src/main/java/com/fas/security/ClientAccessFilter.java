package com.fas.security;

import com.admin.entity.Device;
import com.admin.entity.FasUser;
import com.admin.entity.Subscription;
import com.admin.repository.DeviceRepository;
import com.admin.repository.FasUserRepository;
import com.admin.repository.SubscriptionRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@Order(Ordered.LOWEST_PRECEDENCE - 10)
public class ClientAccessFilter
        extends OncePerRequestFilter {

    private final FasUserRepository userRepository;

    private final DeviceRepository deviceRepository;

    private final SubscriptionRepository subscriptionRepository;

    public ClientAccessFilter(
            FasUserRepository userRepository,
            DeviceRepository deviceRepository,
            SubscriptionRepository subscriptionRepository
    ) {
        this.userRepository =
                userRepository;

        this.deviceRepository =
                deviceRepository;

        this.subscriptionRepository =
                subscriptionRepository;
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

        // =================================================
        // Admin
        // =================================================

        if (path.startsWith(
                "/api/admin/"
        )) {

            return true;
        }

        // =================================================
        // Login
        // لا يحتاج Session
        // =================================================

        if (path.equals(
                "/api/auth/login"
        )) {

            return true;
        }

        // =================================================
        // Register Device
        // يعتمد على Setup Token
        // =================================================

        if (path.equals(
                "/api/auth/register-device"
        )) {

            return true;
        }

        // =================================================
        // Logout
        // يحتاج Session فقط
        // ولا يحتاج فحص الاشتراك
        // =================================================

        if (path.equals(
                "/api/auth/logout"
        )) {

            return true;
        }

        // =================================================
        // كل المسارات غير الخاصة بالعميل
        // =================================================

        return !(
                path.equals("/api/auth/session")

                        || path.equals("/api/subscription-requests")
                        || path.startsWith("/api/subscription-requests/")

                        || path.equals("/api/clients")
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
        // USER_ID
        // =================================================

        Object userIdAttribute =
                request.getAttribute(
                        ClientSessionFilter.USER_ID_ATTRIBUTE
                );

        if (!(userIdAttribute instanceof Number number)) {

            sendUnauthorized(
                    response,
                    "تعذر تحديد مستخدم الجلسة."
            );

            return;
        }

        Long userId =
                number.longValue();

        // =================================================
        // CLINIC_ID
        // =================================================

        Object clinicIdAttribute =
                request.getAttribute(
                        ClientSessionFilter.CLINIC_ID_ATTRIBUTE
                );

        if (!(clinicIdAttribute instanceof Number )) {

            sendUnauthorized(
                    response,
                    "تعذر تحديد عيادة الجلسة."
            );

            return;
        }

        Long clinicId =
                number.longValue();

        // =================================================
        // DEVICE_ID
        // =================================================

        Object deviceIdAttribute =
                request.getAttribute(
                        ClientSessionFilter.DEVICE_ID_ATTRIBUTE
                );

        if (!(deviceIdAttribute instanceof Number )) {

            sendUnauthorized(
                    response,
                    "تعذر تحديد جهاز الجلسة."
            );

            return;
        }

        Long deviceId =
                number.longValue();

        // =================================================
        // المستخدم
        // =================================================

        FasUser user =
                userRepository
                        .findById(userId)
                        .orElse(null);

        if (user == null) {

            sendUnauthorized(
                    response,
                    "المستخدم المرتبط بالجلسة غير موجود."
            );

            return;
        }

        // =================================================
        // حالة المستخدم
        // =================================================

        if (!"ACTIVE".equalsIgnoreCase(
                user.getStatus()
        )) {

            sendAccessDenied(
                    response,
                    "USER_INACTIVE",
                    "حساب المستخدم غير نشط."
            );

            return;
        }

        // =================================================
        // عيادة المستخدم
        // =================================================

        if (user.getClinicId() == null ||
                !clinicId.equals(
                        user.getClinicId()
                )) {

            sendAccessDenied(
                    response,
                    "CLINIC_MISMATCH",
                    "المستخدم غير مرتبط بالعيادة الحالية."
            );

            return;
        }

        // =================================================
        // الجهاز
        // =================================================

        Device device =
                deviceRepository
                        .findById(deviceId)
                        .orElse(null);

        if (device == null) {

            sendUnauthorized(
                    response,
                    "الجهاز المرتبط بالجلسة غير موجود."
            );

            return;
        }

        // =================================================
        // عيادة الجهاز
        // =================================================

        if (device.getClinicId() == null ||
                !clinicId.equals(
                        device.getClinicId()
                )) {

            sendAccessDenied(
                    response,
                    "DEVICE_CLINIC_MISMATCH",
                    "الجهاز غير مرتبط بالعيادة الحالية."
            );

            return;
        }

        // =================================================
        // حالة الجهاز
        // =================================================

        String deviceStatus =
                device.getStatus();

        if (!"ACTIVE".equalsIgnoreCase(
                deviceStatus
        )) {

            if ("BLOCKED".equalsIgnoreCase(
                    deviceStatus
            )) {

                sendAccessDenied(
                        response,
                        "DEVICE_BLOCKED",
                        "تم إيقاف هذا الجهاز من قبل مدير النظام."
                );

                return;
            }

            if ("REVOKED".equalsIgnoreCase(
                    deviceStatus
            )) {

                sendAccessDenied(
                        response,
                        "DEVICE_REVOKED",
                        "تم إلغاء تسجيل هذا الجهاز."
                );

                return;
            }

            sendAccessDenied(
                    response,
                    "DEVICE_INACTIVE",
                    "الجهاز غير نشط."
            );

            return;
        }

        // =================================================
        // طلب الاشتراك
        //
        // هذا المسار يسمح به حتى لو:
        // - انتهى الاشتراك
        // - لا يوجد اشتراك
        //
        // لأن الهدف منه طلب اشتراك جديد أو تجديد.
        // =================================================

        String path =
                request.getRequestURI();

        boolean subscriptionRequestPath =
                path.equals(
                        "/api/subscription-requests"
                )
                        || path.startsWith(
                        "/api/subscription-requests/"
                );

        if (subscriptionRequestPath) {

            device.setLastSeenAt(
                    LocalDateTime.now()
            );

            deviceRepository.save(
                    device
            );

            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }

        // =================================================
        // باقي مسارات العميل
        //
        // تحتاج اشتراكًا صالحًا
        // =================================================

        Subscription subscription =
                subscriptionRepository
                        .findFirstByClinic_ClinicIdAndStatusOrderByEndDateDesc(
                                clinicId,
                                "ACTIVE"
                        )
                        .orElse(null);

        if (subscription == null) {

            sendAccessDenied(
                    response,
                    "SUBSCRIPTION_INACTIVE",
                    "لا يوجد اشتراك نشط لهذه العيادة."
            );

            return;
        }

        // =================================================
        // تاريخ بداية الاشتراك
        // =================================================

        LocalDate today =
                LocalDate.now();

        if (subscription.getStartDate() != null &&
                today.isBefore(
                        subscription.getStartDate()
                )) {

            sendAccessDenied(
                    response,
                    "SUBSCRIPTION_NOT_STARTED",
                    "اشتراك العيادة لم يبدأ بعد."
            );

            return;
        }

        // =================================================
        // تاريخ نهاية الاشتراك
        // =================================================

        if (subscription.getEndDate() != null &&
                today.isAfter(
                        subscription.getEndDate()
                )) {

            sendAccessDenied(
                    response,
                    "SUBSCRIPTION_EXPIRED",
                    "انتهى اشتراك العيادة."
            );

            return;
        }

        // =================================================
        // آخر نشاط
        // =================================================

        device.setLastSeenAt(
                LocalDateTime.now()
        );

        deviceRepository.save(
                device
        );

        // =================================================
        // السماح
        // =================================================

        filterChain.doFilter(
                request,
                response
        );
    }

    // =====================================================
    // 401
    // =====================================================

    private void sendUnauthorized(
            HttpServletResponse response,
            String message
    ) throws IOException {

        sendError(
                response,
                HttpServletResponse.SC_UNAUTHORIZED,
                "UNAUTHORIZED",
                message
        );
    }

    // =====================================================
    // 403
    // =====================================================

    private void sendAccessDenied(
            HttpServletResponse response,
            String code,
            String message
    ) throws IOException {

        sendError(
                response,
                HttpServletResponse.SC_FORBIDDEN,
                code,
                message
        );
    }

    // =====================================================
    // JSON Error
    // =====================================================

    private void sendError(
            HttpServletResponse response,
            int status,
            String code,
            String message
    ) throws IOException {

        response.setStatus(
                status
        );

        response.setContentType(
                "application/json;charset=UTF-8"
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