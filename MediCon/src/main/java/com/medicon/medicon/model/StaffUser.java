package com.medicon.medicon.model;

public class StaffUser {
    private String uid;
    private String name;
    private String role; // "doctor", "nurse"
    private String phone;
    private String rank;
    private String department;

    // 필요 시 추가 가능
    private String employee_number;
    private String email;
    private int authority;

    public StaffUser() {} // Gson용 기본 생성자
    public StaffUser(String uid, String name, String role, String rank) {
        this.uid = uid;
        this.name = name;
        this.role = role;
        this.rank = rank;
    }

    public String getUid() { return uid; }
    public String getName() { return name; }
    public String getRole() { return role; }
    public String getPhone() { return phone; }
    public String getRank() { return rank; }
    public String getDepartment() { return department; }

    public String getEmployee_number() { return employee_number; }
    public String getEmail() { return email; }
    public int getAuthority() { return authority; }

    public void setUid(String uid) { this.uid = uid; }
    public void setName(String name) { this.name = name; }
    public void setRole(String role) { this.role = role; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setRank(String rank) { this.rank = rank; }
    public void setDepartment(String department) { this.department = department; }

    public void setEmployee_number(String employee_number) { this.employee_number = employee_number; }
    public void setEmail(String email) { this.email = email; }
    public void setAuthority(int authority) { this.authority = authority; }

    @Override
    public String toString() {
        return name + " (" + uid + ")";
    }
}
