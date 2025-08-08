package com.medicon.medicon.model;

public class SalaryResponse {
    private int year;
    private int month;
    private long basePay;
    private long bonus;
    private long pension;
    private long health;
    private long employment;
    private long incomeTax;
    private long localTax;
    private long netPay;
    private Object paidAt;

    public SalaryResponse() {}

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }

    public long getBasePay() { return basePay; }
    public void setBasePay(long basePay) { this.basePay = basePay; }

    public long getBonus() { return bonus; }
    public void setBonus(long bonus) { this.bonus = bonus; }

    public long getPension() { return pension; }
    public void setPension(long pension) { this.pension = pension; }

    public long getHealth() { return health; }
    public void setHealth(long health) { this.health = health; }

    public long getEmployment() { return employment; }
    public void setEmployment(long employment) { this.employment = employment; }

    public long getIncomeTax() { return incomeTax; }
    public void setIncomeTax(long incomeTax) { this.incomeTax = incomeTax; }

    public long getLocalTax() { return localTax; }
    public void setLocalTax(long localTax) { this.localTax = localTax; }

    public long getNetPay() { return netPay; }
    public void setNetPay(long netPay) { this.netPay = netPay; }

    public Object getPaidAt() { return paidAt; }
    public void setPaidAt(String paidAt) { this.paidAt = paidAt; }
}
