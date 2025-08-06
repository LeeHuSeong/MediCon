package com.medicon.medicon.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medicon.medicon.config.AppConfig;
import com.medicon.medicon.model.PatientSignupRequest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

public class RegisterApiService {

    private static final String BASE_URL = AppConfig.SERVER_BASE_URL + "/api/register";
    private final ObjectMapper objectMapper;

    public RegisterApiService() {
        this.objectMapper = new ObjectMapper();
    }

    // 환자 회원가입 - POST /api/register/patient
    public CompletableFuture<Boolean> registerPatient(PatientSignupRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(BASE_URL + "/patient");
                System.out.println("🌐 요청 URL: " + url.toString());
                
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);

                String jsonInputString = objectMapper.writeValueAsString(request);
                System.out.println("📤 전송 데이터: " + jsonInputString);

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                System.out.println("📡 응답 코드: " + responseCode);

                if (responseCode >= 200 && responseCode < 300) {
                    // 성공 응답 읽기
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                        StringBuilder response = new StringBuilder();
                        String inputLine;
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        String responseBody = response.toString();
                        System.out.println("✅ 환자 회원가입 성공: " + request.getName());
                        System.out.println("📧 이메일: " + request.getEmail());
                        System.out.println("📄 응답 내용: " + responseBody);
                        
                        // 응답 내용에서 success 필드 확인
                        if (responseBody.contains("\"success\":true")) {
                            return true;
                        } else {
                            System.err.println("❌ 서버에서 실패 응답: " + responseBody);
                            return false;
                        }
                    }
                } else {
                    // 에러 응답 읽기
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream()))) {
                        String errorResponse = br.lines().collect(java.util.stream.Collectors.joining("\n"));
                        System.err.println("❌ 환자 회원가입 실패: " + responseCode);
                        System.err.println("🔍 에러 내용: " + errorResponse);
                        System.err.println("📧 이메일: " + request.getEmail());
                        System.err.println("👤 이름: " + request.getName());
                    }
                    return false;
                }
            } catch (Exception e) {
                System.err.println("❌ 환자 회원가입 중 오류: " + e.getMessage());
                System.err.println("📧 이메일: " + request.getEmail());
                System.err.println("👤 이름: " + request.getName());
                e.printStackTrace();
                return false;
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        });
    }
} 