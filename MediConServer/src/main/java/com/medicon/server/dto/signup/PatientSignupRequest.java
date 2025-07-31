package com.medicon.server.dto.signup;

import com.medicon.server.dto.auth.SignupRequest;

public class PatientSignupRequest extends SignupRequest {
    private String birthdate;
    private String gender;

    public PatientSignupRequest() {}

    public PatientSignupRequest(String email, String password, String name, String phone, String birthdate, String gender) {
        super(email, password, name, phone, "patient");
        this.birthdate = birthdate;
        this.gender = gender;
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
}
