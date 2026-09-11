package com.fas.controller;

import com.fas.dto.ClientListDTO;
import com.fas.entity.Client;
import com.fas.entity.HealthData;
import com.fas.entity.LifeStyleInformation;
import com.fas.security.RequirePermission;
import com.fas.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService clientService;

    @Autowired
    public ClientController(
            ClientService clientService
    ) {
        this.clientService =
                clientService;
    }

    // =====================================================
    // إنشاء / حفظ بيانات عميل كاملة
    // POST /api/clients
    // =====================================================

    @PostMapping
    @RequirePermission("CLIENT_CREATE")
    public ResponseEntity<Client>
    saveFullClientData(
            @RequestBody Client client
    ) {

        Client savedClient =
                clientService.saveClient(
                        client
                );

        return new ResponseEntity<>(
                savedClient,
                HttpStatus.CREATED
        );
    }

    // =====================================================
    // جلب جميع العملاء
    // GET /api/clients
    // =====================================================

    @GetMapping
    @RequirePermission("CLIENT_LIST")
    public ResponseEntity<List<ClientListDTO>>
    allClients() {

        return ResponseEntity.ok(
                clientService.getAllClients()
        );
    }

    // =====================================================
    // جلب عميل بواسطة ID
    // GET /api/clients/{id}
    // =====================================================

    @GetMapping("/{id}")
    @RequirePermission("CLIENT_VIEW")
    public ResponseEntity<Client>
    getClientByID(
            @PathVariable Long id
    ) {

        Optional<Client> client =
                clientService.getClientById(
                        id
                );

        return client.map(
                        value ->
                                new ResponseEntity<>(
                                        value,
                                        HttpStatus.OK
                                )
                )
                .orElseGet(
                        () ->
                                new ResponseEntity<>(
                                        HttpStatus.NOT_FOUND
                                )
                );
    }

    // =====================================================
    // تحديث بيانات العميل الأساسية
    // PUT /api/clients/{id}
    // =====================================================

    @PutMapping("/{id}")
    @RequirePermission("CLIENT_UPDATE")
    public ResponseEntity<String>
    updateClient(
            @PathVariable Long id,
            @RequestBody Client client
    ) {

        try {

            clientService.updateClient(
                    id,
                    client
            );

            return new ResponseEntity<>(
                    "تم تحديث بيانات العميل بنجاح",
                    HttpStatus.OK
            );

        } catch (RuntimeException e) {

            return new ResponseEntity<>(
                    e.getMessage(),
                    HttpStatus.NOT_FOUND
            );
        }
    }

    // =====================================================
    // حذف عميل
    // DELETE /api/clients/{id}
    // =====================================================

    @DeleteMapping("/{id}")
    @RequirePermission("CLIENT_DELETE")
    public ResponseEntity<Void>
    deleteClient(
            @PathVariable Long id
    ) {

        try {

            clientService.deleteClient(
                    id
            );

            return new ResponseEntity<>(
                    HttpStatus.NO_CONTENT
            );

        } catch (Exception e) {

            return new ResponseEntity<>(
                    HttpStatus.NOT_FOUND
            );
        }
    }

    // =====================================================
    // تحديث البيانات الصحية
    // PUT /api/clients/{id}/health
    // =====================================================

    @PutMapping("/{id}/health")
    @RequirePermission("CLIENT_HEALTH_UPDATE")
    public ResponseEntity<Void>
    updateHealthData(
            @PathVariable Long id,
            @RequestBody HealthData healthData
    ) {

        try {

            clientService.updateHealthData(
                    id,
                    healthData
            );

            return new ResponseEntity<>(
                    HttpStatus.OK
            );

        } catch (RuntimeException e) {

            return new ResponseEntity<>(
                    HttpStatus.NOT_FOUND
            );
        }
    }

    // =====================================================
    // تحديث نمط الحياة
    // PUT /api/clients/{id}/lifestyle
    // =====================================================

    @PutMapping("/{id}/lifestyle")
    @RequirePermission("CLIENT_LIFESTYLE_UPDATE")
    public ResponseEntity<Void>
    updateLifeStyle(
            @PathVariable Long id,
            @RequestBody LifeStyleInformation lifeStyleInformation
    ) {

        try {

            clientService.updateLifeStyle(
                    id,
                    lifeStyleInformation
            );

            return new ResponseEntity<>(
                    HttpStatus.OK
            );

        } catch (RuntimeException e) {

            return new ResponseEntity<>(
                    HttpStatus.NOT_FOUND
            );
        }
    }

    // =====================================================
    // جلب البيانات الإضافية الشاملة للعميل
    // GET /api/clients/full/{id}
    // =====================================================

    @GetMapping("/full/{id}")
    @RequirePermission("CLIENT_FULL_VIEW")
    public ResponseEntity<Client>
    populateAdditionalData(
            @PathVariable Long id
    ) {

        Optional<Client> client =
                clientService.getFullClientDetails(
                        id
                );

        return client.map(
                        value ->
                                new ResponseEntity<>(
                                        value,
                                        HttpStatus.OK
                                )
                )
                .orElseGet(
                        () ->
                                new ResponseEntity<>(
                                        HttpStatus.NOT_FOUND
                                )
                );
    }
}