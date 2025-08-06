package com.medicon.medicon.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medicon.medicon.config.AppConfig;
import com.medicon.medicon.model.ChartDTO;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.ArrayList;

public class ChartApiService {
    private static final String BASE_URL = AppConfig.SERVER_BASE_URL + "/api/chart";
    private final ObjectMapper objectMapper;

    public ChartApiService() {
        this.objectMapper = new ObjectMapper();
    }

    // 차트 저장
    public CompletableFuture<Boolean> saveChartAsync(ChartDTO chart) {
        return CompletableFuture.supplyAsync(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(BASE_URL + "/save");
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);

                String jsonInputString = objectMapper.writeValueAsString(chart);
                System.out.println("📤 차트 저장 요청: " + jsonInputString);

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                System.out.println("📡 차트 저장 응답 코드: " + responseCode);

                if (responseCode >= 200 && responseCode < 300) {
                    System.out.println("✅ 차트 저장 성공");
                    return true;
                } else {
                    if (conn.getErrorStream() != null) {
                        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream()))) {
                            String errorResponse = br.lines().collect(Collectors.joining("\n"));
                            System.err.println("❌ 차트 저장 실패: " + responseCode);
                            System.err.println("🔍 에러 내용: " + errorResponse);
                        }
                    }
                    return false;
                }
            } catch (Exception e) {
                System.err.println("❌ 차트 저장 중 오류: " + e.getMessage());
                e.printStackTrace();
                return false;
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        });
    }

    // 차트 ID로 단일 차트 조회
    public CompletableFuture<ChartDTO> getChartByChartIdAsync(String chartId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String urlString = BASE_URL + "/" + URLEncoder.encode(chartId, "UTF-8");
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                        StringBuilder response = new StringBuilder();
                        String inputLine;
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        return objectMapper.readValue(response.toString(), ChartDTO.class);
                    }
                } else {
                    System.err.println("차트 조회 실패: " + responseCode);
                }
            } catch (Exception e) {
                System.err.println("차트 조회 중 오류: " + e.getMessage());
            }
            return null;
        });
    }

    // 환자 UID로 차트 목록 조회
    public CompletableFuture<List<ChartDTO>> getChartsByPatientUidAsync(String patientUid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String urlString = BASE_URL + "/by-uid/" + URLEncoder.encode(patientUid, "UTF-8");
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                        StringBuilder response = new StringBuilder();
                        String inputLine;
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        return objectMapper.readValue(response.toString(), new TypeReference<List<ChartDTO>>() {});
                    }
                } else {
                    System.err.println("환자 차트 목록 조회 실패: " + responseCode);
                }
            } catch (Exception e) {
                System.err.println("환자 차트 목록 조회 중 오류: " + e.getMessage());
            }
            return new ArrayList<>();
        });
    }

    // 환자 UID와 방문 날짜로 특정 차트 조회
    public CompletableFuture<ChartDTO> getChartByPatientAndDateAsync(String patientUid, String visitDate) {
        return getChartsByPatientUidAsync(patientUid).thenApply(charts -> {
            return charts.stream()
                    .filter(chart -> visitDate.equals(chart.getVisit_date()))
                    .findFirst()
                    .orElse(null);
        });
    }
} 