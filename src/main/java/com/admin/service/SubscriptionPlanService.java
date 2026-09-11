package com.admin.service;

import com.admin.dto.SubscriptionPlanRequest;
import com.admin.dto.SubscriptionPlanResponse;
import com.admin.entity.SubscriptionPlan;
import com.admin.repository.SubscriptionPlanRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class SubscriptionPlanService {

    private final SubscriptionPlanRepository repository;

    public SubscriptionPlanService(
            SubscriptionPlanRepository repository
    ) {
        this.repository = repository;
    }

    public SubscriptionPlanResponse create(
            SubscriptionPlanRequest request
    ) {

        validate(request);

        String planName = request.getPlanName().trim();

        if (repository.existsByPlanName(planName)) {
            throw new IllegalArgumentException(
                    "اسم الخطة مستخدم بالفعل"
            );
        }

        SubscriptionPlan plan = new SubscriptionPlan();

        plan.setPlanName(planName);
        plan.setDescription(request.getDescription());
        plan.setPrice(request.getPrice());
        plan.setCurrencyCode(request.getCurrencyCode());
        plan.setDurationDays(request.getDurationDays());

        plan.setStatus(
                request.getStatus() != null
                        ? request.getStatus()
                        : "ACTIVE"
        );

        LocalDateTime now = LocalDateTime.now();

        plan.setCreatedAt(now);
        plan.setUpdatedAt(now);

        return mapToResponse(
                repository.save(plan)
        );
    }

    public SubscriptionPlanResponse update(
            Long id,
            SubscriptionPlanRequest request
    ) {

        validate(request);

        SubscriptionPlan plan =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "خطة الاشتراك غير موجودة"
                                ));

        String planName = request.getPlanName().trim();

        repository.findByPlanName(planName)
                .ifPresent(existing -> {

                    if (!existing.getPlanId().equals(id)) {
                        throw new IllegalArgumentException(
                                "اسم الخطة مستخدم بالفعل"
                        );
                    }
                });

        plan.setPlanName(planName);
        plan.setDescription(request.getDescription());
        plan.setPrice(request.getPrice());
        plan.setCurrencyCode(request.getCurrencyCode());
        plan.setDurationDays(request.getDurationDays());

        if (request.getStatus() != null) {
            plan.setStatus(request.getStatus());
        }

        plan.setUpdatedAt(LocalDateTime.now());

        return mapToResponse(
                repository.save(plan)
        );
    }

    public SubscriptionPlanResponse getById(Long id) {

        return repository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() ->
                        new RuntimeException(
                                "خطة الاشتراك غير موجودة"
                        ));
    }

    public List<SubscriptionPlanResponse> getAll() {

        return repository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<SubscriptionPlanResponse> getActive() {

        return repository.findByStatus("ACTIVE")
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public void delete(Long id) {

        if (!repository.existsById(id)) {
            throw new RuntimeException(
                    "خطة الاشتراك غير موجودة"
            );
        }

        repository.deleteById(id);
    }

    private void validate(
            SubscriptionPlanRequest request
    ) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "بيانات الخطة مطلوبة"
            );
        }

        if (request.getPlanName() == null ||
                request.getPlanName().isBlank()) {

            throw new IllegalArgumentException(
                    "اسم الخطة مطلوب"
            );
        }

        if (request.getPrice() != null &&
                request.getPrice().signum() < 0) {

            throw new IllegalArgumentException(
                    "السعر لا يمكن أن يكون سالبًا"
            );
        }

        if (request.getDurationDays() != null &&
                request.getDurationDays() <= 0) {

            throw new IllegalArgumentException(
                    "مدة الخطة يجب أن تكون أكبر من صفر"
            );
        }
    }

    private SubscriptionPlanResponse mapToResponse(
            SubscriptionPlan plan
    ) {

        SubscriptionPlanResponse response =
                new SubscriptionPlanResponse();

        response.setPlanId(plan.getPlanId());
        response.setPlanName(plan.getPlanName());
        response.setDescription(plan.getDescription());
        response.setPrice(plan.getPrice());
        response.setCurrencyCode(plan.getCurrencyCode());
        response.setDurationDays(plan.getDurationDays());
        response.setStatus(plan.getStatus());
        response.setCreatedAt(plan.getCreatedAt());
        response.setUpdatedAt(plan.getUpdatedAt());

        return response;
    }
}