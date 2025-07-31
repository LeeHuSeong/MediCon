package com.medicon.medicon.model;

public class StaffUser {
    private String uid;
    private String name;
    private String role; // "doctor", "nurse"
    private String rank;

    public StaffUser(String uid, String name, String role, String rank) {
        this.uid = uid;
        this.name = name;
        this.role = role;
        this.rank = rank;
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public String getRank() {
        return rank;
    }

    @Override
    public String toString() {
        return name + " (" + uid + ")";
    }
}
