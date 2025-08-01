package com.medicon.medicon.controller.staff;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.medicon.medicon.config.AppConfig;
import com.medicon.medicon.model.StaffUser;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class UpdateController {

    @FXML private ComboBox<StaffUser> staffComboBox;
    @FXML private TextField nameField;
    @FXML private TextField phoneField;
    @FXML private ComboBox<String> rankComboBox;
    @FXML private ComboBox<String> departmentComboBox;

    @FXML
    public void initialize() {
        // TODO: 초기 직원 목록 불러오기
        // TODO: rank/department 값 세팅
    }

    @FXML
    public void onLoadStaffInfo() {
        StaffUser selected = staffComboBox.getValue();
        if (selected == null) return;

        nameField.setText(selected.getName());
        phoneField.setText(selected.getPhone());
        rankComboBox.setValue(selected.getRank());
        departmentComboBox.setValue(selected.getDepartment());
    }

    @FXML
    public void onUpdateStaff() {
        StaffUser selected = staffComboBox.getValue();
        if (selected == null) return;

        try {
            String urlStr = AppConfig.SERVER_BASE_URL + "/api/staff/update"; // 또는 "/api/staff/update/" + selected.getUid()
            HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            JsonObject payload = new JsonObject();
            payload.addProperty("uid", selected.getUid());
            payload.addProperty("name", nameField.getText());
            payload.addProperty("phone", phoneField.getText());
            payload.addProperty("rank", rankComboBox.getValue());
            payload.addProperty("department", departmentComboBox.getValue());
            payload.addProperty("role", selected.getRole()); // 수정 불가지만 서버에 필요

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(payload.toString());
            writer.flush();
            writer.close();

            if (conn.getResponseCode() == 200) {
                showAlert("수정 완료되었습니다.");
            } else {
                showAlert("수정 실패: " + conn.getResponseCode());
            }

        } catch (Exception e) {
            showAlert("오류 발생: " + e.getMessage());
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("알림");
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
