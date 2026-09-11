package com.admin.controller;

import com.admin.dto.DeviceActivationCodeResponse;
import com.admin.security.RequirePermission;
import com.admin.service.DeviceActivationCodeService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/device-activation")
public class DeviceActivationCodeController {

    private final DeviceActivationCodeService service;

    public DeviceActivationCodeController(
            DeviceActivationCodeService service
    ) {
        this.service = service;
    }

    @PostMapping("/{subscriptionId}")
    @RequirePermission(
            "SUBSCRIPTION_MANAGE"
    )
    public ResponseEntity<DeviceActivationCodeResponse>
    generate(
            @PathVariable Long subscriptionId
    ) {

        return ResponseEntity.ok(
                service.generate(
                        subscriptionId
                )
        );
    }
}
