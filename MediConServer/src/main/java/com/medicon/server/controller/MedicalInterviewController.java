package com.medicon.server.controller;

import com.medicon.server.dto.reservation.MedicalInterviewDTO;
import com.medicon.server.service.MedicalInterviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interview")
public class MedicalInterviewController {

    private final MedicalInterviewService interviewService;

    @Autowired
    public MedicalInterviewController(MedicalInterviewService interviewService) {
        this.interviewService = interviewService;
    }

    //예약 ID 기준 문진 조회
    //GET /api/interview/by-reservation?reservationId=xxx
    @GetMapping("/by-reservation")
    public List<MedicalInterviewDTO> getInterviewByReservation(
            @RequestParam String reservationId
    ) {
        return interviewService.findInterviewByReservationId(reservationId);
    }


    //환자 ID기준 모든 문진 조회
    //GET /api/interview/by-patient?patientId=xxx
    @GetMapping("/by-patient")
    public List<MedicalInterviewDTO> getInterviewByPatient(
            @RequestParam String patientId
    ) {
        return interviewService.findInterviewByPatientId(patientId);
    }
}