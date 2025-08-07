package com.medicon.medicon.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medicon.medicon.model.StaffUser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class StaffApiService {
    private static final String BASE_URL = "http://localhost:8080";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    /**
     * 비동기로 /api/staff/{uid} 호출 → 직원(의사, 간호사) DTO 반환
     */
    public CompletableFuture<StaffUser> getStaffByUidAsync(String uid) {
        String url = BASE_URL + "/api/staff/" + uid;
        System.out.println("[StaffApiService] 요청 URL: " + url); // ★ 요청 URL 출력

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    System.out.println("[StaffApiService] 응답 상태코드: " + response.statusCode());
                    System.out.println("[StaffApiService] 응답 JSON: " + response.body()); // ★ 응답 JSON 출력
                    return response.body();
                })
                .thenApply(json -> {
                    try {
                        var root = objectMapper.readTree(json);
                        var data = root.get("data");
                        System.out.println("[StaffApiService] data 노드: " + data); // ★ data 필드 출력

                        StaffUser user = objectMapper.treeToValue(data, StaffUser.class);
                        System.out.println("[StaffApiService] StaffUser 파싱 성공: " + user);
                        return user;
                    } catch (Exception e) {
                        System.out.println("[StaffApiService] StaffUser 파싱 실패");
                        e.printStackTrace();
                        return null;
                    }
                });
    }


}
