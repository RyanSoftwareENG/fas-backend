package com.fas.controller;

import com.fas.dto.ClientListDTO;
import com.fas.entity.Client;
import com.fas.entity.HealthData;
import com.fas.entity.LifeStyleInformation;
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
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    // إنشاء/حفظ بيانات عميل كاملة
    @PostMapping
    public ResponseEntity<Client> saveFullClientData(@RequestBody Client client) {
        Client savedClient = clientService.saveClient(client);
        return new ResponseEntity<>(savedClient, HttpStatus.CREATED);
    }

    // جلب جميع العملاء
    @GetMapping
    public ResponseEntity<List<ClientListDTO>> allClients() {

        return ResponseEntity.ok(
                clientService.getAllClients()
        );
    }

    // جلب عميل بواسطة المعرف ID
    @GetMapping("/{id}")
    public ResponseEntity<Client> getClientByID(@PathVariable Long id) {
        Optional<Client> client = clientService.getClientById(id);
        return client.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // تحديث بيانات العميل الأساسية
    @PutMapping("/{id}")
    public ResponseEntity<String> updateClient(@PathVariable Long id, @RequestBody Client client) {
        try {
            // نقوم بعملية التحديث في قاعدة البيانات
            clientService.updateClient(id, client);

            // نرجع رسالة نجاح بسيطة بدلاً من إرجاع الكائن لتجنب خطأ Jackson (Lazy Loading)
            return new ResponseEntity<>("تم تحديث بيانات العميل بنجاح", HttpStatus.OK);

        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
    // حذف عميل
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        try {
            clientService.deleteClient(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // تحديث البيانات الصحية للعميل
    @PutMapping("/{id}/health")
    public ResponseEntity<Void> updateHealthData(@PathVariable Long id, @RequestBody HealthData healthData) {
        try {
            clientService.updateHealthData(id, healthData);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // تحديث نمط الحياة للعميل
    @PutMapping("/{id}/lifestyle")
    public ResponseEntity<Void> updateLifeStyle(@PathVariable Long id, @RequestBody LifeStyleInformation lifeStyleInformation) {
        try {
            clientService.updateLifeStyle(id, lifeStyleInformation);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // جلب البيانات الإضافية الشاملة للعميل
    @GetMapping("/full/{id}")
    public ResponseEntity<Client> populateAdditionalData(@PathVariable Long id) {
        Optional<Client> client = clientService.getFullClientDetails(id);
        return client.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}