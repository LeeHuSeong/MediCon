package com.medicon.server.controller;

import com.medicon.server.dto.ChangePasswordRequest;
import com.medicon.server.dto.LoginRequest;
import com.medicon.server.dto.LoginResponse;
import com.medicon.server.dto.SignupRequest;
import com.medicon.server.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<LoginResponse> signup(@RequestBody SignupRequest request) {
        return ResponseEntity.ok(authService.signup(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.loginWithIdToken(request.getIdToken()));
    }

    @PostMapping("/changepw")
    public ResponseEntity<LoginResponse> changepw(@RequestBody ChangePasswordRequest request) {
        return ResponseEntity.ok(authService.resetPw(request.getIdToken(), request.getNewPassword()));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<LoginResponse> delete(@RequestHeader("Authorization") String bearer) {
        String jwt = bearer.replace("Bearer ", "");
        String uid = authService.getUidFromJwt(jwt);
        return ResponseEntity.ok(authService.deleteUser(uid));
    }
}
