package com.medicon.medicon.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medicon.medicon.model.PatientDTO;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PatientApiService {
    
    private static final String BASE_URL = "http://localhost:8080/api/patient";
    private final ObjectMapper objectMapper;
    
    public PatientApiService() {
        this.objectMapper = new ObjectMapper();
    }
    
    // 전체 환자 목록 조회 - GET /api/patient/all
    public CompletableFuture<List<PatientDTO>> getAllPatientsAsync() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL(BASE_URL + "/all");
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
                    
                    List<PatientDTO> patients = objectMapper.readValue(response.toString(), new TypeReference<List<PatientDTO>>() {});
                    System.out.println("✅ 전체 환자 조회 성공: " + patients.size() + "명");
                    return patients;
                } else {
                    System.err.println("❌ 전체 환자 조회 실패: " + responseCode);
                    return new ArrayList<>();
                }
            } catch (Exception e) {
                System.err.println("❌ 네트워크 오류: " + e.getMessage());
                e.printStackTrace();
                return new ArrayList<>();
            }
        });
    }
    
    // 이름으로 환자 검색 - GET /api/patient/by-name/{name}
    public CompletableFuture<List<PatientDTO>> getPatientsByNameAsync(String name) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String encodedName = URLEncoder.encode(name, StandardCharsets.UTF_8);
                URL url = new URL(BASE_URL + "/by-name/" + encodedName);
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

                    List<PatientDTO> patients = objectMapper.readValue(response.toString(), new TypeReference<List<PatientDTO>>() {});
                    System.out.println("✅ 환자 검색 성공: " + patients.size() + "명 (검색어: " + name + ")");
                    return patients;
                } else {
                    System.err.println("❌ 환자 검색 실패: " + responseCode);
                    return new ArrayList<>();
                }
            } catch (Exception e) {
                System.err.println("❌ 네트워크 오류: " + e.getMessage());
                e.printStackTrace();
                return new ArrayList<>();
            }
        });
    }
    
    // UID로 환자 단건 조회 - GET /api/patient/{uid}
    public CompletableFuture<PatientDTO> getPatientByUidAsync(String uid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL(BASE_URL + "/" + uid);
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
                    
                    PatientDTO patient = objectMapper.readValue(response.toString(), PatientDTO.class);
                    System.out.println("✅ 환자 단건 조회 성공: " + patient.getName());
                    return patient;
                } else {
                    System.err.println("❌ 환자 단건 조회 실패: " + responseCode);
                    return null;
                }
            } catch (Exception e) {
                System.err.println("❌ 네트워크 오류: " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        });
    }
    
    // 신규 환자 등록 - POST /api/patient/save
    public CompletableFuture<Boolean> savePatientAsync(PatientDTO patient) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL(BASE_URL + "/save");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                
                String jsonInputString = objectMapper.writeValueAsString(patient);
                
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
                
                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    System.out.println("✅ 환자 등록 성공: " + patient.getName());
                    return true;
                } else {
                    System.err.println("❌ 환자 등록 실패: " + responseCode);
                    return false;
                }
            } catch (Exception e) {
                System.err.println("❌ 환자 등록 중 오류: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        });
    }
    
    // 환자 정보 수정 - PUT /api/patient/update
    public CompletableFuture<Boolean> updatePatientAsync(PatientDTO patient) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL(BASE_URL + "/update");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8"); // ✅ charset 추가
                conn.setRequestProperty("Accept-Charset", "UTF-8");
                conn.setDoOutput(true);

                String jsonInputString = objectMapper.writeValueAsString(patient);
                System.out.println("📤 전송할 JSON: " + jsonInputString); // ✅ 디버깅 로그

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes(java.nio.charset.StandardCharsets.UTF_8); // ✅ StandardCharsets 사용
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                System.out.println("📥 응답 코드: " + responseCode);

                if (responseCode == 200) {
                    System.out.println("✅ 환자 정보 수정 성공: " + patient.getName());
                    return true;
                } else {
                    // ✅ 에러 응답 내용도 확인
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                        String errorResponse = br.lines().collect(java.util.stream.Collectors.joining("\n"));
                        System.err.println("❌ 서버 에러 응답: " + errorResponse);
                    }
                    System.err.println("❌ 환자 정보 수정 실패: " + responseCode);
                    return false;
                }
            } catch (Exception e) {
                System.err.println("❌ 환자 정보 수정 중 오류: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        });
    }
    
    // 환자 삭제 - DELETE /api/patient/delete/{uid}
    public CompletableFuture<Boolean> deletePatientAsync(String uid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL(BASE_URL + "/delete/" + uid);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("DELETE");
                conn.setRequestProperty("Content-Type", "application/json");
                
                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    System.out.println("✅ 환자 삭제 성공: UID=" + uid);
                    return true;
                } else {
                    System.err.println("❌ 환자 삭제 실패: " + responseCode);
                    return false;
                }
            } catch (Exception e) {
                System.err.println("❌ 환자 삭제 중 오류: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        });
    }
}