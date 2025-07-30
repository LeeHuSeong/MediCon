package com.medicon.server.dto;

import javax.print.Doc;

public class DoctorDTO {

    private String uid;
    private String department;
    private String doctor_id;
    private String rank;
    private String employee_number;
    private int authority;

    public DoctorDTO() {

    }

    public DoctorDTO(UserDTO userDTO, String department, String doctor_id, String rank, String employee_number) {
        this.uid = userDTO.getUid();
        this.department = department;
        this.doctor_id = doctor_id;
        this.rank = rank;
        this.employee_number = employee_number;
        this.authority = userDTO.getAuthority();
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDoctor_id() {
        return doctor_id;
    }

    public void setDoctor_id(String doctor_id) {
        this.doctor_id = doctor_id;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getEmployee_number() {
        return employee_number;
    }

    public void setEmployee_number(String employee_number) {
        this.employee_number = employee_number;
    }

    public int getAuthority() {
        return authority;
    }

    public void setAuthority(int authority) {
        this.authority = authority;
    }
}
