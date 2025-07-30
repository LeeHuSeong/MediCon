package com.medicon.server.dto;

public class UserDTO {
    private int authority; // 0: 환자, 1: 간호사, 2: 의사
    private String email;
    private String login_password;
    private String name;
    private String phone;
    private String role; // 환자 or 간호사 or 의사
    private String uid;
    private long createAt;

    public UserDTO() {

    }

    public UserDTO(int authority, String email, String login_password, String name, String phone, String role, String uid, long createAt) {
        this.authority = authority;
        this.email = email;
        this.login_password = login_password;
        this.name = name;
        this.phone = phone;
        this.role = role;
        this.uid = uid;
        this.createAt = createAt;
    }

    public int getAuthority() {
        return authority;
    }

    public void setAuthority(int authority) {
        this.authority = authority;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLogin_password() {
        return login_password;
    }

    public void setLogin_password(String login_password) {
        this.login_password = login_password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }
}
