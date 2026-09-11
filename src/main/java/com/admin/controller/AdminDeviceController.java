package com.admin.controller;

import com.admin.dto.DeviceResponse;
import com.admin.service.AdminDeviceService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/devices")
public class AdminDeviceController {

    private final AdminDeviceService adminDeviceService;

    public AdminDeviceController(
            AdminDeviceService adminDeviceService
    ) {
        this.adminDeviceService = adminDeviceService;
    }

    @GetMapping
    public ResponseEntity<List<DeviceResponse>> getAllDevices() {

        return ResponseEntity.ok(
                adminDeviceService.getAllDevices()
        );
    }

    @GetMapping("/{deviceId}")
    public ResponseEntity<DeviceResponse> getDevice(
            @PathVariable Long deviceId
    ) {

        return ResponseEntity.ok(
                adminDeviceService.getDevice(deviceId)
        );
    }

    @PatchMapping("/{deviceId}/status")
    public ResponseEntity<DeviceResponse> changeStatus(
            @PathVariable Long deviceId,
            @RequestParam String status
    ) {

        return ResponseEntity.ok(
                adminDeviceService.changeStatus(
                        deviceId,
                        status
                )
        );
    }

    @DeleteMapping("/{deviceId}")
    public ResponseEntity<Void> deleteDevice(
            @PathVariable Long deviceId
    ) {

        adminDeviceService.deleteDevice(deviceId);

        return ResponseEntity.noContent().build();
    }
}