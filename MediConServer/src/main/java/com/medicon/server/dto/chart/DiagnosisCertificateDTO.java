package com.medicon.server.dto.chart;

public class DiagnosisCertificateDTO { // 진로확인서 입니다
    private String certificate_id;   // 진료확인서 고유 ID
    private String chart_id;         // 차트ID
    private String department;      // 진료과
    private String doctor_uid;       // 의사 UID
    private String patient_uid;      // 환자 UID
    private String visitDate;       // 방문날짜

    public DiagnosisCertificateDTO() {}

    public DiagnosisCertificateDTO(String certificateId, ChartDTO chartDTO,
                                   String department) {
        this.certificate_id = certificateId;
        this.chart_id = chartDTO.getChart_id();
        this.department = department;
        this.doctor_uid = chartDTO.getDoctor_uid();
        this.patient_uid = chartDTO.getPatient_uid();
        this.visitDate = chartDTO.getVisit_date();
    }

    public String getCertificate_id() { return certificate_id; }
    public void setCertificate_id(String certificate_id) { this.certificate_id = certificate_id; }

    public String getChart_id() { return chart_id; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getDoctor_uid() { return doctor_uid; }

    public String getPatient_uid() { return patient_uid; }

    public String getVisitDate() { return visitDate; }
}