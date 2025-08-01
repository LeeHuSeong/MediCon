package com.medicon.server.dto.reservation;

public class ReservationDTO {
    private String reservation_id;
    private String patient_id;
    private String date;
    private String time;

    public ReservationDTO() {}

    public ReservationDTO(String reservationId, String patientId, String date, String time) {
        this.reservation_id = reservationId;
        this.patient_id = patientId;
        this.date = date;
        this.time = time;
    }

    // Getters and setters
    public String getReservation_id() { return reservation_id; }
    public void setReservation_id(String reservation_id) { this.reservation_id = reservation_id; }

    public String getPatient_id() { return patient_id; }
    public void setPatient_id(String patient_id) { this.patient_id = patient_id; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
}
