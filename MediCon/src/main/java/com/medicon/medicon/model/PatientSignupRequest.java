package com.medicon.medicon.model;

public class PatientSignupRequest {
    private String email;
    private String password;
    private String name;
    private String phone;
    private String role;
    private String birthdate;
    private String gender;
    private String address;
    private String rnn;

    public PatientSignupRequest() {}

    public PatientSignupRequest(String email, String password, String name, String phone, 
                               String birthdate, String gender, String address, String rnn) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.role = "patient";
        this.birthdate = birthdate;
        this.gender = gender;
        this.address = address;
        this.rnn = rnn;
    }

    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getBirthdate() { return birthdate; }
    public void setBirthdate(String birthdate) { this.birthdate = birthdate; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getRnn() { return rnn; }
    public void setRnn(String rnn) { this.rnn = rnn; }
} 