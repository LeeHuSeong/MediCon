package com.medicon.server.dto.user;

public class PatientDTO extends UserDTO {

    private String patient_id;
    private String gender;
    private String address;
    private String run;

    public PatientDTO() {}

    public PatientDTO(String uid, String name, String phone, String email, String role, int authority, long createAt,
                      String patient_id, String gender, String address, String run) {
        super(uid, name, phone, email, role, authority, createAt);
        this.patient_id = patient_id;
        this.gender = gender;
        this.address = address;
        this.run = run;
    }

    public String getPatient_id() { return patient_id; }
    public void setPatient_id(String patient_id) { this.patient_id = patient_id; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getRun() { return run; }
    public void setRun(String run) { this.run = run; }
}
