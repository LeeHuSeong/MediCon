package com.medicon.server.controller;

import com.medicon.server.dto.UserDTO;
import com.medicon.server.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
public class StaffController {

    @Autowired
    private StaffService staffService;

    /**
     * 의사, 간호사, 전체 직원 목록을 가져오는 통합 API
     * @param role doctor | nurse | all (기본값: all)
     * @return UserDTO를 상속한 DoctorDTO, NurseDTO 리스트
     */
    @GetMapping("/list")
    public List<UserDTO> getStaffList(@RequestParam(defaultValue = "all") String role) throws Exception {
        return staffService.getStaffList(role);
    }
}
