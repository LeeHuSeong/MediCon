package com.medicon.server.controller;

import com.medicon.server.dto.common.ApiResponse;
import com.medicon.server.dto.salary.SalaryRecordRequest;
import com.medicon.server.dto.signup.DoctorSignupRequest;
import com.medicon.server.dto.signup.NurseSignupRequest;
import com.medicon.server.dto.signup.SignupResponse;
import com.medicon.server.dto.user.UserDTO;
import com.medicon.server.service.AuthService;
import com.medicon.server.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/staff")
public class StaffController {

    @Autowired
    private AuthService authService;

    @Autowired
    private StaffService staffService;

    /**
     * 의사, 간호사, 전체 직원 목록을 가져오는 통합 API
     */
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getStaffList(@RequestParam(defaultValue = "all") String role) {
        try {
            List<UserDTO> result = staffService.getStaffList(role);
            return ResponseEntity.ok(new ApiResponse<>(true, "직원 목록 조회 성공", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "직원 조회 실패: " + e.getMessage()));
        }
    }

    /**
     * 직원 등록 API (의사 전용)
     */
    @PostMapping("/register")
    public ResponseEntity<SignupResponse> registerStaff(@RequestBody Map<String, String> payload) {
        String role = payload.get("role");

        if ("doctor".equalsIgnoreCase(role)) {
            DoctorSignupRequest req = new DoctorSignupRequest(
                    payload.get("email"),
                    payload.get("password"),
                    payload.get("name"),
                    payload.get("phone"),
                    payload.get("rank"),
                    payload.get("department")
            );
            return ResponseEntity.ok(authService.signupDoctor(req));

        } else if ("nurse".equalsIgnoreCase(role)) {
            NurseSignupRequest req = new NurseSignupRequest(
                    payload.get("email"),
                    payload.get("password"),
                    payload.get("name"),
                    payload.get("phone"),
                    payload.get("rank"),
                    payload.get("department")
            );
            return ResponseEntity.ok(authService.signupNurse(req));
        }

        // 공통 에러 처리
        return ResponseEntity.badRequest()
                .body(new SignupResponse(false, "지원되지 않는 역할입니다.", null));
    }

    @PostMapping("/salary/{uid}")
    public ResponseEntity<ApiResponse<String>> addSalary(
            @PathVariable String uid,
            @RequestParam String role, // "doctor" or "nurse"
            @RequestBody SalaryRecordRequest request
    ) {
        try {
            staffService.saveSalary(uid, role, request);
            return ResponseEntity.ok(new ApiResponse<>(true, "급여 기록 저장 성공"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "저장 실패: " + e.getMessage()));
        }
    }
}
