package com.medicon.medicon.model;

import javafx.beans.property.*;

public class BatchSalaryRow {

    private final StringProperty uid;
    private final StringProperty name;
    private final StringProperty role;
    private final StringProperty rank;
    private final LongProperty basePay;
    private final LongProperty bonus;
    private final StringProperty status;

    public BatchSalaryRow(String uid, String name, String role, String rank, long basePay) {
        this.uid = new SimpleStringProperty(uid);
        this.name = new SimpleStringProperty(name);
        this.role = new SimpleStringProperty(role);
        this.rank = new SimpleStringProperty(rank);
        this.basePay = new SimpleLongProperty(basePay);
        this.bonus = new SimpleLongProperty(0);
        this.status = new SimpleStringProperty("대기");
    }

    public String getUid() { return uid.get(); }
    public StringProperty uidProperty() { return uid; }

    public String getName() { return name.get(); }
    public StringProperty nameProperty() { return name; }

    public String getRole() { return role.get(); }
    public StringProperty roleProperty() { return role; }

    public String getRank() { return rank.get(); }
    public StringProperty rankProperty() { return rank; }

    public long getBasePay() { return basePay.get(); }
    public LongProperty basePayProperty() { return basePay; }

    public long getBonus() { return bonus.get(); }
    public void setBonus(long bonus) { this.bonus.set(bonus); }
    public LongProperty bonusProperty() { return bonus; }

    public String getStatus() { return status.get(); }
    public void setStatus(String status) { this.status.set(status); }
    public StringProperty statusProperty() { return status; }
}
