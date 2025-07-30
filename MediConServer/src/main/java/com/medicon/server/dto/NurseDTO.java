package com.medicon.server.dto;

public class NurseDTO {


    private String department;
    private String nurse_id;
    private String rank;
    private String uid;
    private String employee_number;
    private int authority;

    public NurseDTO() {

    }

    public NurseDTO(String department, String nurse_id, String rank, UserDTO userDTO, String employee_number) {
        this.department = department;
        this.nurse_id = nurse_id;
        this.rank = rank;
        this.uid = userDTO.getUid();
        this.employee_number = employee_number;
        this.authority = userDTO.getAuthority();
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getNurse_id() {
        return nurse_id;
    }

    public void setNurse_id(String nurse_id) {
        this.nurse_id = nurse_id;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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
