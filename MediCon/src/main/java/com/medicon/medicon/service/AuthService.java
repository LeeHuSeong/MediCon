package com.medicon.medicon.service;

import static com.medicon.medicon.config.AppConfig.FIREBASE_WEB_API_KEY;
import static com.medicon.medicon.config.AppConfig.SERVER_BASE_URL;
import static com.medicon.medicon.config.AppConfig.LOGIN_ENDPOINT;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medicon.medicon.model.LoginResult;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AuthService {

    public String loginAndGetJwt(String email, String password) throws Exception {
        String idToken = getFirebaseIdToken(email, password);
        return requestJwtFromServer(idToken);
    }

    public LoginResult loginAndGetResult(String email, String password) throws Exception {
        String idToken = getFirebaseIdToken(email, password);
        return requestLoginResultFromServer(idToken);
    }

    private String getFirebaseIdToken(String email, String password) throws Exception {
        String firebaseUrl = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + FIREBASE_WEB_API_KEY;

        URL url = new URL(firebaseUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        String requestBody = String.format(
                "{\"email\":\"%s\",\"password\":\"%s\",\"returnSecureToken\":true}",
                email, password
        );

        try (OutputStream os = conn.getOutputStream()) {
            os.write(requestBody.getBytes());
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode response = mapper.readTree(conn.getInputStream());

        return response.get("idToken").asText();
    }

    private String requestJwtFromServer(String idToken) throws Exception {
        String fullUrl = SERVER_BASE_URL + LOGIN_ENDPOINT;

        URL url = new URL(fullUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        String requestBody = String.format("{\"idToken\":\"%s\"}", idToken);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(requestBody.getBytes());
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode response = mapper.readTree(conn.getInputStream());

        System.out.println("서버 응답 전체: " + response.toPrettyString());

        JsonNode jwtNode = response.get("token");
        if (jwtNode == null) {
            throw new IllegalStateException("서버 응답에 'token' 필드가 없습니다.");
        }

        return jwtNode.asText();
    }

    private LoginResult requestLoginResultFromServer(String idToken) throws Exception {
        String fullUrl = SERVER_BASE_URL + LOGIN_ENDPOINT;

        URL url = new URL(fullUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        String requestBody = String.format("{\"idToken\":\"%s\"}", idToken);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(requestBody.getBytes());
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode response = mapper.readTree(conn.getInputStream());

        System.out.println("서버 응답 전체: " + response.toPrettyString());

        boolean success = response.path("success").asBoolean(false);
        String message = response.path("message").asText();
        String token = response.path("token").asText(null);
        String uid = response.path("uid").asText(null);
        String email = response.path("email").asText(null);
        int authority = response.hasNonNull("authority") ? response.path("authority").asInt() : -1;

        return new LoginResult(success, message, token, uid, email, authority);
    }
}