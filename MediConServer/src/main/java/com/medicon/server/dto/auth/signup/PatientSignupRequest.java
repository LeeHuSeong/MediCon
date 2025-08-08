package com.medicon.server.dto.auth.signup;

public class PatientSignupRequest extends SignupRequest {
    private String birthdate;
    private String gender;
    private String address;
    private String rnn;

    public PatientSignupRequest() {}

    public PatientSignupRequest(String email, String password, String name, String phone, String birthdate, String gender,String address, String rnn) {
        super(email, password, name, phone, "patient");
        this.birthdate = birthdate;
        this.gender = gender;
        this.address = address;
        this.rnn = rnn;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRnn() {
        return rnn;
    }

    public void setRnn(String rnn) {
        this.rnn = rnn;
    }
}
