package com.medicon.server.dto.salary;

public class SalaryResponse {
    private int year;          // 급여 지급 연도
    private int month;         // 급여 지급 월
    private long basePay;      // 기본급
    private long bonus;        // 수당
    private long pension;      // 국민연금 (9%)
    private long health;       // 건강보험
    private long employment;   // 고용보험
    private long incomeTax;    // 소득세
    private long localTax;     // 주민세
    private long netPay;       // 실수령액 (basePay + bonus - 공제 총합)

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public long getBasePay() {
        return basePay;
    }

    public void setBasePay(long basePay) {
        this.basePay = basePay;
    }

    public long getBonus() {
        return bonus;
    }

    public void setBonus(long bonus) {
        this.bonus = bonus;
    }

    public long getPension() {
        return pension;
    }

    public void setPension(long pension) {
        this.pension = pension;
    }

    public long getHealth() {
        return health;
    }

    public void setHealth(long health) {
        this.health = health;
    }

    public long getEmployment() {
        return employment;
    }

    public void setEmployment(long employment) {
        this.employment = employment;
    }

    public long getIncomeTax() {
        return incomeTax;
    }

    public void setIncomeTax(long incomeTax) {
        this.incomeTax = incomeTax;
    }

    public long getLocalTax() {
        return localTax;
    }

    public void setLocalTax(long localTax) {
        this.localTax = localTax;
    }

    public long getNetPay() {
        return netPay;
    }

    public void setNetPay(long netPay) {
        this.netPay = netPay;
    }
}
