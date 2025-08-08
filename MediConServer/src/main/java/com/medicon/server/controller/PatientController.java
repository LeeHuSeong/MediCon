package com.medicon.server.controller;

import com.medicon.server.dao.patient.PatientDAO;
import com.medicon.server.dao.patient.PatientDAOImpl;
import com.medicon.server.dto.user.PatientDTO;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/patient", produces = "application/json; charset=UTF-8")
public class PatientController {
    private final PatientDAO patientDAO = new PatientDAOImpl();

    // 전체 환자 목록 조회
    @GetMapping(value = "/all", produces = "application/json; charset=UTF-8")
    public ResponseEntity<List<PatientDTO>> getAllPatients() {
        try {
            List<PatientDTO> patients = patientDAO.findAllPatients();
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .body(patients);
        } catch (Exception e) {
            System.err.println("전체 환자 조회 실패: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    // 이름으로 환자 목록 조회 (동명이인 모두)
    @GetMapping(value = "/by-name/{name}", produces = "application/json; charset=UTF-8")
    public ResponseEntity<List<PatientDTO>> getPatientsByName(@PathVariable String name) {
        try {
            System.out.println("환자 이름 검색: " + name);
            List<PatientDTO> patients = patientDAO.findPatientByName(name);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .body(patients);
        } catch (Exception e) {
            System.err.println("환자 이름 검색 실패: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    // UID로 환자 단건 조회
    @GetMapping(value = "/{uid}", produces = "application/json; charset=UTF-8")
    public ResponseEntity<PatientDTO> getPatientByUid(@PathVariable String uid) {
        try {
            PatientDTO patient = patientDAO.findPatientByUid(uid);
            if (patient != null) {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .body(patient);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.err.println(" 환자 단건 조회 실패: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    // patient_id로 환자 단건 조회
    @GetMapping(value = "/by-patient-id/{patientId}", produces = "application/json; charset=UTF-8")
    public ResponseEntity<PatientDTO> getPatientByPatientId(@PathVariable String patientId) {
        try {
            PatientDTO patient = patientDAO.findPatientByPatientId(patientId);
            if (patient != null) {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .body(patient);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.err.println(" patient_id로 환자 조회 실패: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    // 신규 환자 등록
    @PostMapping(value = "/save",
            consumes = "application/json; charset=UTF-8",
            produces = "application/json; charset=UTF-8")
    public ResponseEntity<String> savePatient(@RequestBody PatientDTO patient) {
        try {
            System.out.println("환자 등록 요청: " + patient.getName());
            patientDAO.savePatient(patient);
            System.out.println("환자 등록 완료: " + patient.getName());
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .body("ok");
        } catch (Exception e) {
            System.err.println("환자 등록 실패: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .body("error: " + e.getMessage());
        }
    }

    // 환자 정보 수정 - 가장 중요한 부분!
    @PutMapping(value = "/update",
            consumes = "application/json; charset=UTF-8",
            produces = "application/json; charset=UTF-8")
    public ResponseEntity<String> updatePatient(@RequestBody PatientDTO patient) {
        try {
            System.out.println(" 환자 정보 수정 요청: " + patient.getName() + " (ID: " + patient.getPatient_id() + ")");

            // 수정 전 환자 정보 로그
            PatientDTO existingPatient = patientDAO.findPatientByUid(patient.getPatient_id());
            if (existingPatient != null) {
                System.out.println(" 기존 정보: " + existingPatient.getName());
            }

            // 실제 수정 실행
            patientDAO.updatePatient(patient);

            System.out.println(" 환자 정보 수정 완료: " + patient.getName());

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .body("ok");

        } catch (Exception e) {
            System.err.println(" 환자 정보 수정 실패: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .body("error: " + e.getMessage());
        }
    }

    // 환자 삭제
    @DeleteMapping(value = "/delete/{uid}", produces = "application/json; charset=UTF-8")
    public ResponseEntity<String> deletePatient(@PathVariable String uid) {
        try {
            System.out.println("🗑 환자 삭제 요청: " + uid);
            patientDAO.deletePatient(uid);
            System.out.println("환자 삭제 완료: " + uid);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .body("ok");
        } catch (Exception e) {
            System.err.println(" 환자 삭제 실패: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .body("error: " + e.getMessage());
        }
    }
}