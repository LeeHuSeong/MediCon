package com.medicon.server.dto;

public class PrescriptionDTO { // 처방전 약 이거 API 우짜냐
    private String prescriptionId;   // 처방전 ID
    private String chartId;          // 차트 ID
    private String doctorUid;        // 의사 UID
    private String patientUid;       // 환자 UID
    private String medicines;        // 처방 약품 정보

    public PrescriptionDTO() {}

    public PrescriptionDTO(String prescriptionId, ChartDTO chartDTO, String medicines) {
        this.prescriptionId = prescriptionId;
        this.chartId = chartDTO.getChart_id();
        this.doctorUid = chartDTO.getDoctor_uid();
        this.patientUid = chartDTO.getPatient_uid();
        this.medicines = medicines;
    }

    public String getPrescriptionId() { return prescriptionId; }
    public void setPrescriptionId(String prescriptionId) { this.prescriptionId = prescriptionId; }

    public String getChartId() { return chartId; }

    public String getDoctorUid() { return doctorUid; }

    public String getPatientUid() { return patientUid; }

    public String getMedicines() { return medicines; }
    public void setMedicines(String medicines) { this.medicines = medicines; }
}