package com.medicon.medicon.service;

public class AuthService {
    public boolean login(String id, String pw) {
        return id.equals("admin") && pw.equals("1234");
    }
}