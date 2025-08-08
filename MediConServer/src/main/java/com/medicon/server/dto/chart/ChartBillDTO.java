package com.medicon.server.dto.chart;

public class ChartBillDTO { // 진로비입니다
    private String bill_id;
    private String chart_id;     // 차트ID
    private String patient_uid;  // 환자ID
    private String amount;      // 진료비
    private boolean isPaid;     // 결제 여부
    private String paidAt;      // 결제 날짜

    public ChartBillDTO() {}


    public ChartBillDTO(String billId, ChartDTO chartDTO,
                        String amount, boolean isPaid, String paidAt) {
        this.bill_id = billId;
        this.chart_id = chartDTO.getChart_id();
        this.patient_uid = chartDTO.getPatient_uid();
        this.amount = amount;
        this.isPaid = isPaid;
        this.paidAt = paidAt;
    }

    public String getBill_id() {
        return bill_id;
    }

    public void setBill_id(String bill_id) {
        this.bill_id = bill_id;
    }

    public String getChart_id() {
        return chart_id;
    }

    public String getPatient_uid() {
        return patient_uid;
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
