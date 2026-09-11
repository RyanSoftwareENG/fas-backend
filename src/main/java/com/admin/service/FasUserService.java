package com.admin.service;

import com.admin.dto.FasUserCreateRequest;
import com.admin.dto.FasUserResponse;
import com.admin.dto.FasUserUpdateRequest;
import com.admin.entity.FasRole;
import com.admin.entity.FasUser;
import com.admin.entity.FasUserRole;
import com.admin.repository.ClinicRepository;
import com.admin.repository.FasRoleRepository;
import com.admin.repository.FasUserRepository;
import com.admin.repository.FasUserRoleRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FasUserService {

    private final FasUserRepository userRepository;
    private final ClinicRepository clinicRepository;
    private final AdminActivityLogService adminActivityLogService;
    private final FasUserRoleRepository userRoleRepository;
    private final FasRoleRepository roleRepository;

    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder(12);

    public FasUserService(
            FasUserRepository userRepository,
            ClinicRepository clinicRepository,
            AdminActivityLogService adminActivityLogService,
            FasUserRoleRepository userRoleRepository,
            FasRoleRepository roleRepository
    ) {
        this.userRepository = userRepository;
        this.clinicRepository = clinicRepository;
        this.adminActivityLogService = adminActivityLogService;
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;
    }

    // =========================================================
    // إنشاء مستخدم
    // =========================================================

    @Transactional
    public FasUserResponse createUser(
            FasUserCreateRequest request
    ) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "بيانات المستخدم مطلوبة"
            );
        }

        if (request.getClinicId() == null) {
            throw new IllegalArgumentException(
                    "العيادة مطلوبة"
            );
        }

        if (!clinicRepository.existsById(
                request.getClinicId()
        )) {
            throw new IllegalArgumentException(
                    "العيادة غير موجودة"
            );
        }

        if (request.getUsername() == null ||
                request.getUsername().isBlank()) {

            throw new IllegalArgumentException(
                    "اسم المستخدم مطلوب"
            );
        }

        if (request.getPassword() == null ||
                request.getPassword().isBlank()) {

            throw new IllegalArgumentException(
                    "كلمة المرور مطلوبة"
            );
        }

        if (request.getFullName() == null ||
                request.getFullName().isBlank()) {

            throw new IllegalArgumentException(
                    "الاسم الكامل مطلوب"
            );
        }

        String username =
                request.getUsername().trim();

        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException(
                    "اسم المستخدم مستخدم بالفعل"
            );
        }

        LocalDateTime now =
                LocalDateTime.now();

        FasUser user = new FasUser();

        user.setClinicId(
                request.getClinicId()
        );

        user.setUsername(username);

        user.setPasswordHash(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        user.setFullName(
                request.getFullName().trim()
        );

        user.setStatus("ACTIVE");
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        FasUser saved =
                userRepository.save(user);

        logAdminActivity(
                "CREATE",
                "FAS_USER",
                saved.getUserId(),
                "تم إنشاء مستخدم: "
                        + saved.getUsername()
        );

        return toResponse(saved);
    }

    // =========================================================
    // جميع المستخدمين
    // =========================================================

    @Transactional(readOnly = true)
    public List<FasUserResponse> getAllUsers() {

        return userRepository
                .findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // =========================================================
    // مستخدمو عيادة محددة
    // =========================================================

    @Transactional(readOnly = true)
    public List<FasUserResponse> getUsersByClinic(
            Long clinicId
    ) {

        if (clinicId == null) {
            throw new IllegalArgumentException(
                    "معرف العيادة مطلوب"
            );
        }

        if (!clinicRepository.existsById(clinicId)) {
            throw new IllegalArgumentException(
                    "العيادة غير موجودة"
            );
        }

        return userRepository
                .findByClinicId(clinicId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // =========================================================
    // مستخدم بواسطة ID
    // =========================================================

    @Transactional(readOnly = true)
    public FasUserResponse getUserById(
            Long userId
    ) {

        validateUserId(userId);

        FasUser user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "المستخدم غير موجود"
                                )
                        );

        return toResponse(user);
    }

    // =========================================================
    // تعديل المستخدم
    // =========================================================

    @Transactional
    public FasUserResponse updateUser(
            Long userId,
            FasUserUpdateRequest request
    ) {

        validateUserId(userId);

        if (request == null) {
            throw new IllegalArgumentException(
                    "بيانات التعديل مطلوبة"
            );
        }

        FasUser user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "المستخدم غير موجود"
                                )
                        );

        if (request.getFullName() != null &&
                !request.getFullName().isBlank()) {

            user.setFullName(
                    request.getFullName().trim()
            );
        }

        if (request.getPassword() != null &&
                !request.getPassword().isBlank()) {

            user.setPasswordHash(
                    passwordEncoder.encode(
                            request.getPassword()
                    )
            );
        }

        user.setUpdatedAt(
                LocalDateTime.now()
        );

        FasUser updated =
                userRepository.save(user);

        logAdminActivity(
                "UPDATE",
                "FAS_USER",
                updated.getUserId(),
                "تم تعديل مستخدم: "
                        + updated.getUsername()
        );

        return toResponse(updated);
    }

    // =========================================================
    // تعليق المستخدم
    // =========================================================

    @Transactional
    public FasUserResponse suspendUser(
            Long userId
    ) {

        validateUserId(userId);

        FasUser user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "المستخدم غير موجود"
                                )
                        );

        user.setStatus("SUSPENDED");
        user.setUpdatedAt(
                LocalDateTime.now()
        );

        FasUser updated =
                userRepository.save(user);

        logAdminActivity(
                "SUSPEND",
                "FAS_USER",
                updated.getUserId(),
                "تم إيقاف مستخدم: "
                        + updated.getUsername()
        );

        return toResponse(updated);
    }

    // =========================================================
    // إعادة تفعيل المستخدم
    // =========================================================

    @Transactional
    public FasUserResponse activateUser(
            Long userId
    ) {

        validateUserId(userId);

        FasUser user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "المستخدم غير موجود"
                                )
                        );

        user.setStatus("ACTIVE");
        user.setUpdatedAt(
                LocalDateTime.now()
        );

        FasUser updated =
                userRepository.save(user);

        logAdminActivity(
                "ACTIVATE",
                "FAS_USER",
                updated.getUserId(),
                "تم تفعيل مستخدم: "
                        + updated.getUsername()
        );

        return toResponse(updated);
    }

    // =========================================================
    // جلب أدوار المستخدم
    // =========================================================

    @Transactional(readOnly = true)
    public List<FasRole> getUserRoles(
            Long userId
    ) {

        validateUserId(userId);

        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException(
                    "المستخدم غير موجود"
            );
        }

        return userRoleRepository
                .findByUserIdWithRole(userId)
                .stream()
                .map(FasUserRole::getRole)
                .toList();
    }

    // =========================================================
    // تعيين دور للمستخدم
    // =========================================================

    @Transactional
    public void updateUserRole(
            Long userId,
            Long roleId
    ) {

        validateUserId(userId);

        if (roleId == null) {
            throw new IllegalArgumentException(
                    "الدور مطلوب"
            );
        }

        FasUser user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "المستخدم غير موجود"
                                )
                        );

        FasRole role =
                roleRepository
                        .findById(roleId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "الدور غير موجود"
                                )
                        );

        if (!"ACTIVE".equals(role.getStatus())) {
            throw new IllegalArgumentException(
                    "الدور غير فعال"
            );
        }

        /*
         * واجهة المستخدم الحالية تعتمد دورًا واحدًا.
         * لذلك يتم حذف الدور السابق ثم إضافة الدور الجديد.
         */
        userRoleRepository.deleteByUserId(
                userId
        );

        FasUserRole userRole =
                new FasUserRole(
                        user,
                        role
                );

        userRoleRepository.save(userRole);

        logAdminActivity(
                "UPDATE_ROLE",
                "FAS_USER",
                userId,
                "تم تعيين الدور: "
                        + role.getRoleName()
        );
    }

    // =========================================================
    // تحويل Entity إلى Response
    // =========================================================

    private FasUserResponse toResponse(
            FasUser user
    ) {

        return new FasUserResponse(
                user.getUserId(),
                user.getClinicId(),
                user.getUsername(),
                user.getFullName(),
                user.getStatus(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getLastLoginAt()
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

    // =========================================================
    // الحصول على Admin User الحالي
    // =========================================================

    private Long getCurrentAdminUserId() {

        ServletRequestAttributes attributes =
                (ServletRequestAttributes)
                        RequestContextHolder
                                .getRequestAttributes();

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
    // التحقق من User ID
    // =========================================================

    private void validateUserId(
            Long userId
    ) {

        if (userId == null) {
            throw new IllegalArgumentException(
                    "معرف المستخدم مطلوب"
            );
        }
    }
}