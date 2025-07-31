package com.medicon.server.dto;

import com.medicon.server.dto.user.DoctorDTO;
import com.medicon.server.dto.user.PatientDTO;

public class ChartDTO {

    private String chart_id;       // 차트 고유 ID
    private String doctor_uid;     // 의사 UID
    private String patient_uid;    // 환자 UID
    private String diagnosis;      // 진단명
    private String symptoms;       // 증상
    private String note;           // 비고
    private String visit_date;     // 방문 날짜
    private String visit_time;     // 방문 시간

    public ChartDTO() {

    }

    public ChartDTO(String chart_id, String doctor_uid, String patient_uid, String diagnosis, String symptoms,
                    String note, String visit_date, String visit_time) {
        this.chart_id = chart_id;
        this.doctor_uid = doctor_uid;
        this.patient_uid = patient_uid;
        this.diagnosis = diagnosis;
        this.symptoms = symptoms;
        this.note = note;
        this.visit_date = visit_date;
        this.visit_time = visit_time;
    }

    public ChartDTO(String chart_id, DoctorDTO doctorDTO, PatientDTO patientDTO, String diagnosis, String symptoms,
                    String note, String visit_date, String visit_time) {

        this.chart_id = chart_id;
        this.doctor_uid = doctorDTO.getUid();
        this.patient_uid = patientDTO.getUid();
        this.diagnosis = diagnosis;
        this.symptoms = symptoms;
        this.note = note;
        this.visit_date = visit_date;
        this.visit_time = visit_time;
    }

    public String getChart_id() {
        return chart_id;
    }

    public void setChart_id(String chart_id) {
        this.chart_id = chart_id;
    }

    public String getDoctor_uid() {
        return doctor_uid;
    }

    public String getPatient_uid() {
        return patient_uid;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getVisit_date() {
        return visit_date;
    }

    public void setVisit_date(String visit_date) {
        this.visit_date = visit_date;
    }

    public String getVisit_time() {
        return visit_time;
    }

    public void setVisit_time(String visit_time) {
        this.visit_time = visit_time;
    }
}