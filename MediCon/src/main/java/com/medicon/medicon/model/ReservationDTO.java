package com.medicon.medicon.model;

public class ReservationDTO {
    private String reservation_id;
    private String patient_uid;
    private String patient_id;
    private String date;
    private String time;
    private String department;

    public ReservationDTO() {}

    public ReservationDTO(String reservationId, String patientUid, String patientId, String date, String time, String department) {
        this.reservation_id = reservationId;
        this.patient_uid = patientUid;
        this.patient_id = patientId;
        this.date = date;
        this.time = time;
        this.department = department;
    }

    // Getters and setters
    public String getReservation_id() { return reservation_id; }
    public void setReservation_id(String reservation_id) { this.reservation_id = reservation_id; }

    public String getPatient_uid() { return patient_uid; }
    public void setPatient_uid(String patient_uid) { this.patient_uid = patient_uid; }

    public String getPatient_id() { return patient_id; }
    public void setPatient_id(String patient_id) { this.patient_id = patient_id; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
}
