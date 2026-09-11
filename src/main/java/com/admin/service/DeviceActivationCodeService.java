package com.admin.service;

import com.admin.dto.DeviceActivationCodeResponse;
import com.admin.entity.DeviceActivationCode;
import com.admin.entity.Subscription;
import com.admin.repository.DeviceActivationCodeRepository;
import com.admin.repository.SubscriptionRepository;
import com.admin.security.ActivationCodeHashUtil;
import com.admin.security.DeviceActivationCodeUtil;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class DeviceActivationCodeService {

    private final DeviceActivationCodeRepository
            codeRepository;

    private final SubscriptionRepository
            subscriptionRepository;

    public DeviceActivationCodeService(
            DeviceActivationCodeRepository codeRepository,
            SubscriptionRepository subscriptionRepository
    ) {
        this.codeRepository =
                codeRepository;

        this.subscriptionRepository =
                subscriptionRepository;
    }

// =========================================================
// إصدار كود تفعيل لجهاز
// =========================================================

    @Transactional
    public DeviceActivationCodeResponse generate(
            Long subscriptionId
    ) {

        if (subscriptionId == null) {

            throw new IllegalArgumentException(
                    "معرف الاشتراك مطلوب"
            );
        }

        Subscription subscription =
                subscriptionRepository
                        .findById(subscriptionId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "الاشتراك غير موجود"
                                )
                        );

        // -----------------------------------------------------
        // التحقق من حالة الاشتراك
        // -----------------------------------------------------

        if (!"ACTIVE".equalsIgnoreCase(
                subscription.getStatus()
        )) {

            throw new IllegalArgumentException(
                    "لا يمكن إصدار كود لجهاز من اشتراك غير نشط"
            );
        }

        // -----------------------------------------------------
        // الحصول على العيادة من علاقة Subscription
        // -----------------------------------------------------

        if (subscription.getClinic() == null ||
                subscription.getClinic().getClinicId() == null) {

            throw new IllegalStateException(
                    "الاشتراك لا يحتوي على عيادة مرتبطة"
            );
        }

        Long clinicId =
                subscription
                        .getClinic()
                        .getClinicId();

        // -----------------------------------------------------
        // إنشاء كود عشوائي فريد
        // -----------------------------------------------------

        String code;
        String hash;

        do {

            code =
                    DeviceActivationCodeUtil.generate();

            hash =
                    ActivationCodeHashUtil.hash(
                            code
                    );

        } while (
                codeRepository
                        .findByCodeHashAndStatus(
                                hash,
                                "ACTIVE"
                        )
                        .isPresent()
        );

        // -----------------------------------------------------
        // إنشاء سجل كود التفعيل
        // -----------------------------------------------------

        LocalDateTime now =
                LocalDateTime.now();

        DeviceActivationCode entity =
                new DeviceActivationCode();

        entity.setSubscriptionId(
                subscriptionId
        );

        entity.setClinicId(
                clinicId
        );

        entity.setCodeHash(
                hash
        );

        entity.setStatus(
                "ACTIVE"
        );

        entity.setCreatedAt(
                now
        );

        // -----------------------------------------------------
        // انتهاء الكود مع انتهاء الاشتراك
        // -----------------------------------------------------

        if (subscription.getEndDate() != null) {

            entity.setExpiresAt(
                    subscription
                            .getEndDate()
                            .atTime(
                                    23,
                                    59,
                                    59
                            )
            );
        }

        DeviceActivationCode saved =
                codeRepository.save(
                        entity
                );

        // -----------------------------------------------------
        // إعادة الكود الحقيقي للـ Admin
        // -----------------------------------------------------

        return new DeviceActivationCodeResponse(
                saved.getActivationCodeId(),
                saved.getSubscriptionId(),
                saved.getClinicId(),
                code,
                saved.getStatus(),
                saved.getCreatedAt(),
                saved.getExpiresAt()
        );
    }
}
