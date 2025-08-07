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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ChartApiService {

    private static final String CHART_BASE_URL = AppConfig.SERVER_BASE_URL + "/api/chart";
    private final ObjectMapper objectMapper;

    public ChartApiService() {
        this.objectMapper = new ObjectMapper();
    }

    public CompletableFuture<List<ChartDTO>> getChartsByPatientUidAsync(String patientUid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL(CHART_BASE_URL + "/by-patient/" + patientUid);
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

                    return objectMapper.readValue(response.toString(), new TypeReference<List<ChartDTO>>() {});
                } else {
                    System.err.println("차트 조회 실패: " + responseCode);
                    return new ArrayList<>();
                }
            } catch (Exception e) {
                System.err.println("네트워크 오류: " + e.getMessage());
                e.printStackTrace();
                return new ArrayList<>();
            }
        });
    }

    public CompletableFuture<Boolean> saveChartAsync(ChartDTO chart) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL(CHART_BASE_URL + "/save");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; utf-8");
                conn.setDoOutput(true);

                String jsonInputString = objectMapper.writeValueAsString(chart);

                try(OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                return responseCode == 200;
            } catch (Exception e) {
                System.err.println("차트 저장 API 호출 실패: " + e.getMessage());
                return false;
            }
        });
    }

    public CompletableFuture<ChartDTO> getChartByChartIdAsync(String chartId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL(CHART_BASE_URL + "/" + chartId);
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

                    return objectMapper.readValue(response.toString(), ChartDTO.class);
                } else {
                    System.err.println("차트 ID로 조회 실패: " + responseCode);
                    return null;
                }
            } catch (Exception e) {
                System.err.println("네트워크 오류: " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        });
    }
}