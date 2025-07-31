package com.medicon.medicon.controller.salary;

import com.google.gson.Gson;
import com.medicon.medicon.config.AppConfig;
import com.medicon.medicon.model.StaffUser;
import com.medicon.medicon.util.SalaryBaseTable;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

//급여 지급을 위한 컨트롤러
public class SalaryRegisterController {

    @FXML private ComboBox<StaffUser> uidComboBox;
    @FXML private TextField yearField;
    @FXML private TextField monthField;
    @FXML private TextField basePayField;
    @FXML private TextField bonusField;
    @FXML private Button submitButton;

    @FXML
    public void initialize() {
        uidComboBox.getItems().addAll(
                new StaffUser("uid001", "김의사", "doctor", "전임의"),
                new StaffUser("uid002", "이간호", "nurse", "책임간호사")
        );

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
                long basePay = SalaryBaseTable.getBasePay(selected.getRole(), selected.getRank());
                basePayField.setText(String.valueOf(basePay));
            }
        });
    }

    @FXML
    public void handleRegisterSalary() {
        StaffUser selectedUser = uidComboBox.getValue();
        String yearStr = yearField.getText();
        String monthStr = monthField.getText();
        String basePayStr = basePayField.getText();
        String bonusStr = bonusField.getText();

        if (selectedUser == null || yearStr.isEmpty() || monthStr.isEmpty()
                || basePayStr.isEmpty() || bonusStr.isEmpty()) {
            showAlert("모든 필드를 입력해주세요.");
            return;
        }

        try {
            int year = Integer.parseInt(yearStr);
            int month = Integer.parseInt(monthStr);
            long basePay = Long.parseLong(basePayStr);
            long bonus = Long.parseLong(bonusStr);

            SalaryRequest req = new SalaryRequest(year, month, basePay, bonus);
            String json = new Gson().toJson(req);

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

        } catch (NumberFormatException e) {
            showAlert("숫자 형식이 잘못되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("서버 요청 중 오류 발생: " + e.getMessage());
        }
    }

    private void clearForm() {
        uidComboBox.getSelectionModel().clearSelection();
        yearField.clear();
        monthField.clear();
        basePayField.clear();
        bonusField.clear();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    // DTO 내부 클래스
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
}
