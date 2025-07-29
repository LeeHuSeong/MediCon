package com.medicon.medicon.service;

import static com.medicon.medicon.config.AppConfig.FIREBASE_WEB_API_KEY;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AuthService {

    public String loginAndGetJwt(String email, String password) throws Exception {
        // FirebaseAuthHelper 역할을 이 안에 직접 구현해도 됨 (간단용)
        String idToken = getFirebaseIdToken(email, password);

        // JwtLoginHelper 역할도 이 안에서 바로 처리해도 됨
        return requestJwtFromServer(idToken);
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
        URL url = new URL("http://localhost:8080/auth/login");
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

        // 디버깅용 전체 출력
        System.out.println("서버 응답 전체: " + response.toPrettyString());

        // 안전하게 token 추출
        JsonNode jwtNode = response.get("token");
        if (jwtNode == null) {
            throw new IllegalStateException("서버 응답에 'token' 필드가 없습니다.");
        }

        return jwtNode.asText();
    }

}
