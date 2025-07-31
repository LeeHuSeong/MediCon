package com.medicon.server.controller;

import com.medicon.server.dto.common.ApiResponse;
import com.medicon.server.dto.salary.SalaryRecordRequest;
import com.medicon.server.service.SalaryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/staff/salary")
public class SalaryController {

    private final SalaryService salaryService;

    public SalaryController(SalaryService salaryService) {
        this.salaryService = salaryService;
    }

    /**
     * 급여 등록
     * POST /api/staff/salary/{uid}?role=doctor
     */
    @PostMapping("/{uid}")
    public ResponseEntity<ApiResponse<String>> saveSalary(
            @PathVariable String uid,
            @RequestParam String role,
            @RequestBody SalaryRecordRequest request
    ) {
        try {
            salaryService.saveSalary(uid, role, request);
            return ResponseEntity.ok(new ApiResponse<>(true, "급여 등록 성공"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "급여 등록 실패: " + e.getMessage()));
        }
    }
}
