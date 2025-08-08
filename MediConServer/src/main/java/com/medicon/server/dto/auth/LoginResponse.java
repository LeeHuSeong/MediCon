package com.medicon.server.dto.auth;

public class LoginResponse {
    private boolean success;
    private String message;
    private String token; // JWT
    private String uid;   // Firebase UID
    private String email; // 사용자 이메일
    private Integer authority;

    public LoginResponse(boolean success, String message, String token, String uid, String email, Integer authority) {
        this.success = success;
        this.message = message;
        this.token = token;
        this.uid = uid;
        this.email = email;
        this.authority = authority;
    }

    // 성공 or 실패용 오버로드
    public LoginResponse(boolean success, String message) {
        this(success, message, null, null, null,null);
    }

    public LoginResponse(boolean success, String message, String token) {
        this(success, message, token, null, null,null);
    }

    // Getter/Setter 모두 추가
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public String getToken() { return token; }
    public String getUid() { return uid; }
    public String getEmail() { return email; }
    public Integer getAuthority() { return authority; }

    public void setSuccess(boolean success) { this.success = success; }
    public void setMessage(String message) { this.message = message; }
    public void setToken(String token) { this.token = token; }
    public void setUid(String uid) { this.uid = uid; }
    public void setEmail(String email) { this.email = email; }
    public void setAuthority(Integer authority) { this.authority = authority; }
}
