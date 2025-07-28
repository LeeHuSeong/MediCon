package com.medicon.server.controller;

import com.medicon.server.service.Test_FirestoreReadService;
import com.medicon.server.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final Test_FirestoreReadService firestoreReadService;
    private final JwtUtil jwtUtil;

    public UserController(Test_FirestoreReadService firestoreReadService, JwtUtil jwtUtil) {
        this.firestoreReadService = firestoreReadService;
        this.jwtUtil = jwtUtil;
    }

    // 예: GET http://localhost:8080/api/user/2lsN3DgGI1UYyjqxylM3
    @GetMapping("/{uid}")
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable String uid) {
        try {
            Map<String, Object> user = firestoreReadService.getUserData(uid);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getUserByToken(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String uid = jwtUtil.getUidFromToken(token);
            Map<String, Object> user = firestoreReadService.getUserData(uid);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }
}
