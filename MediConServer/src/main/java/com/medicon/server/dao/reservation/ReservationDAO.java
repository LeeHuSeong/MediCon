package com.medicon.server.dao.reservation;

import com.medicon.server.dto.reservation.ReservationDTO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationDAO {
    void saveReservation(ReservationDTO reservation);   // 예약 등록
    ReservationDTO findReservationById(String reservationId);     // 예약 ID로 단건 조회
    List<ReservationDTO> findReservationByPatientId(String patientId); // 환자ID로 예약 목록 조회
    List<ReservationDTO> findReservationByDate(String date);      // 날짜로 예약 목록 조회
    void updateReservation(ReservationDTO reservation);           // 예약 수정
    void deleteReservation(String reservationId);                 // 예약 삭제
}
