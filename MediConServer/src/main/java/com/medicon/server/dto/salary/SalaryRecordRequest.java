package com.medicon.server.dto.salary;

public class SalaryRecordRequest {
    private int year;
    private int month;
    private long basePay;
    private long bonus;

    public SalaryRecordRequest() {}

    public SalaryRecordRequest(int year, int month, long basePay, long bonus) {
        this.year = year;
        this.month = month;
        this.basePay = basePay;
        this.bonus = bonus;
    }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }

    public long getBasePay() { return basePay; }
    public void setBasePay(long basePay) { this.basePay = basePay; }

    public long getBonus() { return bonus; }
    public void setBonus(long bonus) { this.bonus = bonus; }
}
