package com.admin.repository;

import com.admin.entity.Subscription;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository
        extends JpaRepository<Subscription, Long> {

    // =====================================================
    // جميع اشتراكات العيادة
    // =====================================================

    @EntityGraph(attributePaths = {"clinic", "plan"})
    List<Subscription> findByClinic_ClinicId(
            Long clinicId
    );

    // =====================================================
    // حسب الخطة
    // =====================================================

    @EntityGraph(attributePaths = {"clinic", "plan"})
    List<Subscription> findByPlan_PlanId(
            Long planId
    );

    // =====================================================
    // حسب الحالة
    // =====================================================

    @EntityGraph(attributePaths = {"clinic", "plan"})
    List<Subscription> findByStatus(
            String status
    );

    // =====================================================
    // آخر اشتراك للعيادة
    // =====================================================

    @EntityGraph(attributePaths = {"clinic", "plan"})
    Optional<Subscription>
    findFirstByClinic_ClinicIdOrderByEndDateDesc(
            Long clinicId
    );

    // =====================================================
    // حسب العيادة والحالة
    // =====================================================

    @EntityGraph(attributePaths = {"clinic", "plan"})
    List<Subscription>
    findByClinic_ClinicIdAndStatus(
            Long clinicId,
            String status
    );

    // =====================================================
    // الاشتراك الحالي
    // =====================================================

    @EntityGraph(attributePaths = {"clinic", "plan"})
    Optional<Subscription>
    findFirstByClinic_ClinicIdAndStatusOrderByEndDateDesc(
            Long clinicId,
            String status
    );

    // =====================================================
    // جميع اشتراكات العيادة مرتبة
    // =====================================================

    @EntityGraph(attributePaths = {"clinic", "plan"})
    List<Subscription>
    findByClinic_ClinicIdOrderByEndDateDesc(
            Long clinicId
    );
}