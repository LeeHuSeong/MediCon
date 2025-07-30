package com.medicon.server.dto.signup;

public class SignupResponse {
    private boolean success;
    private String message;
    private String jwt;

    public SignupResponse() {}

    public SignupResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public SignupResponse(boolean success, String message, String jwt) {
        this.success = success;
        this.message = message;
        this.jwt = jwt;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getJwt() {
        return jwt;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
}
