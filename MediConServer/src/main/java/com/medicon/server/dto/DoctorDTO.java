package com.medicon.server.dto;

public class DoctorDTO extends UserDTO {

    private String doctor_id;
    private String department;
    private String rank;
    private String employee_number;

    public DoctorDTO() {}

    public DoctorDTO(String uid, String name, String phone, String email, String role, int authority, long createAt,
                     String doctor_id, String department, String rank, String employee_number) {
        super(uid, name, phone, email, role, authority, createAt);
        this.doctor_id = doctor_id;
        this.department = department;
        this.rank = rank;
        this.employee_number = employee_number;
    }

    public String getDoctor_id() { return doctor_id; }
    public void setDoctor_id(String doctor_id) { this.doctor_id = doctor_id; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getRank() { return rank; }
    public void setRank(String rank) { this.rank = rank; }

    public String getEmployee_number() { return employee_number; }
    public void setEmployee_number(String employee_number) { this.employee_number = employee_number; }
}
