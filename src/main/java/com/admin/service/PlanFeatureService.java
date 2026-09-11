package com.admin.service;

import com.admin.dto.PlanFeatureRequest;
import com.admin.dto.PlanFeatureResponse;
import com.admin.entity.Feature;
import com.admin.entity.PlanFeature;
import com.admin.entity.SubscriptionPlan;
import com.admin.repository.FeatureRepository;
import com.admin.repository.PlanFeatureRepository;
import com.admin.repository.SubscriptionPlanRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class PlanFeatureService {

    private final PlanFeatureRepository repository;
    private final SubscriptionPlanRepository planRepository;
    private final FeatureRepository featureRepository;

    public PlanFeatureService(
            PlanFeatureRepository repository,
            SubscriptionPlanRepository planRepository,
            FeatureRepository featureRepository
    ) {
        this.repository = repository;
        this.planRepository = planRepository;
        this.featureRepository = featureRepository;
    }

    public PlanFeatureResponse assign(
            PlanFeatureRequest request
    ) {

        validate(request);

        if (!planRepository.existsById(request.getPlanId())) {
            throw new RuntimeException(
                    "خطة الاشتراك غير موجودة"
            );
        }

        Feature feature =
                featureRepository.findById(
                        request.getFeatureId()
                ).orElseThrow(() ->
                        new RuntimeException(
                                "الميزة غير موجودة"
                        ));

        if (repository.existsByPlanIdAndFeatureId(
                request.getPlanId(),
                request.getFeatureId()
        )) {
            throw new RuntimeException(
                    "الميزة مرتبطة بالفعل بهذه الخطة"
            );
        }

        PlanFeature planFeature =
                new PlanFeature();

        planFeature.setPlanId(
                request.getPlanId()
        );

        planFeature.setFeatureId(
                request.getFeatureId()
        );

        planFeature.setEnabled(
                request.getEnabled() != null
                        ? request.getEnabled()
                        : true
        );

        planFeature.setFeatureLimit(
                request.getFeatureLimit()
        );

        PlanFeature saved =
                repository.save(planFeature);

        return mapToResponse(saved, feature);
    }

    public PlanFeatureResponse update(
            Long planId,
            Long featureId,
            PlanFeatureRequest request
    ) {

        PlanFeature planFeature =
                repository.findByPlanIdAndFeatureId(
                        planId,
                        featureId
                ).orElseThrow(() ->
                        new RuntimeException(
                                "ربط الخطة بالميزة غير موجود"
                        ));

        Feature feature =
                featureRepository.findById(featureId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "الميزة غير موجودة"
                                ));

        if (request.getEnabled() != null) {
            planFeature.setEnabled(
                    request.getEnabled()
            );
        }

        if (request.getFeatureLimit() != null) {
            if (request.getFeatureLimit() < 0) {
                throw new IllegalArgumentException(
                        "حد الميزة لا يمكن أن يكون سالبًا"
                );
            }

            planFeature.setFeatureLimit(
                    request.getFeatureLimit()
            );
        }

        return mapToResponse(
                repository.save(planFeature),
                feature
        );
    }

    public List<PlanFeatureResponse> getByPlan(
            Long planId
    ) {

        if (!planRepository.existsById(planId)) {
            throw new RuntimeException(
                    "خطة الاشتراك غير موجودة"
            );
        }

        return repository.findByPlanId(planId)
                .stream()
                .map(pf -> {

                    Feature feature =
                            featureRepository.findById(
                                    pf.getFeatureId()
                            ).orElse(null);

                    return mapToResponse(
                            pf,
                            feature
                    );
                })
                .toList();
    }

    public List<PlanFeatureResponse> getEnabledByPlan(
            Long planId
    ) {

        if (!planRepository.existsById(planId)) {
            throw new RuntimeException(
                    "خطة الاشتراك غير موجودة"
            );
        }

        return repository
                .findEnabledFeaturesByPlanId(planId)
                .stream()
                .map(pf -> {

                    Feature feature =
                            featureRepository.findById(
                                    pf.getFeatureId()
                            ).orElse(null);

                    return mapToResponse(
                            pf,
                            feature
                    );
                })
                .toList();
    }

    public void remove(
            Long planId,
            Long featureId
    ) {

        if (!repository.existsByPlanIdAndFeatureId(
                planId,
                featureId
        )) {
            throw new RuntimeException(
                    "ربط الخطة بالميزة غير موجود"
            );
        }

        repository.deleteByPlanIdAndFeatureId(
                planId,
                featureId
        );
    }

    private void validate(
            PlanFeatureRequest request
    ) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "بيانات ربط الخطة بالميزة مطلوبة"
            );
        }

        if (request.getPlanId() == null) {
            throw new IllegalArgumentException(
                    "PLAN_ID مطلوب"
            );
        }

        if (request.getFeatureId() == null) {
            throw new IllegalArgumentException(
                    "FEATURE_ID مطلوب"
            );
        }

        if (request.getFeatureLimit() != null &&
                request.getFeatureLimit() < 0) {
            throw new IllegalArgumentException(
                    "حد الميزة لا يمكن أن يكون سالبًا"
            );
        }
    }

    private PlanFeatureResponse mapToResponse(
            PlanFeature planFeature,
            Feature feature
    ) {

        PlanFeatureResponse response =
                new PlanFeatureResponse();

        response.setPlanId(
                planFeature.getPlanId()
        );

        response.setFeatureId(
                planFeature.getFeatureId()
        );

        if (feature != null) {
            response.setFeatureCode(
                    feature.getFeatureCode()
            );

            response.setFeatureName(
                    feature.getFeatureName()
            );
        }

        response.setEnabled(
                planFeature.getEnabled()
        );

        response.setFeatureLimit(
                planFeature.getFeatureLimit()
        );

        return response;
    }
}