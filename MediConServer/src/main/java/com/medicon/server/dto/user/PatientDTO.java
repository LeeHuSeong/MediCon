package com.medicon.server.dto.user;

public class PatientDTO {
    // 기본 사용자 정보
    private String uid;
    private String name;
    private String phone;
    private String email;
    private String role;
    private int authority;
    private String createAt;

    // 환자 전용 정보
    private String patient_id;
    private String gender;
    private String address;
    private String rnn;//주민번호인데, 사용X

    public PatientDTO() {}

    // 생성자와 모든 getter/setter 추가
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public int getAuthority() { return authority; }
    public void setAuthority(int authority) { this.authority = authority; }

    public String getCreateAt() { return createAt; }
    public void setCreateAt(String createAt) { this.createAt = createAt; }

    public String getPatient_id() { return patient_id; }
    public void setPatient_id(String patient_id) { this.patient_id = patient_id; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getRnn() { return rnn; }
    public void setRnn(String rnn) { this.rnn = rnn; }

    @Override
    public String toString() {
        return name + " (" + gender + ")";
    }
}