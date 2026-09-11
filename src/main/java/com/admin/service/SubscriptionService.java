package com.admin.service;

import com.admin.dto.SubscriptionRequestCreateRequest;
import com.admin.dto.SubscriptionResponse;
import com.admin.entity.Clinic;
import com.admin.entity.Subscription;
import com.admin.entity.SubscriptionPlan;
import com.admin.repository.ClinicRepository;
import com.admin.repository.SubscriptionPlanRepository;
import com.admin.repository.SubscriptionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanRepository planRepository;
    private final ClinicRepository clinicRepository;

    public SubscriptionService(
            SubscriptionRepository subscriptionRepository,
            SubscriptionPlanRepository planRepository,
            ClinicRepository clinicRepository
    ) {
        this.subscriptionRepository = subscriptionRepository;
        this.planRepository = planRepository;
        this.clinicRepository = clinicRepository;
    }

    public SubscriptionResponse create(SubscriptionRequestCreateRequest request) {

        Clinic clinic = clinicRepository.findById(request.getClinicId())
                .orElseThrow(() ->
                        new RuntimeException("العيادة غير موجودة"));

        SubscriptionPlan plan = planRepository.findById(request.getPlanId())
                .orElseThrow(() ->
                        new RuntimeException("الخطة غير موجودة"));

        Subscription subscription = new Subscription();

        subscription.setClinic(clinic);
        subscription.setPlan(plan);
        subscription.setStartDate(request.getStartDate());
        subscription.setEndDate(request.getEndDate());
        subscription.setStatus(
                request.getStatus() != null
                        ? request.getStatus()
                        : "ACTIVE"
        );

        LocalDateTime now = LocalDateTime.now();

        subscription.setCreatedAt(now);
        subscription.setUpdatedAt(now);

        Subscription saved =
                subscriptionRepository.save(subscription);

        return mapToResponse(saved);
    }

    public SubscriptionResponse update(
            Long subscriptionId,
            SubscriptionRequestCreateRequest request
    ) {

        Subscription subscription =
                subscriptionRepository.findById(subscriptionId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "الاشتراك غير موجود"));

        if (request.getClinicId() != null) {
            Clinic clinic =
                    clinicRepository.findById(request.getClinicId())
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "العيادة غير موجودة"));

            subscription.setClinic(clinic);
        }

        if (request.getPlanId() != null) {
            SubscriptionPlan plan =
                    planRepository.findById(request.getPlanId())
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "الخطة غير موجودة"));

            subscription.setPlan(plan);
        }

        if (request.getStartDate() != null) {
            subscription.setStartDate(request.getStartDate());
        }

        if (request.getEndDate() != null) {
            subscription.setEndDate(request.getEndDate());
        }

        if (request.getStatus() != null) {
            subscription.setStatus(request.getStatus());
        }

        subscription.setUpdatedAt(LocalDateTime.now());

        return mapToResponse(subscription);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public SubscriptionResponse getById(Long id) {

        Subscription subscription =
                subscriptionRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "الاشتراك غير موجود"));

        return mapToResponse(subscription);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<SubscriptionResponse> getByClinic(Long clinicId) {

        return subscriptionRepository
                .findByClinic_ClinicId(clinicId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public void delete(Long id) {

        if (!subscriptionRepository.existsById(id)) {
            throw new RuntimeException("الاشتراك غير موجود");
        }

        subscriptionRepository.deleteById(id);
    }

    private SubscriptionResponse mapToResponse(
            Subscription subscription
    ) {

        SubscriptionResponse response =
                new SubscriptionResponse();

        response.setSubscriptionId(
                subscription.getSubscriptionId());

        response.setClinicId(
                subscription.getClinic().getClinicId());

        response.setClinicName(
                subscription.getClinic().getClinicName());

        response.setPlanId(
                subscription.getPlan().getPlanId());

        response.setPlanName(
                subscription.getPlan().getPlanName());

        response.setStartDate(
                subscription.getStartDate());

        response.setEndDate(
                subscription.getEndDate());

        response.setStatus(
                subscription.getStatus());

        response.setCreatedAt(
                subscription.getCreatedAt());

        response.setUpdatedAt(
                subscription.getUpdatedAt());

        return response;
    }
}