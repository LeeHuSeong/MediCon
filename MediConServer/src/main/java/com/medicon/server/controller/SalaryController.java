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

    /**
     * 급여 조회 (단일 또는 전체)
     * GET /api/staff/salary/{uid}?role=doctor&year=2025&month=7
     */
    @GetMapping("/{uid}")
    public ResponseEntity<ApiResponse<Object>> getSalary(
            @PathVariable String uid,
            @RequestParam String role,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month
    ) {
        try {
            Object result;
            if (year != null && month != null) {
                result = salaryService.getSalaryByMonth(uid, role, year, month);
            } else {
                result = salaryService.getAllSalary(uid, role);
            }

            return ResponseEntity.ok(new ApiResponse<>(true, "급여 조회 성공", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "급여 조회 실패: " + e.getMessage()));
        }
    }

    @PutMapping("/{uid}")
    public ResponseEntity<ApiResponse<String>> editSalary(
            @PathVariable String uid,
            @RequestParam String yearMonth,
            @RequestParam String role,
            @RequestBody SalaryRecordRequest request
    ) {
        try {
            salaryService.editSalary(uid, role, yearMonth, request);
            return ResponseEntity.ok(new ApiResponse<>(true, "급여 수정 성공"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "급여 수정 실패: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{uid}")
    public ResponseEntity<ApiResponse<String>> deleteSalary(
            @PathVariable String uid,
            @RequestParam String yearMonth,
            @RequestParam String role
    ) {
        try {
            salaryService.deleteSalary(uid, role, yearMonth);
            return ResponseEntity.ok(new ApiResponse<>(true, "급여 삭제 성공"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "급여 삭제 실패: " + e.getMessage()));
        }
    }
}
