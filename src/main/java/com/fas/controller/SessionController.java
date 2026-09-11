package com.fas.controller;

import com.fas.dto.SessionListDTO;
import com.fas.entity.Session;
import com.fas.security.RequirePermission;
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
    public SessionController(
            SessionService sessionService
    ) {
        this.sessionService =
                sessionService;
    }

    // =========================================================
    // إنشاء جلسة جديدة
    // POST /api/sessions
    // =========================================================

    @PostMapping
    @RequirePermission("SESSION_CREATE")
    public ResponseEntity<Session>
    saveSession(
            @RequestBody Session session
    ) {

        Session savedSession =
                sessionService.saveSession(
                        session
                );

        return new ResponseEntity<>(
                savedSession,
                HttpStatus.CREATED
        );
    }

    // =========================================================
    // جلب جميع الجلسات
    // GET /api/sessions
    // البيانات الأساسية فقط
    // =========================================================

    @GetMapping
    @RequirePermission("SESSION_LIST")
    public ResponseEntity<List<SessionListDTO>>
    allSessions() {

        return ResponseEntity.ok(
                sessionService.getAllSessions()
        );
    }

    // =========================================================
    // جلب جلسة بواسطة ID
    // GET /api/sessions/{id}
    // =========================================================

    @GetMapping("/{id}")
    @RequirePermission("SESSION_VIEW")
    public ResponseEntity<Session>
    getSessionById(
            @PathVariable Long id
    ) {

        Optional<Session> session =
                sessionService.getSessionById(
                        id
                );

        if (session.isPresent()) {

            /*
             * الإبقاء على منطق الكود الحالي.
             * تم حذف كود ObjectMapper التجريبي
             * لأنه لا يؤثر على Response.
             */

            return new ResponseEntity<>(
                    session.get(),
                    HttpStatus.OK
            );
        }

        return new ResponseEntity<>(
                HttpStatus.NOT_FOUND
        );
    }

    // =========================================================
    // تحديث جلسة
    // PUT /api/sessions/{id}
    // =========================================================

    @PutMapping("/{id}")
    @RequirePermission("SESSION_UPDATE")
    public ResponseEntity<?> updateSession(
            @PathVariable Long id,
            @RequestBody Session session
    ) {

        try {

            Session updated =
                    sessionService.updateSession(
                            id,
                            session
                    );

            return ResponseEntity.ok(
                    updated
            );

        } catch (RuntimeException e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(
                            HttpStatus.INTERNAL_SERVER_ERROR
                    )
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
    // DELETE /api/sessions/{id}
    // =========================================================

    @DeleteMapping("/{id}")
    @RequirePermission("SESSION_DELETE")
    public ResponseEntity<Void>
    deleteSession(
            @PathVariable Long id
    ) {

        try {

            sessionService.deleteSession(
                    id
            );

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