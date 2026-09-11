package com.admin.service;

import com.admin.dto.ClinicCreateRequest;
import com.admin.dto.ClinicResponse;
import com.admin.dto.ClinicUpdateRequest;
import com.admin.entity.Clinic;
import com.admin.entity.ClinicDatabase;
import com.admin.repository.ClinicDatabaseRepository;
import com.admin.repository.ClinicRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClinicService {

    private final ClinicRepository clinicRepository;
    private final AdminActivityLogService adminActivityLogService;
    private final ClinicDatabaseRepository clinicDatabaseRepository;
    private final ClinicSchemaProvisioningService clinicSchemaProvisioningService;

    public ClinicService(
            ClinicRepository clinicRepository,
            AdminActivityLogService adminActivityLogService,
            ClinicDatabaseRepository clinicDatabaseRepository,
            ClinicSchemaProvisioningService clinicSchemaProvisioningService
    ) {
        this.clinicRepository = clinicRepository;
        this.adminActivityLogService = adminActivityLogService;
        this.clinicDatabaseRepository = clinicDatabaseRepository;
        this.clinicSchemaProvisioningService =
                clinicSchemaProvisioningService;
    }
    // =========================================================
    // إنشاء عيادة
    // =========================================================

    @Transactional
    public ClinicResponse createClinic(
            ClinicCreateRequest request
    ) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "بيانات العيادة مطلوبة"
            );
        }

        if (request.getClinicCode() == null
                || request.getClinicCode().isBlank()) {

            throw new IllegalArgumentException(
                    "رمز العيادة مطلوب"
            );
        }

        if (request.getClinicName() == null
                || request.getClinicName().isBlank()) {

            throw new IllegalArgumentException(
                    "اسم العيادة مطلوب"
            );
        }

        String clinicCode =
                request.getClinicCode().trim();

        if (clinicRepository.existsByClinicCode(
                clinicCode
        )) {

            throw new IllegalArgumentException(
                    "رمز العيادة مستخدم بالفعل"
            );
        }

        LocalDateTime now =
                LocalDateTime.now();

        Clinic clinic = new Clinic();

        clinic.setClinicCode(clinicCode);

        clinic.setClinicName(
                request.getClinicName().trim()
        );

        clinic.setOwnerName(
                clean(request.getOwnerName())
        );

        clinic.setPhone(
                clean(request.getPhone())
        );

        clinic.setEmail(
                clean(request.getEmail())
        );

        clinic.setAddress(
                clean(request.getAddress())
        );

        clinic.setStatus("ACTIVE");

        clinic.setCreatedAt(now);
        clinic.setUpdatedAt(now);

        Clinic saved =
                clinicRepository.saveAndFlush(clinic);
        String schemaName =
                "FAS_CLINIC_" + saved.getClinicId();

        try {

            clinicSchemaProvisioningService
                    .provisionClinicSchema(
                            saved.getClinicId()
                    );

            LocalDateTime databaseNow =
                    LocalDateTime.now();

            ClinicDatabase clinicDatabase =
                    new ClinicDatabase();

            clinicDatabase.setClinic(saved);

            clinicDatabase.setDatabaseName(
                    "FAS"
            );

            clinicDatabase.setHost(
                    "localhost"
            );

            clinicDatabase.setPort(
                    1521
            );

            clinicDatabase.setServiceName(
                    "XEPDB1"
            );

            clinicDatabase.setSchemaName(
                    schemaName
            );

            clinicDatabase.setStatus(
                    "ACTIVE"
            );

            clinicDatabase.setCreatedAt(
                    databaseNow
            );

            clinicDatabase.setUpdatedAt(
                    databaseNow
            );

            clinicDatabaseRepository.save(
                    clinicDatabase
            );

        } catch (RuntimeException e) {

            throw new IllegalStateException(
                    "تم إنشاء العيادة لكن فشل تجهيز قاعدة بياناتها",
                    e
            );
        }
        logAdminActivity(
                "CREATE",
                "CLINIC",
                saved.getClinicId(),
                "تم إنشاء العيادة: "
                        + saved.getClinicName()
        );

        return toResponse(saved);
    }

    // =========================================================
    // جلب جميع العيادات
    // =========================================================

    @Transactional(readOnly = true)
    public List<ClinicResponse> getAllClinics() {

        return clinicRepository
                .findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // =========================================================
    // جلب عيادة بواسطة ID
    // =========================================================

    @Transactional(readOnly = true)
    public ClinicResponse getClinicById(
            Long clinicId
    ) {

        Clinic clinic =
                clinicRepository
                        .findById(clinicId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "العيادة غير موجودة"
                                )
                        );

        return toResponse(clinic);
    }

    // =========================================================
    // تعديل بيانات العيادة
    // =========================================================

    @Transactional
    public ClinicResponse updateClinic(
            Long clinicId,
            ClinicUpdateRequest request
    ) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "بيانات التعديل مطلوبة"
            );
        }

        Clinic clinic =
                clinicRepository
                        .findById(clinicId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "العيادة غير موجودة"
                                )
                        );

        if (request.getClinicName() == null
                || request.getClinicName().isBlank()) {

            throw new IllegalArgumentException(
                    "اسم العيادة مطلوب"
            );
        }

        clinic.setClinicName(
                request.getClinicName().trim()
        );

        clinic.setOwnerName(
                clean(request.getOwnerName())
        );

        clinic.setPhone(
                clean(request.getPhone())
        );

        clinic.setEmail(
                clean(request.getEmail())
        );

        clinic.setAddress(
                clean(request.getAddress())
        );

        clinic.setUpdatedAt(
                LocalDateTime.now()
        );

        Clinic updated =
                clinicRepository.save(clinic);

        logAdminActivity(
                "UPDATE",
                "CLINIC",
                updated.getClinicId(),
                "تم تعديل بيانات العيادة: "
                        + updated.getClinicName()
        );

        return toResponse(updated);
    }

    // =========================================================
    // تعطيل العيادة
    // =========================================================

    @Transactional
    public ClinicResponse suspendClinic(
            Long clinicId
    ) {

        Clinic clinic =
                clinicRepository
                        .findById(clinicId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "العيادة غير موجودة"
                                )
                        );

        clinic.setStatus("SUSPENDED");

        clinic.setUpdatedAt(
                LocalDateTime.now()
        );

        Clinic updated =
                clinicRepository.save(clinic);

        logAdminActivity(
                "SUSPEND",
                "CLINIC",
                updated.getClinicId(),
                "تم إيقاف العيادة: "
                        + updated.getClinicName()
        );

        return toResponse(updated);
    }

    // =========================================================
    // إعادة تفعيل العيادة
    // =========================================================

    @Transactional
    public ClinicResponse activateClinic(
            Long clinicId
    ) {

        Clinic clinic =
                clinicRepository
                        .findById(clinicId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "العيادة غير موجودة"
                                )
                        );

        clinic.setStatus("ACTIVE");

        clinic.setUpdatedAt(
                LocalDateTime.now()
        );

        Clinic updated =
                clinicRepository.save(clinic);

        logAdminActivity(
                "ACTIVATE",
                "CLINIC",
                updated.getClinicId(),
                "تم تفعيل العيادة: "
                        + updated.getClinicName()
        );

        return toResponse(updated);
    }

    // =========================================================
    // تحويل Entity -> DTO
    // =========================================================

    private ClinicResponse toResponse(
            Clinic clinic
    ) {

        return new ClinicResponse(
                clinic.getClinicId(),
                clinic.getClinicCode(),
                clinic.getClinicName(),
                clinic.getOwnerName(),
                clinic.getPhone(),
                clinic.getEmail(),
                clinic.getAddress(),
                clinic.getStatus(),
                clinic.getCreatedAt(),
                clinic.getUpdatedAt()
        );
    }

    // =========================================================
    // تسجيل نشاط Admin
    // =========================================================

    private void logAdminActivity(
            String action,
            String entityName,
            Long entityId,
            String details
    ) {

        Long adminUserId =
                getCurrentAdminUserId();

        adminActivityLogService.log(
                adminUserId,
                action,
                entityName,
                entityId,
                details
        );
    }

    private Long getCurrentAdminUserId() {

        ServletRequestAttributes attributes =
                (ServletRequestAttributes)
                        RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            return null;
        }

        HttpServletRequest request =
                attributes.getRequest();

        Object value =
                request.getAttribute(
                        "ADMIN_USER_ID"
                );

        return value instanceof Long
                ? (Long) value
                : null;
    }

    // =========================================================
    // تنظيف القيم الاختيارية
    // =========================================================

    private String clean(String value) {

        if (value == null) {
            return null;
        }

        String trimmed =
                value.trim();

        return trimmed.isEmpty()
                ? null
                : trimmed;
    }
}