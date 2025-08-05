package com.medicon.server.controller;

import com.medicon.server.dto.auth.signup.DoctorSignupRequest;
import com.medicon.server.dto.auth.signup.NurseSignupRequest;
import com.medicon.server.dto.auth.signup.PatientSignupRequest;
import com.medicon.server.dto.auth.signup.SignupResponse;
import com.medicon.server.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/register")
public class RegisterController {

    @Autowired
    private AuthService authService;

    @PostMapping("/doctor")
    public ResponseEntity<SignupResponse> registerDoctor(@RequestBody DoctorSignupRequest request) {
        return ResponseEntity.ok(authService.signupDoctor(request));
    }

    @PostMapping("/nurse")
    public ResponseEntity<SignupResponse> registerNurse(@RequestBody NurseSignupRequest request) {
        return ResponseEntity.ok(authService.signupNurse(request));
    }

    @PostMapping("/patient")
    public ResponseEntity<SignupResponse> registerPatient(@RequestBody PatientSignupRequest request) {
        return ResponseEntity.ok(authService.signupPatient(request));
    }
}
