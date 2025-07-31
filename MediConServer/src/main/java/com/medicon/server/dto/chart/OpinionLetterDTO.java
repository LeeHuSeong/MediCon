package com.medicon.server.dto.chart;

public class OpinionLetterDTO { // 소견서입니돠
    private String opinion_id;     // 소견서 ID
    private String chart_id;       // 차트ID
    private String doctor_uid;     // 의사 UID
    private String patient_uid;    // 환자 UID
    private String note;          // 소견 내용
    private String visitDate;     // 방문 날짜

    public OpinionLetterDTO() {}

    public OpinionLetterDTO(String opinionId, ChartDTO chartDTO, String note) {
        this.opinion_id = opinionId;
        this.chart_id = chartDTO.getChart_id();
        this.doctor_uid = chartDTO.getDoctor_uid();
        this.patient_uid = chartDTO.getPatient_uid();
        this.note = note;
        this.visitDate = chartDTO.getVisit_date();
    }


    public String getOpinion_id() { return opinion_id; }
    public void setOpinion_id(String opinion_id) { this.opinion_id = opinion_id; }

    public String getChart_id() { return chart_id; }

    public String getDoctor_uid() { return doctor_uid; }

    public String getPatient_uid() { return patient_uid; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getVisitDate() { return visitDate; }
}