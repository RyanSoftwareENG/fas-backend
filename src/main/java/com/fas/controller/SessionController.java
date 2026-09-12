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
    // ط¥ظ†ط´ط§ط، ط¬ظ„ط³ط© ط¬ط¯ظٹط¯ط©
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
    // ط¬ظ„ط¨ ط¬ظ…ظٹط¹ ط§ظ„ط¬ظ„ط³ط§طھ
    // ط§ظ„ط¨ظٹط§ظ†ط§طھ ط§ظ„ط£ط³ط§ط³ظٹط© ظپظ‚ط·
    // =========================================================

    @GetMapping
    public ResponseEntity<List<SessionListDTO>> allSessions() {

        return ResponseEntity.ok(
                sessionService.getAllSessions()
        );
    }


    // =========================================================
    // ط¬ظ„ط¨ ط¬ظ„ط³ط© ط¨ظˆط§ط³ط·ط© ID
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<Session> getSessionById(@PathVariable Long id) {

        Optional<Session> session = sessionService.getSessionById(id);

        if (session.isPresent()) {
            try {
                // 1. ط¥ظ†ط´ط§ط، ظƒط§ط¦ظ† ObjectMapper ظ„طھط­ظˆظٹظ„ ط§ظ„ط¬ط§ظپط§ ط¥ظ„ظ‰ JSON
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

                // 2. طھظپط¹ظٹظ„ ط¯ط¹ظ… طھظˆط§ط±ظٹط® ط¬ط§ظپط§ 8 (LocalDate/LocalDateTime) ظ„طھط¬ظ†ط¨ ط£ط®ط·ط§ط، ط§ظ„طھط­ظˆظٹظ„
                mapper.findAndRegisterModules();

                // 3. طھط­ظˆظٹظ„ ط§ظ„ظƒط§ط¦ظ† ط¥ظ„ظ‰ ظ†طµ JSON ظ…ط±طھط¨ ظˆظ…ظ‚ط±ظˆط،
//                String jsonOutput = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(session.get());
//
                // 4. ط·ط¨ط§ط¹ط© ط§ظ„ظ†طھظٹط¬ط© ظپظٹ ط³ط·ط± ط§ظ„ط£ظˆط§ظ…ط± (Console)
//                System.out.println("================= JSON RESPONSE =================");
//                System.out.println(jsonOutput);
//                System.out.println("=================================================");

            } catch (Exception e) {
                System.err.println("ط®ط·ط£ ط£ط«ظ†ط§ط، طھط­ظˆظٹظ„ ط§ظ„ظƒط§ط¦ظ† ط¥ظ„ظ‰ JSON: " + e.getMessage());
            }

            return new ResponseEntity<>(session.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    // =========================================================
    // طھط­ط¯ظٹط« ط¬ظ„ط³ط©
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
                            "ظپط´ظ„ طھط­ط¯ظٹط« ط§ظ„ط¬ظ„ط³ط© ط±ظ‚ظ… "
                                    + id
                                    + ": "
                                    + e.getMessage()
                    );
        }
    }

    // =========================================================
    // ط­ط°ظپ ط¬ظ„ط³ط©
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