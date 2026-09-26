package com.admin.service;

import com.admin.dto.DeviceActivationCodeResponse;
import com.admin.entity.Clinic;
import com.admin.entity.DeviceActivationCode;
import com.admin.repository.ClinicRepository;
import com.admin.repository.DeviceActivationCodeRepository;
import com.admin.security.ActivationCodeHashUtil;
import com.admin.security.DeviceActivationCodeUtil;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class DeviceActivationCodeService {

    private final DeviceActivationCodeRepository codeRepository;

    private final ClinicRepository clinicRepository;

    // =====================================================
    // مدة صلاحية كود التفعيل
    // =====================================================

    private static final long ACTIVATION_CODE_VALIDITY_HOURS = 24;

    // =====================================================
    // Constructor
    // =====================================================

    public DeviceActivationCodeService(
            DeviceActivationCodeRepository codeRepository,
            ClinicRepository clinicRepository
    ) {

        this.codeRepository =
                codeRepository;

        this.clinicRepository =
                clinicRepository;
    }

    // =========================================================
    // إصدار كود تفعيل لجهاز تابع لعيادة
    // =========================================================

    @Transactional
    public DeviceActivationCodeResponse generate(
            Long clinicId
    ) {

        // -----------------------------------------------------
        // التحقق من Clinic ID
        // -----------------------------------------------------

        if (clinicId == null ||
                clinicId <= 0) {

            throw new IllegalArgumentException(
                    "معرف العيادة غير صالح."
            );
        }

        // -----------------------------------------------------
        // البحث عن العيادة
        // -----------------------------------------------------

        Clinic clinic =
                clinicRepository
                        .findById(clinicId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "العيادة غير موجودة."
                                )
                        );

        // -----------------------------------------------------
        // يمكن هنا التحقق من حالة العيادة
        // إذا كان كيان Clinic لديك يحتوي status
        // -----------------------------------------------------

        /*
        مثال:

        if (!"ACTIVE".equalsIgnoreCase(
                clinic.getStatus()
        )) {
            throw new IllegalArgumentException(
                    "العيادة غير نشطة."
            );
        }
        */

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
        // الوقت
        // -----------------------------------------------------

        LocalDateTime now =
                LocalDateTime.now();

        LocalDateTime expiresAt =
                now.plusHours(
                        ACTIVATION_CODE_VALIDITY_HOURS
                );

        // -----------------------------------------------------
        // إنشاء سجل الكود
        // -----------------------------------------------------

        DeviceActivationCode entity =
                new DeviceActivationCode();

        /*
         * لا يوجد Subscription هنا.
         *
         * يبقى null.
         */
        entity.setSubscriptionId(
                null
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

        entity.setExpiresAt(
                expiresAt
        );

        // -----------------------------------------------------
        // الحفظ
        // -----------------------------------------------------

        DeviceActivationCode saved =
                codeRepository.save(
                        entity
                );

        // -----------------------------------------------------
        // إعادة الكود الحقيقي إلى Admin
        // -----------------------------------------------------

        return new DeviceActivationCodeResponse(
                saved.getActivationCodeId(),
                saved.getClinicId(),
                code,
                saved.getStatus(),
                saved.getCreatedAt(),
                saved.getExpiresAt()
        );
    }
}