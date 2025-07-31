package com.medicon.server.dto;

public class ChartBillDTO { // 진로비입니다
    private String billId;
    private String chartId;     // 차트ID
    private String patientUid;  // 환자ID
    private String amount;      // 진료비
    private boolean isPaid;     // 결제 여부
    private String paidAt;      // 결제 날짜

    public ChartBillDTO() {}


    public ChartBillDTO(String billId, ChartDTO chartDTO,
                        String amount, boolean isPaid, String paidAt) {
        this.billId = billId;
        this.chartId = chartDTO.getChart_id();
        this.patientUid = chartDTO.getPatient_uid();
        this.amount = amount;
        this.isPaid = isPaid;
        this.paidAt = paidAt;
    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public String getChartId() {
        return chartId;
    }

    public String getPatientUid() {
        return patientUid;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    public String getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(String paidAt) {
        this.paidAt = paidAt;
    }
}
