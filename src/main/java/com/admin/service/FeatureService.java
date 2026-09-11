package com.admin.service;

import com.admin.dto.FeatureRequest;
import com.admin.dto.FeatureResponse;
import com.admin.entity.Feature;
import com.admin.repository.FeatureRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class FeatureService {

    private final FeatureRepository repository;

    public FeatureService(FeatureRepository repository) {
        this.repository = repository;
    }

    public FeatureResponse create(FeatureRequest request) {

        validate(request);

        String code = request.getFeatureCode().trim();

        if (repository.existsByFeatureCode(code)) {
            throw new RuntimeException(
                    "رمز الميزة مستخدم بالفعل"
            );
        }

        Feature feature = new Feature();

        feature.setFeatureCode(code);
        feature.setFeatureName(
                request.getFeatureName().trim()
        );
        feature.setDescription(
                request.getDescription()
        );
        feature.setStatus(
                request.getStatus() == null
                        ? "ACTIVE"
                        : request.getStatus()
        );

        return mapToResponse(
                repository.save(feature)
        );
    }

    public FeatureResponse update(
            Long id,
            FeatureRequest request
    ) {

        validate(request);

        Feature feature =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "الميزة غير موجودة"
                                ));

        String code = request.getFeatureCode().trim();

        repository.findByFeatureCode(code)
                .ifPresent(existing -> {
                    if (!existing.getFeatureId().equals(id)) {
                        throw new RuntimeException(
                                "رمز الميزة مستخدم بالفعل"
                        );
                    }
                });

        feature.setFeatureCode(code);
        feature.setFeatureName(
                request.getFeatureName().trim()
        );
        feature.setDescription(
                request.getDescription()
        );

        if (request.getStatus() != null) {
            feature.setStatus(
                    request.getStatus()
            );
        }

        return mapToResponse(
                repository.save(feature)
        );
    }

    public FeatureResponse getById(Long id) {

        return repository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() ->
                        new RuntimeException(
                                "الميزة غير موجودة"
                        ));
    }

    public List<FeatureResponse> getAll() {

        return repository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<FeatureResponse> getActive() {

        return repository.findByStatus("ACTIVE")
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public void delete(Long id) {

        if (!repository.existsById(id)) {
            throw new RuntimeException(
                    "الميزة غير موجودة"
            );
        }

        repository.deleteById(id);
    }

    private void validate(FeatureRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "بيانات الميزة مطلوبة"
            );
        }

        if (request.getFeatureCode() == null ||
                request.getFeatureCode().isBlank()) {
            throw new IllegalArgumentException(
                    "رمز الميزة مطلوب"
            );
        }

        if (request.getFeatureName() == null ||
                request.getFeatureName().isBlank()) {
            throw new IllegalArgumentException(
                    "اسم الميزة مطلوب"
            );
        }
    }

    private FeatureResponse mapToResponse(
            Feature feature
    ) {

        FeatureResponse response =
                new FeatureResponse();

        response.setFeatureId(
                feature.getFeatureId()
        );

        response.setFeatureCode(
                feature.getFeatureCode()
        );

        response.setFeatureName(
                feature.getFeatureName()
        );

        response.setDescription(
                feature.getDescription()
        );

        response.setStatus(
                feature.getStatus()
        );

        return response;
    }
}