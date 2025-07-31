package com.medicon.server.dto;

public class OpinionLetterDTO { // 소견서입니돠
    private String opinionId;     // 소견서 ID
    private String chartId;       // 차트ID
    private String doctorUid;     // 의사 UID
    private String patientUid;    // 환자 UID
    private String note;          // 소견 내용
    private String visitDate;     // 방문 날짜

    public OpinionLetterDTO() {}

    public OpinionLetterDTO(String opinionId, ChartDTO chartDTO, String note) {
        this.opinionId = opinionId;
        this.chartId = chartDTO.getChart_id();
        this.doctorUid = chartDTO.getDoctor_uid();
        this.patientUid = chartDTO.getPatient_uid();
        this.note = note;
        this.visitDate = chartDTO.getVisit_date();
    }


    public String getOpinionId() { return opinionId; }
    public void setOpinionId(String opinionId) { this.opinionId = opinionId; }

    public String getChartId() { return chartId; }

    public String getDoctorUid() { return doctorUid; }

    public String getPatientUid() { return patientUid; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getVisitDate() { return visitDate; }
}