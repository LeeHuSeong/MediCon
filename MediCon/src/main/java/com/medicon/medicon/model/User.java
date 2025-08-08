package com.medicon.medicon.model;

public class User {
    private String uid;
    private String name;
    private String role;  // "doctor", "nurse", "patient"
    private int authority;

    public User(String uid, String name, String role, String rank) {
        this.uid = uid;
        this.name = name;
        this.role = role;
        this.authority = authority;
    }

    public String getUid() { return uid; }
    public String getName() { return name; }
    public String getRole() { return role; }
    public int getauthority() { return authority; }

    @Override
    public String toString() {
        return name + " (" + uid + ")";
    }

    public int getAuthority() {
        return authority;
    }

    public void setAuthority(int authority) {
        this.authority = authority;
    }
}
