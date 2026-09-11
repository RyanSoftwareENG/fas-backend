package com.admin.controller;

import com.admin.dto.DeviceRegistrationRequest;
import com.admin.dto.DeviceRegistrationResponse;
import com.admin.service.DeviceService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/device")
public class DeviceController {

    private final DeviceService deviceService;

    public DeviceController(
            DeviceService deviceService
    ) {
        this.deviceService =
                deviceService;
    }

    @PostMapping("/register")
    public ResponseEntity<DeviceRegistrationResponse>
    register(
            @RequestBody DeviceRegistrationRequest request
    ) {

        return ResponseEntity.ok(
                deviceService.register(
                        request
                )
        );
    }
}
