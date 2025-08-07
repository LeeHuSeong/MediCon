package com.medicon.medicon.model;

public class UserDTO {

    protected String uid;
    protected String name;
    protected String phone;
    protected String email;
    protected String role;        // "의사", "간호사", "환자"
    protected int authority;      // 0=환자, 1=간호사, 2=의사
    protected long createAt;

    public UserDTO() {}

    public UserDTO(String uid, String name, String phone, String email, String role, int authority, long createAt) {
        this.uid = uid;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.role = role;
        this.authority = authority;
        this.createAt = createAt;
    }

    // Getters & Setters

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

    public long getCreateAt() { return createAt; }
    public void setCreateAt(long createAt) { this.createAt = createAt; }
}
