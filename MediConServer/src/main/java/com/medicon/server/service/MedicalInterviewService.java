package com.medicon.server.service;

import com.medicon.server.dao.reservation.MedicalInterviewDAO;
import com.medicon.server.dto.reservation.MedicalInterviewDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicalInterviewService {

    private final MedicalInterviewDAO interviewDAO;

    @Autowired
    public MedicalInterviewService(MedicalInterviewDAO interviewDAO) {
        this.interviewDAO = interviewDAO;
    }

    public List<MedicalInterviewDTO> findInterviewByReservationId(String reservationId) {
        return interviewDAO.findInterviewByReservationId(reservationId);
    }

    public List<MedicalInterviewDTO> findInterviewByPatientId(String patientId) {
        return interviewDAO.findInterviewByPatientId(patientId);
    }

    public MedicalInterviewDTO saveInterview(MedicalInterviewDTO interviewDTO) {
        return interviewDAO.saveInterview(interviewDTO);
    }
}