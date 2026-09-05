package com.fas.controller;

import com.fas.dto.SessionListDTO;
import com.fas.entity.Session;
import com.fas.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    private final SessionService sessionService;

    @Autowired
    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }


    // =========================================================
    // إنشاء جلسة جديدة
    // =========================================================

    @PostMapping
    public ResponseEntity<Session> saveSession(
            @RequestBody Session session) {

        Session savedSession =
                sessionService.saveSession(session);

        return new ResponseEntity<>(
                savedSession,
                HttpStatus.CREATED
        );
    }


    // =========================================================
    // جلب جميع الجلسات
    // البيانات الأساسية فقط
    // =========================================================

    @GetMapping
    public ResponseEntity<List<SessionListDTO>> allSessions() {

        return ResponseEntity.ok(
                sessionService.getAllSessions()
        );
    }


    // =========================================================
    // جلب جلسة بواسطة ID
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<Session> getSessionById(@PathVariable Long id) {

        Optional<Session> session = sessionService.getSessionById(id);

        if (session.isPresent()) {
            try {
                // 1. إنشاء كائن ObjectMapper لتحويل الجافا إلى JSON
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

                // 2. تفعيل دعم تواريخ جافا 8 (LocalDate/LocalDateTime) لتجنب أخطاء التحويل
                mapper.findAndRegisterModules();

                // 3. تحويل الكائن إلى نص JSON مرتب ومقروء
//                String jsonOutput = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(session.get());
//
                // 4. طباعة النتيجة في سطر الأوامر (Console)
//                System.out.println("================= JSON RESPONSE =================");
//                System.out.println(jsonOutput);
//                System.out.println("=================================================");

            } catch (Exception e) {
                System.err.println("خطأ أثناء تحويل الكائن إلى JSON: " + e.getMessage());
            }

            return new ResponseEntity<>(session.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    // =========================================================
    // تحديث جلسة
    // =========================================================

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSession(
            @PathVariable Long id,
            @RequestBody Session session) {

        try {

            Session updated =
                    sessionService.updateSession(
                            id,
                            session
                    );

            return ResponseEntity.ok(updated);

        } catch (RuntimeException e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            "فشل تحديث الجلسة رقم "
                                    + id
                                    + ": "
                                    + e.getMessage()
                    );
        }
    }

    // =========================================================
    // حذف جلسة
    // =========================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSession(
            @PathVariable Long id) {

        try {

            sessionService.deleteSession(id);

            return new ResponseEntity<>(
                    HttpStatus.NO_CONTENT
            );

        } catch (RuntimeException e) {

            return new ResponseEntity<>(
                    HttpStatus.NOT_FOUND
            );
        }
    }
}