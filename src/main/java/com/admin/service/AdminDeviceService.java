package com.admin.service;

import com.admin.dto.DeviceResponse;
import com.admin.entity.Device;
import com.admin.repository.DeviceRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AdminDeviceService {

    private final DeviceRepository deviceRepository;

    public AdminDeviceService(
            DeviceRepository deviceRepository
    ) {
        this.deviceRepository = deviceRepository;
    }

    /**
     * جميع الأجهزة
     */
    @Transactional(readOnly = true)
    public List<DeviceResponse> getAllDevices() {

        return deviceRepository
                .findAllByOrderByLastSeenAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * جهاز واحد
     */
    @Transactional(readOnly = true)
    public DeviceResponse getDevice(Long deviceId) {

        Device device = deviceRepository
                .findById(deviceId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "الجهاز غير موجود."
                        )
                );

        return toResponse(device);
    }

    /**
     * تغيير حالة الجهاز
     */
    public DeviceResponse changeStatus(
            Long deviceId,
            String status
    ) {

        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException(
                    "حالة الجهاز مطلوبة."
            );
        }

        Device device = deviceRepository
                .findById(deviceId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "الجهاز غير موجود."
                        )
                );

        String normalizedStatus =
                status.trim().toUpperCase();

        if (!normalizedStatus.equals("ACTIVE")
                && !normalizedStatus.equals("BLOCKED")
                && !normalizedStatus.equals("REVOKED")) {

            throw new IllegalArgumentException(
                    "حالة الجهاز غير صالحة."
            );
        }

        device.setStatus(normalizedStatus);

        return toResponse(
                deviceRepository.save(device)
        );
    }

    /**
     * حذف الجهاز
     */
    public void deleteDevice(Long deviceId) {

        if (!deviceRepository.existsById(deviceId)) {
            throw new IllegalArgumentException(
                    "الجهاز غير موجود."
            );
        }

        deviceRepository.deleteById(deviceId);
    }

    /**
     * تحويل Entity إلى DTO
     */
    private DeviceResponse toResponse(Device device) {

        DeviceResponse response =
                new DeviceResponse();

        response.setDeviceId(
                device.getDeviceId()
        );

        response.setClinicId(
                device.getClinicId()
        );

        response.setDeviceName(
                device.getDeviceName()
        );

        response.setInstallationId(
                device.getInstallationId()
        );

        response.setStatus(
                device.getStatus()
        );

        response.setFirstSeenAt(
                device.getFirstSeenAt()
        );

        response.setLastSeenAt(
                device.getLastSeenAt()
        );

        return response;
    }
}