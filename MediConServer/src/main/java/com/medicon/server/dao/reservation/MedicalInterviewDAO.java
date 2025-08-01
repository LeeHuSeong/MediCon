package com.medicon.server.dao.reservation;

import com.medicon.server.dto.reservation.MedicalInterviewDTO;
import java.util.List;

public interface MedicalInterviewDAO {
    // 예약ID로 문진 조회
    List<MedicalInterviewDTO> findInterviewByReservationId(String reservationId);

    // 환자ID 문진 조회
    List<MedicalInterviewDTO> findInterviewByPatientId(String patientId);

    // 문진 등록
    void saveInterview(String reservationId, MedicalInterviewDTO interview);

    // 문진 수정
    void updateInterview(String reservationId, MedicalInterviewDTO interview);

    // 문진 삭제
    void deleteInterview(String reservationId, String interviewId);
}