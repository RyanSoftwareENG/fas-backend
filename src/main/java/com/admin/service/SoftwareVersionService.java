package com.admin.service;

import com.admin.dto.SoftwareVersionRequest;
import com.admin.dto.SoftwareVersionResponse;
import com.admin.entity.SoftwareVersion;
import com.admin.repository.SoftwareVersionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class SoftwareVersionService {

    private final SoftwareVersionRepository repository;

    public SoftwareVersionService(
            SoftwareVersionRepository repository
    ) {
        this.repository = repository;
    }

    public SoftwareVersionResponse create(
            SoftwareVersionRequest request
    ) {

        validateRequest(request);

        SoftwareVersion version = new SoftwareVersion();

        version.setApplicationName(
                request.getApplicationName().trim()
        );

        version.setVersionNumber(
                request.getVersionNumber().trim()
        );

        version.setMinimumVersion(
                request.getMinimumVersion()
        );

        version.setReleaseDate(
                request.getReleaseDate()
        );

        version.setStatus(
                request.getStatus() != null
                        ? request.getStatus()
                        : "ACTIVE"
        );

        version.setMandatory(
                request.getMandatory() != null
                        ? request.getMandatory()
                        : false
        );

        version.setReleaseNotes(
                request.getReleaseNotes()
        );

        return mapToResponse(
                repository.save(version)
        );
    }

    public SoftwareVersionResponse update(
            Long id,
            SoftwareVersionRequest request
    ) {

        SoftwareVersion version =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "الإصدار غير موجود"
                                ));

        validateRequest(request);

        version.setApplicationName(
                request.getApplicationName().trim()
        );

        version.setVersionNumber(
                request.getVersionNumber().trim()
        );

        version.setMinimumVersion(
                request.getMinimumVersion()
        );

        version.setReleaseDate(
                request.getReleaseDate()
        );

        if (request.getStatus() != null) {
            version.setStatus(
                    request.getStatus()
            );
        }

        if (request.getMandatory() != null) {
            version.setMandatory(
                    request.getMandatory()
            );
        }

        version.setReleaseNotes(
                request.getReleaseNotes()
        );

        return mapToResponse(
                repository.save(version)
        );
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public SoftwareVersionResponse getById(Long id) {

        SoftwareVersion version =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "الإصدار غير موجود"
                                ));

        return mapToResponse(version);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<SoftwareVersionResponse> getAll() {

        return repository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<SoftwareVersionResponse> getByApplication(
            String applicationName
    ) {

        return repository
                .findByApplicationNameOrderByReleaseDateDesc(
                        applicationName
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public SoftwareVersionResponse getLatest(
            String applicationName
    ) {

        SoftwareVersion version =
                repository
                        .findFirstByApplicationNameOrderByReleaseDateDesc(
                                applicationName
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "لا يوجد إصدار لهذا التطبيق"
                                ));

        return mapToResponse(version);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public SoftwareVersionResponse getLatestActive(
            String applicationName
    ) {

        SoftwareVersion version =
                repository
                        .findFirstByApplicationNameAndStatusOrderByReleaseDateDesc(
                                applicationName,
                                "ACTIVE"
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "لا يوجد إصدار نشط لهذا التطبيق"
                                ));

        return mapToResponse(version);
    }

    public void delete(Long id) {

        if (!repository.existsById(id)) {
            throw new RuntimeException(
                    "الإصدار غير موجود"
            );
        }

        repository.deleteById(id);
    }

    private void validateRequest(
            SoftwareVersionRequest request
    ) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "بيانات الإصدار مطلوبة"
            );
        }

        if (request.getApplicationName() == null ||
                request.getApplicationName().isBlank()) {

            throw new IllegalArgumentException(
                    "اسم التطبيق مطلوب"
            );
        }

        if (request.getVersionNumber() == null ||
                request.getVersionNumber().isBlank()) {

            throw new IllegalArgumentException(
                    "رقم الإصدار مطلوب"
            );
        }

        if (request.getReleaseDate() == null) {
            throw new IllegalArgumentException(
                    "تاريخ الإصدار مطلوب"
            );
        }
    }

    private SoftwareVersionResponse mapToResponse(
            SoftwareVersion version
    ) {

        SoftwareVersionResponse response =
                new SoftwareVersionResponse();

        response.setVersionId(
                version.getVersionId()
        );

        response.setApplicationName(
                version.getApplicationName()
        );

        response.setVersionNumber(
                version.getVersionNumber()
        );

        response.setMinimumVersion(
                version.getMinimumVersion()
        );

        response.setReleaseDate(
                version.getReleaseDate()
        );

        response.setStatus(
                version.getStatus()
        );

        response.setMandatory(
                version.getMandatory()
        );

        response.setReleaseNotes(
                version.getReleaseNotes()
        );

        return response;
    }
}