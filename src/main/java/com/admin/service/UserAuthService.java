package com.admin.service;

import com.admin.dto.DeviceRegistrationRequest;
import com.admin.dto.UserLoginRequest;
import com.admin.dto.UserLoginResponse;
import com.admin.entity.Device;
import com.admin.entity.DeviceActivationCode;
import com.admin.entity.FasUser;
import com.admin.entity.Subscription;
import com.admin.repository.DeviceActivationCodeRepository;
import com.admin.repository.DeviceRepository;
import com.admin.repository.FasUserRepository;
import com.admin.repository.SubscriptionRepository;
import com.admin.security.ActivationCodeHashUtil;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class UserAuthService {

    private final FasUserRepository userRepository;

    private final DeviceRepository deviceRepository;

    private final SubscriptionRepository subscriptionRepository;

    private final DeviceActivationCodeRepository
            activationCodeRepository;

    private final UserSessionService userSessionService;

    private final DeviceSetupTokenService
            deviceSetupTokenService;

    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    public UserAuthService(
            FasUserRepository userRepository,
            DeviceRepository deviceRepository,
            SubscriptionRepository subscriptionRepository,
            DeviceActivationCodeRepository activationCodeRepository,
            UserSessionService userSessionService,
            DeviceSetupTokenService deviceSetupTokenService
    ) {

        this.userRepository =
                userRepository;

        this.deviceRepository =
                deviceRepository;

        this.subscriptionRepository =
                subscriptionRepository;

        this.activationCodeRepository =
                activationCodeRepository;

        this.userSessionService =
                userSessionService;

        this.deviceSetupTokenService =
                deviceSetupTokenService;
    }

    // =====================================================
    // Login
    // =====================================================

    @Transactional
    public UserLoginResponse login(
            UserLoginRequest request
    ) {

        if (request == null) {

            return failure(
                    "بيانات تسجيل الدخول مطلوبة."
            );
        }

        String username =
                clean(
                        request.getUsername()
                );

        String password =
                request.getPassword();

        String installationId =
                clean(
                        request.getInstallationId()
                );

        // -------------------------------------------------
        // التحقق من البيانات
        // -------------------------------------------------

        if (username == null) {

            return failure(
                    "اسم المستخدم مطلوب."
            );
        }

        if (password == null ||
                password.isBlank()) {

            return failure(
                    "كلمة المرور مطلوبة."
            );
        }

        if (installationId == null) {

            return failure(
                    "معرف التثبيت مطلوب."
            );
        }

        // -------------------------------------------------
        // البحث عن المستخدم
        // -------------------------------------------------

        FasUser user =
                userRepository
                        .findByUsername(username)
                        .orElse(null);

        if (user == null) {

            return failure(
                    "اسم المستخدم أو كلمة المرور غير صحيحة."
            );
        }

        // -------------------------------------------------
        // حالة المستخدم
        // -------------------------------------------------

        if (!"ACTIVE".equalsIgnoreCase(
                user.getStatus()
        )) {

            return failure(
                    "حساب المستخدم غير نشط."
            );
        }

        // -------------------------------------------------
        // التحقق من كلمة المرور
        // -------------------------------------------------

        if (!passwordEncoder.matches(
                password,
                user.getPasswordHash()
        )) {

            return failure(
                    "اسم المستخدم أو كلمة المرور غير صحيحة."
            );
        }

        /*
         * firstLogin معلومة فقط.
         *
         * لا تستخدم لتحديد هل الجهاز يحتاج تسجيلًا.
         */
        boolean firstLogin =
                user.getLastLoginAt() == null;

        // -------------------------------------------------
        // البحث عن الجهاز
        // -------------------------------------------------

        Device device =
                deviceRepository
                        .findByInstallationId(
                                installationId
                        )
                        .orElse(null);

        // =================================================
        // الجهاز غير مسجل
        // =================================================

        if (device == null) {

            /*
             * إنشاء Setup Token مؤقت.
             *
             * تم التحقق من username/password قبل الوصول
             * إلى هذه النقطة.
             */
            String setupToken =
                    deviceSetupTokenService.create(
                            user.getUserId(),
                            user.getClinicId(),
                            installationId
                    );

            UserLoginResponse response =
                    success(
                            "تم التحقق من بيانات الحساب. "
                                    + "هذا الجهاز غير مسجل.",
                            null,
                            user,
                            null,
                            firstLogin,
                            true
                    );

            response.setSetupToken(
                    setupToken
            );

            return response;
        }

        // =================================================
        // الجهاز موجود
        // =================================================

        if (!"ACTIVE".equalsIgnoreCase(
                device.getStatus()
        )) {

            if ("BLOCKED".equalsIgnoreCase(
                    device.getStatus()
            )) {

                return failure(
                        "تم حظر هذا الجهاز."
                );
            }

            if ("REVOKED".equalsIgnoreCase(
                    device.getStatus()
            )) {

                return failure(
                        "تم إلغاء تسجيل هذا الجهاز."
                );
            }

            return failure(
                    "حالة الجهاز غير صالحة."
            );
        }

        // -------------------------------------------------
        // التحقق من العيادة
        // -------------------------------------------------

        if (device.getClinicId() == null ||
                user.getClinicId() == null) {

            return failure(
                    "بيانات ارتباط المستخدم والعيادة غير مكتملة."
            );
        }

        if (!device.getClinicId()
                .equals(
                        user.getClinicId()
                )) {

            return failure(
                    "هذا الجهاز لا يتبع عيادة المستخدم."
            );
        }

        // -------------------------------------------------
        // الاشتراك
        // -------------------------------------------------

        Subscription subscription =
                findActiveSubscription(
                        user.getClinicId()
                );

        if (subscription == null) {

            return failure(
                    "لا يوجد اشتراك نشط لهذه العيادة."
            );
        }

        String subscriptionError =
                validateSubscriptionDates(
                        subscription
                );

        if (subscriptionError != null) {

            return failure(
                    subscriptionError
            );
        }

        // -------------------------------------------------
        // تحديث الجهاز
        // -------------------------------------------------

        LocalDateTime now =
                LocalDateTime.now();

        device.setLastSeenAt(
                now
        );

        deviceRepository.save(
                device
        );

        // -------------------------------------------------
        // إنشاء جلسة حقيقية
        // -------------------------------------------------

        UserSessionService.SessionCreationResult
                sessionResult =
                userSessionService.createSession(
                        user.getUserId(),
                        user.getClinicId(),
                        device.getDeviceId()
                );

        // -------------------------------------------------
        // تحديث آخر تسجيل دخول
        // -------------------------------------------------

        user.setLastLoginAt(
                now
        );

        user.setUpdatedAt(
                now
        );

        userRepository.save(
                user
        );

        // -------------------------------------------------
        // النتيجة
        // -------------------------------------------------

        return success(
                "تم تسجيل الدخول بنجاح.",
                sessionResult.token(),
                user,
                device,
                firstLogin,
                false
        );
    }

    // =====================================================
    // تسجيل جهاز جديد
    // =====================================================

    @Transactional
    public UserLoginResponse registerDevice(
            String setupToken,
            DeviceRegistrationRequest request
    ) {

        // -------------------------------------------------
        // التحقق من Setup Token
        // -------------------------------------------------

        if (setupToken == null ||
                setupToken.isBlank()) {

            return failure(
                    "رمز تهيئة الجهاز غير موجود أو انتهت صلاحيته."
            );
        }

        DeviceSetupTokenService.PendingSetup pendingSetup =
                deviceSetupTokenService.get(
                        setupToken
                );

        if (pendingSetup == null) {

            return failure(
                    "رمز تهيئة الجهاز غير صالح أو انتهت صلاحيته."
            );
        }

        // -------------------------------------------------
        // التحقق من الطلب
        // -------------------------------------------------

        if (request == null) {

            return failure(
                    "بيانات تسجيل الجهاز مطلوبة."
            );
        }

        String activationCode =
                clean(
                        request.getActivationCode()
                );

        String installationId =
                clean(
                        request.getInstallationId()
                );

        String deviceName =
                clean(
                        request.getDeviceName()
                );

        if (activationCode == null) {

            return failure(
                    "كود التفعيل مطلوب."
            );
        }

        if (installationId == null) {

            return failure(
                    "معرف التثبيت مطلوب."
            );
        }

        // -------------------------------------------------
        // التأكد من أن الجهاز هو نفسه
        // الذي تم تسجيل الدخول منه
        // -------------------------------------------------

        if (!pendingSetup
                .installationId()
                .equals(
                        installationId
                )) {

            return failure(
                    "معرف الجهاز لا يطابق عملية تسجيل الدخول."
            );
        }

        // -------------------------------------------------
        // استرجاع المستخدم من السيرفر
        // -------------------------------------------------

        FasUser user =
                userRepository
                        .findById(
                                pendingSetup.userId()
                        )
                        .orElse(null);

        if (user == null) {

            deviceSetupTokenService.invalidate(
                    setupToken
            );

            return failure(
                    "المستخدم غير موجود."
            );
        }

        // -------------------------------------------------
        // حالة المستخدم
        // -------------------------------------------------

        if (!"ACTIVE".equalsIgnoreCase(
                user.getStatus()
        )) {

            deviceSetupTokenService.invalidate(
                    setupToken
            );

            return failure(
                    "حساب المستخدم غير نشط."
            );
        }

        // -------------------------------------------------
        // التحقق من العيادة
        // -------------------------------------------------

        if (user.getClinicId() == null) {

            return failure(
                    "المستخدم غير مرتبط بعيادة."
            );
        }

        if (!user.getClinicId()
                .equals(
                        pendingSetup.clinicId()
                )) {

            return failure(
                    "بيانات العيادة في عملية التهيئة غير صحيحة."
            );
        }

        // -------------------------------------------------
        // التأكد أن الجهاز غير مسجل
        // -------------------------------------------------

        if (deviceRepository
                .findByInstallationId(
                        installationId
                )
                .isPresent()) {

            return failure(
                    "هذا الجهاز مسجل بالفعل في النظام."
            );
        }

        // -------------------------------------------------
        // Hash لكود التفعيل
        // -------------------------------------------------

        String codeHash =
                ActivationCodeHashUtil.hash(
                        activationCode
                );

        // -------------------------------------------------
        // البحث عن Activation Code
        // -------------------------------------------------

        DeviceActivationCode activation =
                activationCodeRepository
                        .findByCodeHashAndStatus(
                                codeHash,
                                "ACTIVE"
                        )
                        .orElse(null);

        if (activation == null) {

            return failure(
                    "كود التفعيل غير صحيح أو غير صالح."
            );
        }

        LocalDateTime now =
                LocalDateTime.now();

        // -------------------------------------------------
        // التأكد أن الكود لم يتم استخدامه بطريقة غير صحيحة
        // -------------------------------------------------

        if (activation.getUsedAt() != null ||
                "USED".equalsIgnoreCase(
                        activation.getStatus()
                )) {

            return failure(
                    "كود التفعيل مستخدم مسبقًا."
            );
        }

        // -------------------------------------------------
        // انتهاء صلاحية الكود
        // -------------------------------------------------

        if (activation.getExpiresAt() != null &&
                !activation.getExpiresAt()
                        .isAfter(now)) {

            return failure(
                    "انتهت صلاحية كود التفعيل."
            );
        }

        // -------------------------------------------------
        // الاشتراك
        // -------------------------------------------------

        Subscription subscription =
                subscriptionRepository
                        .findById(
                                activation.getSubscriptionId()
                        )
                        .orElse(null);

        if (subscription == null) {

            return failure(
                    "الاشتراك المرتبط بكود التفعيل غير موجود."
            );
        }

        String subscriptionError =
                validateSubscriptionDates(
                        subscription
                );

        if (subscriptionError != null) {

            return failure(
                    subscriptionError
            );
        }

        // -------------------------------------------------
        // التحقق من بيانات العيادة للكود
        // -------------------------------------------------

        if (activation.getClinicId() == null ||
                subscription.getClinic() == null ||
                subscription.getClinic().getClinicId() == null) {

            return failure(
                    "بيانات العيادة المرتبطة بكود التفعيل غير مكتملة."
            );
        }

        Long subscriptionClinicId =
                subscription
                        .getClinic()
                        .getClinicId();

        // -------------------------------------------------
        // الكود يجب أن يتبع الاشتراك نفسه
        // -------------------------------------------------

        if (!activation.getClinicId()
                .equals(
                        subscriptionClinicId
                )) {

            return failure(
                    "كود التفعيل مرتبط بعيادة غير صالحة."
            );
        }

        // -------------------------------------------------
        // الكود يجب أن يتبع عيادة المستخدم
        // -------------------------------------------------

        if (!activation.getClinicId()
                .equals(
                        user.getClinicId()
                )) {

            return failure(
                    "كود التفعيل لا يتبع عيادة المستخدم."
            );
        }

        // =================================================
        // إنشاء الجهاز
        // =================================================

        Device device =
                new Device();

        device.setClinicId(
                user.getClinicId()
        );

        device.setInstallationId(
                installationId
        );

        device.setDeviceName(
                deviceName != null
                        ? deviceName
                        : "FAS Device"
        );

        device.setStatus(
                "ACTIVE"
        );

        device.setFirstSeenAt(
                now
        );

        device.setLastSeenAt(
                now
        );

        Device savedDevice =
                deviceRepository.save(
                        device
                );

        // =================================================
        // استهلاك Activation Code
        // =================================================

        activation.setStatus(
                "USED"
        );

        activation.setUsedAt(
                now
        );

        activation.setInstallationId(
                installationId
        );

        activationCodeRepository.save(
                activation
        );

        // =================================================
        // إنشاء الجلسة الحقيقية
        // =================================================

        UserSessionService.SessionCreationResult
                sessionResult =
                userSessionService.createSession(
                        user.getUserId(),
                        user.getClinicId(),
                        savedDevice.getDeviceId()
                );

        // =================================================
        // تحديث المستخدم
        // =================================================

        boolean firstLogin =
                user.getLastLoginAt() == null;

        user.setLastLoginAt(
                now
        );

        user.setUpdatedAt(
                now
        );

        userRepository.save(
                user
        );

        // =================================================
        // إلغاء Setup Token
        // =================================================

        deviceSetupTokenService.invalidate(
                setupToken
        );

        // =================================================
        // النتيجة النهائية
        // =================================================

        return success(
                "تم تسجيل الجهاز بنجاح.",
                sessionResult.token(),
                user,
                savedDevice,
                firstLogin,
                false
        );
    }

    // =====================================================
    // البحث عن الاشتراك النشط
    // =====================================================

    private Subscription findActiveSubscription(
            Long clinicId
    ) {

        if (clinicId == null) {
            return null;
        }

        return subscriptionRepository
                .findFirstByClinic_ClinicIdAndStatusOrderByEndDateDesc(
                        clinicId,
                        "ACTIVE"
                )
                .orElse(null);
    }

    // =====================================================
    // التحقق من تواريخ الاشتراك
    // =====================================================

    private String validateSubscriptionDates(
            Subscription subscription
    ) {

        if (subscription == null) {

            return "لا يوجد اشتراك للعيادة.";
        }

        LocalDate today =
                LocalDate.now();

        if (subscription.getStartDate() != null &&
                today.isBefore(
                        subscription.getStartDate()
                )) {

            return "اشتراك العيادة لم يبدأ بعد.";
        }

        if (subscription.getEndDate() != null &&
                today.isAfter(
                        subscription.getEndDate()
                )) {

            return "انتهى اشتراك العيادة.";
        }

        return null;
    }

    // =====================================================
    // Success
    // =====================================================

    private UserLoginResponse success(
            String message,
            String token,
            FasUser user,
            Device device,
            boolean firstLogin,
            boolean needsSetup
    ) {

        return new UserLoginResponse(
                true,
                message,
                token,
                user.getUserId(),
                user.getClinicId(),
                device != null
                        ? device.getDeviceId()
                        : null,
                user.getUsername(),
                user.getFullName(),
                null,
                firstLogin,
                needsSetup
        );
    }

    // =====================================================
    // Failure
    // =====================================================

    private UserLoginResponse failure(
            String message
    ) {

        return new UserLoginResponse(
                false,
                message,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                false,
                false
        );
    }

    // =====================================================
    // تنظيف النص
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
}