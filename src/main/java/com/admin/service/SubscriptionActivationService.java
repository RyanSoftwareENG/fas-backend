package com.admin.service;

import com.admin.dto.SubscriptionActivationRequest;
import com.admin.dto.SubscriptionActivationResponse;
import com.admin.entity.FasAdminUser;
import com.admin.entity.Subscription;
import com.admin.entity.SubscriptionActivation;
import com.admin.repository.FasAdminUserRepository;
import com.admin.repository.SubscriptionActivationRepository;
import com.admin.repository.SubscriptionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class SubscriptionActivationService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionActivationRepository activationRepository;
    private final FasAdminUserRepository adminUserRepository;

    public SubscriptionActivationService(
            SubscriptionRepository subscriptionRepository,
            SubscriptionActivationRepository activationRepository,
            FasAdminUserRepository adminUserRepository
    ) {
        this.subscriptionRepository = subscriptionRepository;
        this.activationRepository = activationRepository;
        this.adminUserRepository = adminUserRepository;
    }

    public SubscriptionActivationResponse changeStatus(
            SubscriptionActivationRequest request
    ) {

        validate(request);

        Subscription subscription =
                subscriptionRepository.findById(
                        request.getSubscriptionId()
                ).orElseThrow(() ->
                        new RuntimeException(
                                "الاشتراك غير موجود"
                        ));

        FasAdminUser adminUser =
                adminUserRepository.findById(
                        request.getAdminUserId()
                ).orElseThrow(() ->
                        new RuntimeException(
                                "المستخدم الإداري غير موجود"
                        ));

        if (!"ACTIVE".equalsIgnoreCase(
                adminUser.getStatus()
        )) {
            throw new IllegalStateException(
                    "المستخدم الإداري غير فعال"
            );
        }

        String previousStatus =
                subscription.getStatus();

        String newStatus =
                request.getNewStatus().toUpperCase();

        validateStatus(newStatus);

        if (previousStatus.equalsIgnoreCase(newStatus)) {
            throw new IllegalArgumentException(
                    "الحالة الجديدة مطابقة للحالة الحالية"
            );
        }

        subscription.setStatus(newStatus);

        subscription.setUpdatedAt(
                LocalDateTime.now()
        );

        subscriptionRepository.save(subscription);

        SubscriptionActivation activation =
                new SubscriptionActivation();

        activation.setSubscriptionId(
                subscription.getSubscriptionId()
        );

        activation.setAdminUserId(
                adminUser.getAdminUserId()
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
                request.getNotes()
        );

        SubscriptionActivation saved =
                activationRepository.save(activation);

        return mapToResponse(saved);
    }

    public List<SubscriptionActivationResponse> getHistory(
            Long subscriptionId
    ) {

        if (!subscriptionRepository.existsById(subscriptionId)) {
            throw new RuntimeException(
                    "الاشتراك غير موجود"
            );
        }

        return activationRepository
                .findBySubscriptionIdOrderByActivationDateDesc(
                        subscriptionId
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private void validate(
            SubscriptionActivationRequest request
    ) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "بيانات العملية مطلوبة"
            );
        }

        if (request.getSubscriptionId() == null) {
            throw new IllegalArgumentException(
                    "SUBSCRIPTION_ID مطلوب"
            );
        }

        if (request.getAdminUserId() == null) {
            throw new IllegalArgumentException(
                    "ADMIN_USER_ID مطلوب"
            );
        }

        if (request.getNewStatus() == null ||
                request.getNewStatus().isBlank()) {
            throw new IllegalArgumentException(
                    "الحالة الجديدة مطلوبة"
            );
        }
    }

    private void validateStatus(
            String status
    ) {

        switch (status) {

            case "PENDING":
            case "ACTIVE":
            case "EXPIRED":
            case "SUSPENDED":
            case "CANCELLED":
                break;

            default:
                throw new IllegalArgumentException(
                        "حالة الاشتراك غير صحيحة"
                );
        }
    }

    private SubscriptionActivationResponse mapToResponse(
            SubscriptionActivation activation
    ) {

        SubscriptionActivationResponse response =
                new SubscriptionActivationResponse();

        response.setActivationId(
                activation.getActivationId()
        );

        response.setSubscriptionId(
                activation.getSubscriptionId()
        );

        response.setAdminUserId(
                activation.getAdminUserId()
        );

        response.setActivationDate(
                activation.getActivationDate()
        );

        response.setPreviousStatus(
                activation.getPreviousStatus()
        );

        response.setNewStatus(
                activation.getNewStatus()
        );

        response.setNotes(
                activation.getNotes()
        );

        return response;
    }
}