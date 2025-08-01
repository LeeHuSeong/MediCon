package com.medicon.server.dto.chart;

public class TestRequestFormDTO { // 검사의뢰서..
    private String request_id;     // 의뢰서 ID
    private String chart_id;       // 차트 ID
    private String doctor_uid;     // 의사 UID
    private String patient_uid;    // 환자 UID
    private String note;          // 비고/특이사항
    private String priority;      // 우선순위 (예: 긴급/일반)
    private String testDetail;    // 검사 상세명
    private String testType;      // 검사 종류
    private String visitDate;     // 방문 날짜

    public TestRequestFormDTO() {}

    public TestRequestFormDTO(String requestId, ChartDTO chartDTO, String note, String priority,
                              String testDetail, String testType) {
        this.request_id = requestId;
        this.chart_id = chartDTO.getChart_id();
        this.doctor_uid = chartDTO.getDoctor_uid();
        this.patient_uid = chartDTO.getPatient_uid();
        this.note = note;
        this.priority = priority;
        this.testDetail = testDetail;
        this.testType = testType;
        this.visitDate = chartDTO.getVisit_date();
    }

    public String getRequest_id() { return request_id; }
    public void setRequest_id(String request_id) { this.request_id = request_id; }

    public String getChart_id() { return chart_id; }

    public String getDoctor_uid() { return doctor_uid; }

    public String getPatient_uid() { return patient_uid; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getTestDetail() { return testDetail; }
    public void setTestDetail(String testDetail) { this.testDetail = testDetail; }

    public String getTestType() { return testType; }
    public void setTestType(String testType) { this.testType = testType; }

    public String getVisitDate() { return visitDate; }
}