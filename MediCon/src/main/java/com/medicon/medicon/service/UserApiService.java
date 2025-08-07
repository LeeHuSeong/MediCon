package com.medicon.medicon.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medicon.medicon.config.AppConfig;
import com.medicon.medicon.model.UserDTO;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

public class UserApiService {

    private static final String USER_BASE_URL = AppConfig.SERVER_BASE_URL + "/api/user";
    private final ObjectMapper objectMapper;

    public UserApiService() {
        this.objectMapper = new ObjectMapper();
    }

    public CompletableFuture<UserDTO> getUserByUidAsync(String uid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL(USER_BASE_URL + "/" + uid);
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

                    return objectMapper.readValue(response.toString(), UserDTO.class);
                } else {
                    System.err.println("사용자 조회 실패: " + responseCode + ", UID: " + uid);
                    return null;
                }
            } catch (Exception e) {
                System.err.println("네트워크 오류 (UserApiService): " + e.getMessage() + ", UID: " + uid);
                e.printStackTrace();
                return null;
            }
        });
    }
}
