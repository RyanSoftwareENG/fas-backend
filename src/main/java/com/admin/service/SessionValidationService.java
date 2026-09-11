package com.admin.service;

import com.admin.dto.SessionValidationResponse;
import com.admin.entity.Device;
import com.admin.entity.FasUser;
import com.admin.entity.Subscription;
import com.admin.entity.UserSession;
import com.admin.repository.DeviceRepository;
import com.admin.repository.FasUserRepository;
import com.admin.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class SessionValidationService {

    private final UserSessionService userSessionService;

    private final FasUserRepository userRepository;

    private final DeviceRepository deviceRepository;

    private final SubscriptionRepository subscriptionRepository;

    public SessionValidationService(
            UserSessionService userSessionService,
            FasUserRepository userRepository,
            DeviceRepository deviceRepository,
            SubscriptionRepository subscriptionRepository
    ) {
        this.userSessionService =
                userSessionService;

        this.userRepository =
                userRepository;

        this.deviceRepository =
                deviceRepository;

        this.subscriptionRepository =
                subscriptionRepository;
    }

// =====================================================
// التحقق من جلسة المستخدم
// =====================================================

    @Transactional
    public SessionValidationResponse validate(
            String token
    ) {

        if (token == null ||
                token.isBlank()) {

            return invalid(
                    "رمز الجلسة غير موجود."
            );
        }

        UserSession session =
                userSessionService.validateToken(
                        token
                );

        if (session == null) {

            return invalid(
                    "جلسة المستخدم غير صالحة أو منتهية."
            );
        }

        // -------------------------------------------------
        // 1. المستخدم
        // -------------------------------------------------

        FasUser user =
                userRepository
                        .findById(
                                session.getUserId()
                        )
                        .orElse(null);

        if (user == null) {

            return revokeAndInvalidate(
                    session,
                    "المستخدم المرتبط بالجلسة غير موجود."
            );
        }

        if (!"ACTIVE".equalsIgnoreCase(
                user.getStatus()
        )) {

            return revokeAndInvalidate(
                    session,
                    "حساب المستخدم غير نشط."
            );
        }

        // -------------------------------------------------
        // 2. الجهاز
        // -------------------------------------------------

        if (session.getDeviceId() == null) {

            return revokeAndInvalidate(
                    session,
                    "الجلسة غير مرتبطة بجهاز."
            );
        }

        Device device =
                deviceRepository
                        .findById(
                                session.getDeviceId()
                        )
                        .orElse(null);

        if (device == null) {

            return revokeAndInvalidate(
                    session,
                    "الجهاز المرتبط بالجلسة غير موجود."
            );
        }

        if (!"ACTIVE".equalsIgnoreCase(
                device.getStatus()
        )) {

            return revokeAndInvalidate(
                    session,
                    "الجهاز غير مسموح له بالعمل."
            );
        }

        // -------------------------------------------------
        // 3. تطابق العيادة
        // -------------------------------------------------

        if (user.getClinicId() == null ||
                session.getClinicId() == null ||
                device.getClinicId() == null) {

            return revokeAndInvalidate(
                    session,
                    "بيانات العيادة غير مكتملة."
            );
        }

        if (!user.getClinicId()
                .equals(session.getClinicId()) ||

                !device.getClinicId()
                        .equals(session.getClinicId())) {

            return revokeAndInvalidate(
                    session,
                    "بيانات ارتباط الجلسة غير متطابقة."
            );
        }

        // -------------------------------------------------
        // 4. اشتراك العيادة
        // -------------------------------------------------

        Subscription subscription =
                subscriptionRepository
                        .findFirstByClinic_ClinicIdAndStatusOrderByEndDateDesc(
                                session.getClinicId(),
                                "ACTIVE"
                        )
                        .orElse(null);

        if (subscription == null) {

            return revokeAndInvalidate(
                    session,
                    "لا يوجد اشتراك نشط للعيادة."
            );
        }

        LocalDate today =
                LocalDate.now();

        if (today.isBefore(
                subscription.getStartDate()
        )) {

            return revokeAndInvalidate(
                    session,
                    "اشتراك العيادة لم يبدأ بعد."
            );
        }

        if (today.isAfter(
                subscription.getEndDate()
        )) {

            return revokeAndInvalidate(
                    session,
                    "انتهى اشتراك العيادة."
            );
        }

        // -------------------------------------------------
        // 5. تحديث النشاط
        // -------------------------------------------------

        LocalDateTime now =
                LocalDateTime.now();

        device.setLastSeenAt(
                now
        );

        deviceRepository.save(
                device
        );

        return valid(
                "الجلسة صالحة.",
                user,
                device
        );
    }

// =====================================================
// جلسة صالحة
// =====================================================

    private SessionValidationResponse valid(
            String message,
            FasUser user,
            Device device
    ) {

        return new SessionValidationResponse(
                true,
                message,
                user.getUserId(),
                user.getClinicId(),
                device.getDeviceId(),
                user.getUsername(),
                user.getFullName(),
                null
        );
    }

// =====================================================
// جلسة غير صالحة
// =====================================================

    private SessionValidationResponse invalid(
            String message
    ) {

        return new SessionValidationResponse(
                false,
                message,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

// =====================================================
// إلغاء الجلسة وإرجاع فشل
// =====================================================

    private SessionValidationResponse revokeAndInvalidate(
            UserSession session,
            String message
    ) {

        session.setRevoked(
                1
        );

        session.setLastActivityAt(
                LocalDateTime.now()
        );

        /*
         * validateToken() يعيد الـEntity
         * ضمن Transaction الحالية، لذلك
         * تحديث الكيان سيُحفظ بواسطة JPA.
         */

        return invalid(
                message
        );
    }
}
