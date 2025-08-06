package com.medicon.server.controller;

import com.medicon.server.dao.reservation.ReservationDAO;
import com.medicon.server.dto.reservation.ReservationDTO;
import com.medicon.server.dto.common.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservation")
public class ReservationController {

    private final ReservationDAO reservationDAO;

    @Autowired
    public ReservationController(ReservationDAO reservationDAO) {
        this.reservationDAO = reservationDAO;
    }

    @GetMapping("/by-patient")
    public List<ReservationDTO> getReservationsByPatientId(@RequestParam String patientId) {
        List<ReservationDTO> reservations = reservationDAO.findReservationByPatientId(patientId);
        return reservations;
    }

    @GetMapping("/by-date")
    public List<ReservationDTO> getReservationsByDate(@RequestParam String date) {
        List<ReservationDTO> reservations = reservationDAO.findReservationByDate(date);
        return reservations;
    }

    @PostMapping("/save")
    public ResponseEntity<ApiResponse> saveReservation(@RequestBody ReservationDTO reservationDTO) {
        try {
            ReservationDTO savedReservation = reservationDAO.saveReservation(reservationDTO);
            return ResponseEntity.ok(new ApiResponse(true, "예약이 성공적으로 저장되었습니다.", savedReservation));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "예약 저장 중 오류가 발생했습니다: " + e.getMessage(), null));
        }
    }
}