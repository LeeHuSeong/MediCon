package com.medicon.server.dto;

public class DiagnosisCertificateDTO { // 진로확인서 입니다
    private String certificateId;   // 진료확인서 고유 ID
    private String chartId;         // 차트ID
    private String department;      // 진료과
    private String doctorUid;       // 의사 UID
    private String patientUid;      // 환자 UID
    private String visitDate;       // 방문날짜

    public DiagnosisCertificateDTO() {}

    public DiagnosisCertificateDTO(String certificateId, ChartDTO chartDTO,
                                   String department) {
        this.certificateId = certificateId;
        this.chartId = chartDTO.getChart_id();
        this.department = department;
        this.doctorUid = chartDTO.getDoctor_uid();
        this.patientUid = chartDTO.getPatient_uid();
        this.visitDate = chartDTO.getVisit_date();
    }

    public String getCertificateId() { return certificateId; }
    public void setCertificateId(String certificateId) { this.certificateId = certificateId; }

    public String getChartId() { return chartId; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getDoctorUid() { return doctorUid; }

    public String getPatientUid() { return patientUid; }

    public String getVisitDate() { return visitDate; }
}