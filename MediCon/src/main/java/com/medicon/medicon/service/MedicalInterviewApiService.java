package com.medicon.medicon.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medicon.medicon.model.MedicalInterviewDTO;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MedicalInterviewApiService {
    
    private static final String BASE_URL = "http://localhost:8080/api/interview";
    private final ObjectMapper objectMapper;
    
    public MedicalInterviewApiService() {
        this.objectMapper = new ObjectMapper();
    }
    
    // 예약ID로 문진 조회 - GET /api/interview/by-reservation?uid=xxx&patientId=xxx&reservationId=xxx
    public CompletableFuture<List<MedicalInterviewDTO>> getInterviewByReservationAsync(String uid, String patientId, String reservationId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String urlString = BASE_URL + "/by-reservation" +
                        "?uid=" + URLEncoder.encode(uid, "UTF-8") +
                        "&patientId=" + URLEncoder.encode(patientId, "UTF-8") +
                        "&reservationId=" + URLEncoder.encode(reservationId, "UTF-8");
                
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");
                
                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    
                    List<MedicalInterviewDTO> interviews = objectMapper.readValue(response.toString(), new TypeReference<List<MedicalInterviewDTO>>() {});
                    System.out.println("✅ 문진 기록 조회 성공: " + interviews.size() + "개");
                    return interviews;
                } else {
                    System.err.println("❌ 문진 기록 조회 실패: " + responseCode);
                    return new ArrayList<>();
                }
            } catch (Exception e) {
                System.err.println("❌ 문진 기록 조회 오류: " + e.getMessage());
                e.printStackTrace();
                return new ArrayList<>();
            }
        });
    }
    
    // 환자ID로 모든 문진 조회 - GET /api/interview/by-patient?uid=xxx&patientId=xxx
    public CompletableFuture<List<MedicalInterviewDTO>> getInterviewByPatientAsync(String uid, String patientId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String urlString = BASE_URL + "/by-patient" +
                        "?uid=" + URLEncoder.encode(uid, "UTF-8") +
                        "&patientId=" + URLEncoder.encode(patientId, "UTF-8");
                
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");
                
                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    
                    List<MedicalInterviewDTO> interviews = objectMapper.readValue(response.toString(), new TypeReference<List<MedicalInterviewDTO>>() {});
                    System.out.println("✅ 환자 문진 기록 조회 성공: " + interviews.size() + "개");
                    return interviews;
                } else {
                    System.err.println("❌ 환자 문진 기록 조회 실패: " + responseCode);
                    return new ArrayList<>();
                }
            } catch (Exception e) {
                System.err.println("❌ 환자 문진 기록 조회 오류: " + e.getMessage());
                e.printStackTrace();
                return new ArrayList<>();
            }
        });
    }
}