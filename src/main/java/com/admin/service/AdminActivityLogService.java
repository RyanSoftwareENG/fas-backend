package com.admin.service;

import com.admin.dto.AdminActivityLogFilterRequest;
import com.admin.dto.AdminActivityLogResponse;
import com.admin.entity.AdminActivityLog;
import com.admin.entity.FasAdminUser;
import com.admin.repository.AdminActivityLogRepository;
import com.admin.repository.FasAdminUserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class AdminActivityLogService {

    private final AdminActivityLogRepository logRepository;
    private final FasAdminUserRepository adminUserRepository;

    public AdminActivityLogService(
            AdminActivityLogRepository logRepository,
            FasAdminUserRepository adminUserRepository
    ) {
        this.logRepository = logRepository;
        this.adminUserRepository = adminUserRepository;
    }

    // =====================================================
    // تسجيل نشاط
    // =====================================================

    public AdminActivityLogResponse log(
            Long adminUserId,
            String action,
            String entityName,
            Long entityId,
            String details
    ) {

        if (action == null || action.isBlank()) {
            throw new IllegalArgumentException(
                    "ACTION مطلوب"
            );
        }

        AdminActivityLog log =
                new AdminActivityLog();

        log.setAdminUserId(adminUserId);
        log.setAction(action.trim());
        log.setEntityName(
                entityName == null
                        ? null
                        : entityName.trim()
        );
        log.setEntityId(entityId);
        log.setDetails(details);
        log.setCreatedAt(LocalDateTime.now());

        return mapToResponse(
                logRepository.save(log)
        );
    }

    // =====================================================
    // البحث
    // =====================================================

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<AdminActivityLogResponse> search(
            AdminActivityLogFilterRequest request
    ) {

        if (request == null) {
            request =
                    new AdminActivityLogFilterRequest();
        }

        String keyword =
                normalize(request.getKeyword());

        LocalDateTime fromDate =
                request.getFrom() == null
                        ? null
                        : request.getFrom()
                        .atStartOfDay();

        LocalDateTime toDate =
                request.getTo() == null
                        ? null
                        : request.getTo()
                        .plusDays(1)
                        .atStartOfDay();

        return logRepository.search(
                        request.getAdminUserId(),
                        fromDate,
                        toDate,
                        keyword
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<AdminActivityLogResponse> getByAdminUser(
            Long adminUserId
    ) {

        return logRepository
                .findByAdminUserIdOrderByCreatedAtDesc(
                        adminUserId
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<AdminActivityLogResponse> getAll() {

        return logRepository
                .findByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private String normalize(String value) {

        if (value == null) {
            return null;
        }

        String normalized =
                value.trim();

        return normalized.isBlank()
                ? null
                : normalized;
    }

    private AdminActivityLogResponse mapToResponse(
            AdminActivityLog log
    ) {

        AdminActivityLogResponse response =
                new AdminActivityLogResponse();

        response.setLogId(
                log.getLogId()
        );

        response.setAdminUserId(
                log.getAdminUserId()
        );

        response.setAction(
                log.getAction()
        );

        response.setEntityName(
                log.getEntityName()
        );

        response.setEntityId(
                log.getEntityId()
        );

        response.setDetails(
                log.getDetails()
        );

        response.setCreatedAt(
                log.getCreatedAt()
        );

        if (log.getAdminUserId() != null) {

            adminUserRepository
                    .findById(log.getAdminUserId())
                    .ifPresent(user ->
                            response.setAdminUsername(
                                    user.getUsername()
                            )
                    );
        }

        return response;
    }
}