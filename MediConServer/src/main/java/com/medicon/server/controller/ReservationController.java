package com.medicon.server.controller;

import com.medicon.server.dao.reservation.ReservationDAO;
import com.medicon.server.dto.reservation.ReservationDTO;
import org.springframework.beans.factory.annotation.Autowired;
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
}