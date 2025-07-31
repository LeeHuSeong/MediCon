package com.medicon.server.controller;

import com.medicon.server.dao.patient.PatientDAO;
import com.medicon.server.dao.patient.PatientDAOImpl;
import com.medicon.server.dto.user.PatientDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patient")
public class PatientController {
    private final PatientDAO patientDAO = new PatientDAOImpl();

    // 전체 환자 목록 조회
    @GetMapping("/all")
    public List<PatientDTO> getAllPatients() {
        return patientDAO.findAllPatients();
    }

    // 이름으로 환자 목록 조회 (동명이인 모두)
    @GetMapping("/by-name/{name}")
    public List<PatientDTO> getPatientsByName(@PathVariable String name) {
        return patientDAO.findPatientByName(name);
    }

    // UID로 환자 단건 조회
    @GetMapping("/{uid}")
    public PatientDTO getPatientByUid(@PathVariable String uid) {
        return patientDAO.findPatientByUid(uid);
    }

    // 신규 환자 등록
    @PostMapping("/save")
    public String savePatient(@RequestBody PatientDTO patient) {
        patientDAO.savePatient(patient);
        return "ok";
    }

    // 환자 정보 수정
    @PutMapping("/update")
    public String updatePatient(@RequestBody PatientDTO patient) {
        patientDAO.updatePatient(patient);
        return "ok";
    }

    // 환자 삭제
    @DeleteMapping("/delete/{uid}")
    public String deletePatient(@PathVariable String uid) {
        patientDAO.deletePatient(uid);
        return "ok";
    }
}