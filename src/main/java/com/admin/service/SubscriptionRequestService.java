package com.admin.service;

import com.admin.entity.Clinic;
import com.admin.entity.Subscription;
import com.admin.entity.SubscriptionActivation;
import com.admin.entity.SubscriptionPlan;
import com.admin.entity.SubscriptionRequest;
import com.admin.repository.ClinicRepository;
import com.admin.repository.SubscriptionActivationRepository;
import com.admin.repository.SubscriptionPlanRepository;
import com.admin.repository.SubscriptionRepository;
import com.admin.repository.SubscriptionRequestRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SubscriptionRequestService {

    private final SubscriptionRequestRepository requestRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanRepository planRepository;
    private final SubscriptionActivationRepository activationRepository;
    private final ClinicRepository clinicRepository;

    public SubscriptionRequestService(
            SubscriptionRequestRepository requestRepository,
            SubscriptionRepository subscriptionRepository,
            SubscriptionPlanRepository planRepository,
            SubscriptionActivationRepository activationRepository,
            ClinicRepository clinicRepository
    ) {
        this.requestRepository = requestRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.planRepository = planRepository;
        this.activationRepository = activationRepository;
        this.clinicRepository = clinicRepository;
    }

    // =====================================================
    // إنشاء طلب اشتراك
    // OWNER
    // =====================================================

    @Transactional(transactionManager = "managementTransactionManager")
    public SubscriptionRequest createRequest(
            Long clinicId,
            Long planId,
            String requestType,
            String ownerNotes
    ) {

        validateIds(
                clinicId,
                planId
        );

        requestType =
                normalizeRequestType(
                        requestType
                );

        // -------------------------------------------------
        // التحقق من العيادة
        // -------------------------------------------------

        clinicRepository
                .findById(clinicId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "العيادة غير موجودة."
                        )
                );

        // -------------------------------------------------
        // التحقق من الخطة
        // -------------------------------------------------

        SubscriptionPlan plan =
                planRepository
                        .findById(planId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "خطة الاشتراك غير موجودة."
                                )
                        );

        if (!isActivePlan(plan)) {

            throw new IllegalStateException(
                    "خطة الاشتراك غير نشطة."
            );
        }

        validatePlanDuration(
                plan
        );

        // -------------------------------------------------
        // منع أكثر من طلب PENDING
        // -------------------------------------------------

        if (requestRepository
                .existsByClinicIdAndStatus(
                        clinicId,
                        "PENDING"
                )) {

            throw new IllegalStateException(
                    "يوجد بالفعل طلب اشتراك قيد المراجعة."
            );
        }

        // -------------------------------------------------
        // الاشتراك الحالي
        // -------------------------------------------------

        Subscription activeSubscription =
                findActiveSubscription(
                        clinicId
                );

        // -------------------------------------------------
        // NEW
        // -------------------------------------------------

        if ("NEW".equals(requestType) &&
                activeSubscription != null) {

            throw new IllegalStateException(
                    "العيادة لديها اشتراك نشط بالفعل."
            );
        }

        // -------------------------------------------------
        // RENEW
        // -------------------------------------------------

        if ("RENEW".equals(requestType) &&
                activeSubscription == null) {

            Subscription latestSubscription =
                    subscriptionRepository
                            .findFirstByClinic_ClinicIdOrderByEndDateDesc(
                                    clinicId
                            )
                            .orElse(null);

            /*
             * يسمح بطلب التجديد حتى إذا كان الاشتراك
             * السابق منتهيًا.
             */
            if (latestSubscription == null) {

                throw new IllegalStateException(
                        "لا يوجد اشتراك سابق يمكن تجديده."
                );
            }
        }

        // -------------------------------------------------
        // إنشاء الطلب
        // -------------------------------------------------

        SubscriptionRequest request =
                new SubscriptionRequest();

        request.setClinicId(
                clinicId
        );

        request.setPlanId(
                planId
        );

        request.setRequestType(
                requestType
        );

        request.setRequestedAt(
                LocalDateTime.now()
        );

        request.setStatus(
                "PENDING"
        );

        request.setOwnerNotes(
                clean(ownerNotes)
        );

        try {

            return requestRepository.save(
                    request
            );

        } catch (
                DataIntegrityViolationException e
        ) {

            throw new IllegalStateException(
                    "تعذر إنشاء الطلب. يوجد طلب قيد المراجعة بالفعل."
            );
        }
    }

    // =====================================================
    // طلبات العيادة
    // =====================================================

    @Transactional(
            transactionManager = "managementTransactionManager",
            readOnly = true
    )
    public List<SubscriptionRequest>
    getClinicRequests(
            Long clinicId
    ) {

        if (clinicId == null) {

            throw new IllegalArgumentException(
                    "معرف العيادة مطلوب."
            );
        }

        return requestRepository
                .findByClinicIdOrderByRequestedAtDesc(
                        clinicId
                );
    }

    // =====================================================
    // كل الطلبات المعلقة
    // ADMIN
    // =====================================================

    @Transactional(
            transactionManager = "managementTransactionManager",
            readOnly = true
    )
    public List<SubscriptionRequest>
    getPendingRequests() {

        return requestRepository
                .findByStatusOrderByRequestedAtAsc(
                        "PENDING"
                );
    }

    // =====================================================
    // الموافقة
    // ADMIN
    // =====================================================

    @Transactional(transactionManager = "managementTransactionManager")
    public SubscriptionRequest approve(
            Long requestId,
            Long adminUserId,
            String adminNotes
    ) {

        if (requestId == null) {

            throw new IllegalArgumentException(
                    "معرف الطلب مطلوب."
            );
        }

        if (adminUserId == null) {

            throw new IllegalArgumentException(
                    "معرف مدير النظام مطلوب."
            );
        }

        LocalDateTime now =
                LocalDateTime.now();

        LocalDate today =
                LocalDate.now();

        // -------------------------------------------------
        // تحميل الطلب
        // -------------------------------------------------

        SubscriptionRequest request =
                requestRepository
                        .findById(requestId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "طلب الاشتراك غير موجود."
                                )
                        );

        // -------------------------------------------------
        // يجب أن يكون Pending
        // -------------------------------------------------

        if (!"PENDING".equalsIgnoreCase(
                request.getStatus()
        )) {

            throw new IllegalStateException(
                    "لا يمكن معالجة طلب تمت معالجته مسبقًا."
            );
        }

        // -------------------------------------------------
        // العيادة
        // -------------------------------------------------

        Clinic clinic =
                clinicRepository
                        .findById(
                                request.getClinicId()
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "العيادة غير موجودة."
                                )
                        );

        // -------------------------------------------------
        // الخطة
        // -------------------------------------------------

        SubscriptionPlan plan =
                planRepository
                        .findById(
                                request.getPlanId()
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "خطة الاشتراك غير موجودة."
                                )
                        );

        if (!isActivePlan(plan)) {

            throw new IllegalStateException(
                    "خطة الاشتراك غير نشطة."
            );
        }

        validatePlanDuration(
                plan
        );

        // -------------------------------------------------
        // البحث عن الاشتراك النشط
        // -------------------------------------------------

        Subscription activeSubscription =
                findActiveSubscription(
                        request.getClinicId()
                );

        // =================================================
        // NEW
        // =================================================

        if ("NEW".equalsIgnoreCase(
                request.getRequestType()
        )) {

            if (activeSubscription != null) {

                throw new IllegalStateException(
                        "العيادة لديها اشتراك نشط بالفعل."
                );
            }

            LocalDate startDate =
                    today;

            LocalDate endDate =
                    calculateEndDate(
                            startDate,
                            plan.getDurationDays()
                    );

            Subscription subscription =
                    createSubscription(
                            clinic,
                            plan,
                            startDate,
                            endDate,
                            now
                    );

            Subscription saved =
                    subscriptionRepository.save(
                            subscription
                    );

            recordActivation(
                    saved.getSubscriptionId(),
                    adminUserId,
                    null,
                    "ACTIVE",
                    adminNotes
            );
        }

        // =================================================
        // RENEW
        // =================================================

        else {

            if (activeSubscription != null) {

                /*
                 * التجديد المسبق:
                 *
                 * الاشتراك الحالي يبقى ACTIVE.
                 *
                 * نمدد تاريخ النهاية،
                 * ونستخدم الخطة التي طلبها OWNER.
                 */

                LocalDate oldEndDate =
                        activeSubscription.getEndDate();

                LocalDate baseDate;

                if (oldEndDate != null &&
                        !oldEndDate.isBefore(today)) {

                    baseDate =
                            oldEndDate;

                } else {

                    baseDate =
                            today.minusDays(1);
                }

                LocalDate newEndDate =
                        calculateEndDate(
                                baseDate.plusDays(1),
                                plan.getDurationDays()
                        );

                SubscriptionPlan previousPlan =
                        activeSubscription.getPlan();

                activeSubscription.setPlan(
                        plan
                );

                activeSubscription.setEndDate(
                        newEndDate
                );

                activeSubscription.setUpdatedAt(
                        now
                );

                Subscription saved =
                        subscriptionRepository.save(
                                activeSubscription
                        );

                String note =
                        buildRenewalNote(
                                previousPlan,
                                plan,
                                adminNotes
                        );

                recordActivation(
                        saved.getSubscriptionId(),
                        adminUserId,
                        "ACTIVE",
                        "ACTIVE",
                        note
                );

            } else {

                /*
                 * لا يوجد اشتراك نشط.
                 *
                 * نبحث عن آخر اشتراك للتاريخ فقط.
                 */

                Subscription latestSubscription =
                        subscriptionRepository
                                .findFirstByClinic_ClinicIdOrderByEndDateDesc(
                                        request.getClinicId()
                                )
                                .orElse(null);

                LocalDate startDate =
                        today;

                if (latestSubscription != null &&
                        latestSubscription.getEndDate() != null &&
                        !latestSubscription
                                .getEndDate()
                                .isAfter(today.minusDays(1))) {

                    LocalDate previousEnd =
                            latestSubscription.getEndDate();

                    if (!previousEnd.isAfter(today)) {

                        startDate =
                                today;
                    }
                }

                LocalDate endDate =
                        calculateEndDate(
                                startDate,
                                plan.getDurationDays()
                        );

                Subscription subscription =
                        createSubscription(
                                clinic,
                                plan,
                                startDate,
                                endDate,
                                now
                        );

                Subscription saved =
                        subscriptionRepository.save(
                                subscription
                        );

                recordActivation(
                        saved.getSubscriptionId(),
                        adminUserId,
                        null,
                        "ACTIVE",
                        adminNotes
                );
            }
        }

        // =================================================
        // تحديث الطلب
        // =================================================

        request.setStatus(
                "APPROVED"
        );

        request.setReviewedBy(
                adminUserId
        );

        request.setReviewedAt(
                now
        );

        request.setAdminNotes(
                clean(adminNotes)
        );

        return requestRepository.save(
                request
        );
    }

    // =====================================================
    // رفض الطلب
    // =====================================================

    @Transactional(transactionManager = "managementTransactionManager")
    public SubscriptionRequest reject(
            Long requestId,
            Long adminUserId,
            String adminNotes
    ) {

        if (requestId == null) {

            throw new IllegalArgumentException(
                    "معرف الطلب مطلوب."
            );
        }

        if (adminUserId == null) {

            throw new IllegalArgumentException(
                    "معرف مدير النظام مطلوب."
            );
        }

        SubscriptionRequest request =
                requestRepository
                        .findById(requestId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "طلب الاشتراك غير موجود."
                                )
                        );

        if (!"PENDING".equalsIgnoreCase(
                request.getStatus()
        )) {

            throw new IllegalStateException(
                    "لا يمكن رفض طلب تمت معالجته مسبقًا."
            );
        }

        LocalDateTime now =
                LocalDateTime.now();

        request.setStatus(
                "REJECTED"
        );

        request.setReviewedBy(
                adminUserId
        );

        request.setReviewedAt(
                now
        );

        request.setAdminNotes(
                clean(adminNotes)
        );

        return requestRepository.save(
                request
        );
    }

    // =====================================================
    // الاشتراك النشط
    // =====================================================

    private Subscription findActiveSubscription(
            Long clinicId
    ) {

        return subscriptionRepository
                .findFirstByClinic_ClinicIdAndStatusOrderByEndDateDesc(
                        clinicId,
                        "ACTIVE"
                )
                .orElse(null);
    }

    // =====================================================
    // إنشاء اشتراك
    // =====================================================

    private Subscription createSubscription(
            Clinic clinic,
            SubscriptionPlan plan,
            LocalDate startDate,
            LocalDate endDate,
            LocalDateTime now
    ) {

        Subscription subscription =
                new Subscription();

        subscription.setClinic(
                clinic
        );

        subscription.setPlan(
                plan
        );

        subscription.setStartDate(
                startDate
        );

        subscription.setEndDate(
                endDate
        );

        subscription.setStatus(
                "ACTIVE"
        );

        subscription.setCreatedAt(
                now
        );

        subscription.setUpdatedAt(
                now
        );

        return subscription;
    }

    // =====================================================
    // حساب نهاية الاشتراك
    // =====================================================

    private LocalDate calculateEndDate(
            LocalDate startDate,
            Integer durationDays
    ) {

        if (startDate == null) {

            throw new IllegalArgumentException(
                    "تاريخ بداية الاشتراك مطلوب."
            );
        }

        if (durationDays == null ||
                durationDays <= 0) {

            throw new IllegalStateException(
                    "مدة خطة الاشتراك غير صالحة."
            );
        }

        return startDate.plusDays(
                durationDays - 1L
        );
    }

    // =====================================================
    // تسجيل العملية
    // =====================================================

    private void recordActivation(
            Long subscriptionId,
            Long adminUserId,
            String previousStatus,
            String newStatus,
            String notes
    ) {

        SubscriptionActivation activation =
                new SubscriptionActivation();

        activation.setSubscriptionId(
                subscriptionId
        );

        activation.setAdminUserId(
                adminUserId
        );

        activation.setActivationDate(
                LocalDateTime.now()
        );

        activation.setPreviousStatus(
                previousStatus
        );

        activation.setNewStatus(
                newStatus
        );

        activation.setNotes(
                clean(notes)
        );

        activationRepository.save(
                activation
        );
    }

    // =====================================================
    // بناء ملاحظة التجديد
    // =====================================================

    private String buildRenewalNote(
            SubscriptionPlan previousPlan,
            SubscriptionPlan newPlan,
            String adminNotes
    ) {

        StringBuilder result =
                new StringBuilder();

        if (previousPlan != null &&
                previousPlan.getPlanName() != null) {

            result.append(
                    "الخطة السابقة: "
            );

            result.append(
                    previousPlan.getPlanName()
            );
        }

        if (newPlan != null &&
                newPlan.getPlanName() != null) {

            if (!result.isEmpty()) {

                result.append(
                        " | "
                );
            }

            result.append(
                    "الخطة الجديدة: "
            );

            result.append(
                    newPlan.getPlanName()
            );
        }

        if (adminNotes != null &&
                !adminNotes.isBlank()) {

            if (!result.isEmpty()) {

                result.append(
                        " | "
                );
            }

            result.append(
                    "ملاحظات: "
            );

            result.append(
                    adminNotes.trim()
            );
        }

        return result.isEmpty()
                ? null
                : result.toString();
    }

    // =====================================================
    // التحقق من IDs
    // =====================================================

    private void validateIds(
            Long clinicId,
            Long planId
    ) {

        if (clinicId == null) {

            throw new IllegalArgumentException(
                    "معرف العيادة مطلوب."
            );
        }

        if (planId == null) {

            throw new IllegalArgumentException(
                    "معرف الخطة مطلوب."
            );
        }
    }

    // =====================================================
    // Normalize Request Type
    // =====================================================

    private String normalizeRequestType(
            String requestType
    ) {

        if (requestType == null ||
                requestType.isBlank()) {

            throw new IllegalArgumentException(
                    "نوع الطلب مطلوب."
            );
        }

        String normalized =
                requestType.trim()
                        .toUpperCase();

        if (!normalized.equals("NEW") &&
                !normalized.equals("RENEW")) {

            throw new IllegalArgumentException(
                    "نوع الطلب يجب أن يكون NEW أو RENEW."
            );
        }

        return normalized;
    }

    // =====================================================
    // حالة الخطة
    // =====================================================

    private boolean isActivePlan(
            SubscriptionPlan plan
    ) {

        return plan != null &&
                "ACTIVE".equalsIgnoreCase(
                        plan.getStatus()
                );
    }

    // =====================================================
    // مدة الخطة
    // =====================================================

    private void validatePlanDuration(
            SubscriptionPlan plan
    ) {

        if (plan.getDurationDays() == null ||
                plan.getDurationDays() <= 0) {

            throw new IllegalStateException(
                    "مدة خطة الاشتراك غير صالحة."
            );
        }
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