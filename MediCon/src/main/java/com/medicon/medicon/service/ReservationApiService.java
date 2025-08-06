// 새로운 ReservationApiService 생성 (비동기 방식)
package com.medicon.medicon.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medicon.medicon.config.AppConfig;
import com.medicon.medicon.model.ReservationDTO;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ReservationApiService {
    private static final String BASE_URL = AppConfig.SERVER_BASE_URL + "/api/reservation";
    private final ObjectMapper objectMapper;

    public ReservationApiService() {
        this.objectMapper = new ObjectMapper();
    }

    // 환자 ID로 예약 전체 조회
    public CompletableFuture<List<ReservationDTO>> getReservationsByPatientId(String patientId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String urlString = BASE_URL + "/by-patient?patientId=" + URLEncoder.encode(patientId, "UTF-8");
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
                    return objectMapper.readValue(response.toString(), new TypeReference<List<ReservationDTO>>() {});
                } else {
                    System.err.println("예약 조회 실패: " + responseCode);
                    return new ArrayList<>();
                }
            } catch (Exception e) {
                System.err.println("예약 조회 오류: " + e.getMessage());
                return new ArrayList<>();
            }
        });
    }

    // 날짜별 예약 조회
    public CompletableFuture<List<ReservationDTO>> getReservationsByDate(String date) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String urlString = BASE_URL + "/by-date?date=" + URLEncoder.encode(date, "UTF-8");
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
                    return objectMapper.readValue(response.toString(), new TypeReference<List<ReservationDTO>>() {});
                } else {
                    System.err.println("날짜별 예약 조회 실패: " + responseCode);
                    return new ArrayList<>();
                }
            } catch (Exception e) {
                System.err.println("날짜별 예약 조회 오류: " + e.getMessage());
                return new ArrayList<>();
            }
        });
    }

    // 예약 저장
    public CompletableFuture<Boolean> saveReservationAsync(ReservationDTO reservationDTO) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL(BASE_URL + "/save");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                // JSON 데이터 전송
                String jsonInputString = objectMapper.writeValueAsString(reservationDTO);
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                if (responseCode >= 200 && responseCode < 300) {
                    System.out.println("예약 저장 성공");
                    return true;
                } else {
                    System.err.println("예약 저장 실패: " + responseCode);
                    return false;
                }
            } catch (Exception e) {
                System.err.println("예약 저장 오류: " + e.getMessage());
                return false;
            }
        });
    }
}
