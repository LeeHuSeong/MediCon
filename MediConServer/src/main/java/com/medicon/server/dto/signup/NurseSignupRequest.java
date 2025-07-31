package com.medicon.server.dto.signup;

import com.medicon.server.dto.auth.SignupRequest;

public class NurseSignupRequest extends SignupRequest {
    private String rank; // 간호사, 책임간호사 등
    private String department;


    public NurseSignupRequest() {}

    public NurseSignupRequest(String email, String password, String name, String phone, String rank,String department) {
        super(email, password, name, phone, "nurse");
        this.rank = rank;
        this.department = department;
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
