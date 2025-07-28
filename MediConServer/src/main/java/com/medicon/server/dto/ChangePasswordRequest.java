package com.medicon.server.dto;

public class ChangePasswordRequest {
    private String idToken;
    private String newPassword;

    public ChangePasswordRequest() {}

    public ChangePasswordRequest(String idToken, String newPassword) {
        this.idToken = idToken;
        this.newPassword = newPassword;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
