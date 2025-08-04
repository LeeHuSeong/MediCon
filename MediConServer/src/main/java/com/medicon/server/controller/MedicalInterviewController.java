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


    @GetMapping("/by-reservation")
    public List<MedicalInterviewDTO> getInterviewByReservation(
            @RequestParam String reservationId
    ) {
        return interviewService.findInterviewByReservationId(reservationId);
    }


    @GetMapping("/by-patient")
    public List<MedicalInterviewDTO> getInterviewByPatient(
            @RequestParam String patientId
    ) {
        return interviewService.findInterviewByPatientId(patientId);
    }
}