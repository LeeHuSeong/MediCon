package com.medicon.server.dto;

public class PatientDTO {

    private String address;
    private String gender;
    private String patient_id;
    private String run;
    private String uid;
    private int authority;

    public PatientDTO() {

    }

    public PatientDTO(UserDTO userDTO, String run, String patient_id, String gender, String address) {
        this.uid = userDTO.getUid();
        this.run = run;
        this.patient_id = patient_id;
        this.gender = gender;
        this.address = address;
        this.authority = userDTO.getAuthority();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(String patient_id) {
        this.patient_id = patient_id;
    }

    public String getRun() {
        return run;
    }

    public void setRun(String run) {
        this.run = run;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getAuthority() {
        return authority;
    }

    public void setAuthority(int authority) {
        this.authority = authority;
    }
}
