package com.medicon.server.dto;

public class NurseDTO extends UserDTO {

    private String nurse_id;
    private String department;
    private String rank;
    private String employee_number;

    public NurseDTO() {}

    public NurseDTO(String uid, String name, String phone, String email, String role, int authority, long createAt,
                    String nurse_id, String department, String rank, String employee_number) {
        super(uid, name, phone, email, role, authority, createAt);
        this.nurse_id = nurse_id;
        this.department = department;
        this.rank = rank;
        this.employee_number = employee_number;
    }

    public String getNurse_id() { return nurse_id; }
    public void setNurse_id(String nurse_id) { this.nurse_id = nurse_id; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getRank() { return rank; }
    public void setRank(String rank) { this.rank = rank; }

    public String getEmployee_number() { return employee_number; }
    public void setEmployee_number(String employee_number) { this.employee_number = employee_number; }
}
