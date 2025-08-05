package com.medicon.server.controller;

import com.medicon.server.dto.auth.ChangePasswordRequest;
import com.medicon.server.dto.auth.LoginRequest;
import com.medicon.server.dto.auth.LoginResponse;
import com.medicon.server.dto.auth.signup.DoctorSignupRequest;
import com.medicon.server.dto.auth.signup.NurseSignupRequest;
import com.medicon.server.dto.auth.signup.PatientSignupRequest;
import com.medicon.server.dto.auth.signup.SignupRequest;
import com.medicon.server.dto.auth.signup.SignupResponse;
import com.medicon.server.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    //일반 회원가입( 사용자별 DB생성하지 않음)
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

    @PostMapping("/signup/doctor")
    public ResponseEntity<SignupResponse> signupDoctor(@RequestBody DoctorSignupRequest request) {
        return ResponseEntity.ok(authService.signupDoctor(request));
    }

    @PostMapping("/signup/nurse")
    public ResponseEntity<SignupResponse> signupNurse(@RequestBody NurseSignupRequest request) {
        return ResponseEntity.ok(authService.signupNurse(request));
    }

    @PostMapping("/signup/patient")
    public ResponseEntity<SignupResponse> signupPatient(@RequestBody PatientSignupRequest request) {
        return ResponseEntity.ok(authService.signupPatient(request));
    }
}
