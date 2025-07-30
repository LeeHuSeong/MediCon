package com.medicon.server.dto;

public class TestRequestFormDTO { // 검사의뢰서..
    private String requestId;     // 의뢰서 ID
    private String chartId;       // 차트 ID
    private String doctorUid;     // 의사 UID
    private String patientUid;    // 환자 UID
    private String note;          // 비고/특이사항
    private String priority;      // 우선순위 (예: 긴급/일반)
    private String testDetail;    // 검사 상세명
    private String testType;      // 검사 종류
    private String visitDate;     // 방문 날짜

    public TestRequestFormDTO() {}

    public TestRequestFormDTO(String requestId, ChartDTO chartDTO, String note, String priority,
                              String testDetail, String testType) {
        this.requestId = requestId;
        this.chartId = chartDTO.getChart_id();
        this.doctorUid = chartDTO.getDoctor_uid();
        this.patientUid = chartDTO.getPatient_uid();
        this.note = note;
        this.priority = priority;
        this.testDetail = testDetail;
        this.testType = testType;
        this.visitDate = chartDTO.getVisit_date();
    }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getChartId() { return chartId; }

    public String getDoctorUid() { return doctorUid; }

    public String getPatientUid() { return patientUid; }

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