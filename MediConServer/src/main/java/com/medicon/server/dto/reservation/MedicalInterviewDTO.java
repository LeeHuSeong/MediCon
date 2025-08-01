package com.medicon.server.dto.reservation;

public class MedicalInterviewDTO {

    private String patient_id;
    private String reservation_id;
    private String symptoms;
    private String symptom_duration;
    private String current_medication;
    private String past_medical_history;
    private String allergy;

    public MedicalInterviewDTO() {}

    public MedicalInterviewDTO(ReservationDTO reservationDTO, String symptoms, String symptom_duration, String current_medication, String past_medical_history, String allergy) {
        this.patient_id = reservationDTO.getPatient_id();
        this.reservation_id = reservationDTO.getReservation_id();
        this.symptoms = symptoms;
        this.symptom_duration = symptom_duration;
        this.current_medication = current_medication;
        this.past_medical_history = past_medical_history;
        this.allergy = allergy;
    }

    public String getPatient_id() {
        return patient_id;
    }

    public String getReservation_id() {
        return reservation_id;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public String getSymptom_duration() {
        return symptom_duration;
    }

    public void setSymptom_duration(String symptom_duration) {
        this.symptom_duration = symptom_duration;
    }

    public String getCurrent_medication() {
        return current_medication;
    }

    public void setCurrent_medication(String current_medication) {
        this.current_medication = current_medication;
    }

    public String getPast_medical_history() {
        return past_medical_history;
    }

    public void setPast_medical_history(String past_medical_history) {
        this.past_medical_history = past_medical_history;
    }

    public String getAllergy() {
        return allergy;
    }

    public void setAllergy(String allergy) {
        this.allergy = allergy;
    }
}
