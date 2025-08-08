package com.medicon.server.dto.chart;

public class PrescriptionDTO { // 처방전 약 이거 API 우짜냐
    private String prescription_id;   // 처방전 ID
    private String chart_id;          // 차트 ID
    private String doctor_uid;        // 의사 UID
    private String patient_uid;       // 환자 UID
    private String medicines;        // 처방 약품 정보

    public PrescriptionDTO() {}

    public PrescriptionDTO(String prescriptionId, ChartDTO chartDTO, String medicines) {
        this.prescription_id = prescriptionId;
        this.chart_id = chartDTO.getChart_id();
        this.doctor_uid = chartDTO.getDoctor_uid();
        this.patient_uid = chartDTO.getPatient_uid();
        this.medicines = medicines;
    }

    public String getPrescription_id() { return prescription_id; }
    public void setPrescription_id(String prescription_id) { this.prescription_id = prescription_id; }

    public String getChart_id() { return chart_id; }

    public String getDoctor_uid() { return doctor_uid; }

    public String getPatient_uid() { return patient_uid; }

    public String getMedicines() { return medicines; }
    public void setMedicines(String medicines) { this.medicines = medicines; }
}