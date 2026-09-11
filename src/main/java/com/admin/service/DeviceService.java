package com.admin.service;

import com.admin.dto.DeviceRegistrationRequest;
import com.admin.dto.DeviceRegistrationResponse;
import com.admin.entity.Device;
import com.admin.entity.DeviceActivationCode;
import com.admin.entity.Subscription;
import com.admin.repository.DeviceActivationCodeRepository;
import com.admin.repository.DeviceRepository;
import com.admin.repository.SubscriptionRepository;
import com.admin.security.ActivationCodeHashUtil;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final DeviceActivationCodeRepository
            activationCodeRepository;
    private final SubscriptionRepository
            subscriptionRepository;

    public DeviceService(
            DeviceRepository deviceRepository,
            DeviceActivationCodeRepository activationCodeRepository,
            SubscriptionRepository subscriptionRepository
    ) {
        this.deviceRepository =
                deviceRepository;

        this.activationCodeRepository =
                activationCodeRepository;

        this.subscriptionRepository =
                subscriptionRepository;
    }

    @Transactional
    public DeviceRegistrationResponse register(
            DeviceRegistrationRequest request
    ) {

        if (request == null) {
            return failure(
                    "بيانات تسجيل الجهاز مطلوبة."
            );
        }

        if (isBlank(
                request.getActivationCode()
        )) {

            return failure(
                    "كود التفعيل مطلوب."
            );
        }

        if (isBlank(
                request.getInstallationId()
        )) {

            return failure(
                    "معرف التثبيت مطلوب."
            );
        }

        String installationId =
                request.getInstallationId()
                        .trim();

        String codeHash =
                ActivationCodeHashUtil.hash(
                        request
                                .getActivationCode()
                                .trim()
                                .toUpperCase()
                );

        DeviceActivationCode activationCode =
                activationCodeRepository
                        .findByCodeHashAndStatus(
                                codeHash,
                                "ACTIVE"
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "كود التفعيل غير صالح أو مستخدم مسبقًا."
                                )
                        );

        LocalDateTime now =
                LocalDateTime.now();

        if (activationCode.getExpiresAt() != null &&
                activationCode.getExpiresAt()
                        .isBefore(now)) {

            activationCode.setStatus(
                    "EXPIRED"
            );

            activationCodeRepository.save(
                    activationCode
            );

            throw new IllegalArgumentException(
                    "انتهت صلاحية كود التفعيل."
            );
        }

        Subscription subscription =
                subscriptionRepository
                        .findById(
                                activationCode
                                        .getSubscriptionId()
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "الاشتراك المرتبط بالكود غير موجود."
                                )
                        );

        if (!"ACTIVE".equalsIgnoreCase(
                subscription.getStatus()
        )) {

            throw new IllegalArgumentException(
                    "الاشتراك المرتبط بالجهاز غير نشط."
            );
        }

        if (!activationCode
                .getClinicId()
                .equals(
                        subscription.getClinic().getClinicId()
                )) {

            throw new IllegalStateException(
                    "بيانات التفعيل غير متطابقة."
            );
        }

        /*
         * التأكد من أن INSTALLATION_ID
         * غير مرتبط بعيادة أخرى.
         */
        Device existing =
                deviceRepository
                        .findByInstallationId(
                                installationId
                        )
                        .orElse(null);

        if (existing != null) {

            if (!existing.getClinicId()
                    .equals(
                            activationCode
                                    .getClinicId()
                    )) {

                throw new IllegalArgumentException(
                        "هذا الجهاز مرتبط بعيادة أخرى."
                );
            }

            if ("REVOKED".equals(
                    existing.getStatus()
            )) {

                throw new IllegalArgumentException(
                        "هذا الجهاز ملغى."
                );
            }

            existing.setLastSeenAt(now);

            Device saved =
                    deviceRepository.save(
                            existing
                    );

            return success(
                    "الجهاز مسجل مسبقًا.",
                    saved
            );
        }

        Device device =
                new Device();

        device.setClinicId(
                activationCode.getClinicId()
        );

        device.setDeviceName(
                clean(
                        request.getDeviceName()
                )
        );

        device.setInstallationId(
                installationId
        );

        device.setStatus(
                "ACTIVE"
        );

        device.setFirstSeenAt(now);

        device.setLastSeenAt(now);

        Device saved =
                deviceRepository.save(device);

        /*
         * الكود يصبح مستخدمًا بعد نجاح
         * تسجيل الجهاز.
         */
        activationCode.setStatus(
                "USED"
        );

        activationCode.setUsedAt(now);

        activationCode.setInstallationId(
                saved.getInstallationId()
        );

        activationCodeRepository.save(
                activationCode
        );

        return success(
                "تم تسجيل الجهاز بنجاح.",
                saved
        );
    }

    private DeviceRegistrationResponse success(
            String message,
            Device device
    ) {

        return new DeviceRegistrationResponse(
                true,
                message,
                device.getDeviceId(),
                device.getClinicId(),
                device.getDeviceName(),
                device.getInstallationId(),
                device.getStatus()
        );
    }

    private DeviceRegistrationResponse failure(
            String message
    ) {

        return new DeviceRegistrationResponse(
                false,
                message,
                null,
                null,
                null,
                null,
                null
        );
    }

    private boolean isBlank(
            String value
    ) {

        return value == null ||
                value.isBlank();
    }

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
