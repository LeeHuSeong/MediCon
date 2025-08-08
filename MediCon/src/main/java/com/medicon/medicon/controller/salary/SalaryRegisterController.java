package com.medicon.medicon.controller.salary;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.medicon.medicon.config.AppConfig;
import com.medicon.medicon.model.StaffUser;
import com.medicon.medicon.util.SalaryBaseTable;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SalaryRegisterController {

    @FXML private ComboBox<StaffUser> uidComboBox;
    @FXML private ComboBox<String> payMonthComboBox;
    @FXML private TextField basePayField;
    @FXML private TextField bonusField;
    @FXML private Button submitButton;
    @FXML private Label rankLabel;

    private final Gson gson = new Gson();

    @FXML
    public void initialize() {
        // 기본급 필드는 수정 불가 (자동 계산)
        basePayField.setEditable(false);

        //년/월 콤보박스
        populatePayMonthComboBox();

        // 사용자 목록 서버에서 불러오기
        loadStaffUsersFromServer();

        // ComboBox 표시 형식
        uidComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(StaffUser user) {
                return user == null ? "" : user.getName() + " (" + user.getUid() + ")";
            }
            @Override
            public StaffUser fromString(String string) {
                return null;
            }
        });

        uidComboBox.setOnAction(e -> {
            StaffUser selected = uidComboBox.getValue();
            if (selected != null) {
                rankLabel.setText(selected.getRank());
                long basePay = SalaryBaseTable.getBasePay(selected.getRole(), selected.getRank());
                basePayField.setText(String.valueOf(basePay));
            } else {
                rankLabel.setText("-");
                basePayField.clear();
            }
        });

        // 사용자 선택 시 기본급 자동 계산
        uidComboBox.setOnAction(e -> {
            StaffUser selected = uidComboBox.getValue();
            if (selected != null) {
                // 1) 직급 표시
                rankLabel.setText(selected.getRank());

                // 2) 기본급 자동 계산
                long basePay = SalaryBaseTable.getBasePay(selected.getRole(), selected.getRank());
                basePayField.setText(String.valueOf(basePay));
            } else {
                rankLabel.setText("-");
                basePayField.clear();
            }
        });
    }

    private void loadStaffUsersFromServer() {
        new Thread(() -> {
            try {
                String apiUrl = AppConfig.SERVER_BASE_URL + "/api/staff/list?role=doctor,nurse";
                HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) response.append(line);
                    reader.close();

                    ApiResponse<List<StaffUser>> apiResponse = parseResponse(response.toString());
                    if (apiResponse != null && apiResponse.getData() != null) {
                        Platform.runLater(() -> uidComboBox.getItems().setAll(apiResponse.getData()));
                    }
                } else {
                    showAlert("직원 목록 조회 실패 (응답 코드: " + responseCode + ")");
                }

            } catch (Exception e) {
                e.printStackTrace();
                showAlert("직원 목록 요청 중 오류 발생: " + e.getMessage());
            }
        }).start();
    }

    private ApiResponse<List<StaffUser>> parseResponse(String json) {
        try {
            Type responseType = new TypeToken<ApiResponse<List<StaffUser>>>() {}.getType();
            return gson.fromJson(json, responseType);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @FXML
    public void handleRegisterSalary() {
        StaffUser selectedUser = uidComboBox.getValue();
        String payMonth = payMonthComboBox.getValue();
        String basePayStr = basePayField.getText();
        String bonusStr = bonusField.getText();

        if (selectedUser == null || payMonth == null || basePayStr.isEmpty() || bonusStr.isEmpty()) {
            showAlert("모든 필드를 입력해주세요.");
            return;
        }

        try {
            String[] parts = payMonth.split("-");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            long basePay = Long.parseLong(basePayStr);
            long bonus = Long.parseLong(bonusStr);

            SalaryRequest req = new SalaryRequest(year, month, basePay, bonus);
            String json = gson.toJson(req);

            String apiUrl = AppConfig.SERVER_BASE_URL +
                    String.format("/api/staff/salary/%s?role=%s", selectedUser.getUid(), selectedUser.getRole());

            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                showAlert("급여 등록 성공");
                clearForm();
            } else {
                showAlert("급여 등록 실패 (응답 코드: " + responseCode + ")");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("오류 발생: " + e.getMessage());
        }
    }


    private void clearForm() {
        uidComboBox.getSelectionModel().clearSelection();
        payMonthComboBox.getSelectionModel().clearSelection();
        basePayField.clear();
        bonusField.clear();
        rankLabel.setText("-");
    }

    private void showAlert(String msg) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText(msg);
            alert.showAndWait();
        });
    }

    // DTO 클래스
    static class SalaryRequest {
        private int year;
        private int month;
        private long basePay;
        private long bonus;

        public SalaryRequest(int year, int month, long basePay, long bonus) {
            this.year = year;
            this.month = month;
            this.basePay = basePay;
            this.bonus = bonus;
        }
    }

    static class ApiResponse<T> {
        private boolean success;
        private String message;
        private T data;

        public T getData() {
            return data;
        }
    }

    private void populatePayMonthComboBox() {
        for (int year = 2020; year <= 2030; year++) {
            for (int month = 1; month <= 12; month++) {
                String formatted = String.format("%d-%02d", year, month);
                payMonthComboBox.getItems().add(formatted);
            }
        }
    }

}
