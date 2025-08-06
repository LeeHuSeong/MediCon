package com.medicon.medicon.model;

public class LoginResult {
    private final boolean success;
    private final String message;
    private final String token;
    private final String uid;
    private final String email;
    private final int authority;

    public LoginResult(boolean success, String message, String token, String uid, String email, int authority) {
        this.success = success;
        this.message = message;
        this.token = token;
        this.uid = uid;
        this.email = email;
        this.authority = authority;
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public String getToken() { return token; }
    public String getUid() { return uid; }
    public String getEmail() { return email; }
    public int getAuthority() { return authority; }
}
