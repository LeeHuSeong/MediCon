package com.medicon.server.dto.signup;

import com.medicon.server.dto.auth.SignupRequest;

public class DoctorSignupRequest extends SignupRequest {
    private String rank; // 인턴, 레지던트, 교수 등
    private String department;

    public DoctorSignupRequest() {}

    public DoctorSignupRequest(String email, String password, String name, String phone, String rank) {
        super(email, password, name, phone, "doctor");
        this.rank = rank;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
