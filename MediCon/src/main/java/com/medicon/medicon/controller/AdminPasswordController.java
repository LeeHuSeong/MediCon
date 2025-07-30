package com.medicon.medicon.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;

public class AdminPasswordController {

    @FXML private PasswordField adminPwField;
    @FXML private Button confirmButton;

    private final String adminPassword = "admin1234"; // 실제 비밀번호

    @FXML
    public void initialize() {
        confirmButton.setOnAction(event -> {
            String inputPw = adminPwField.getText();
            if (inputPw.equals(adminPassword)) {
                showAlert("관리자 인증 성공");
                // TODO: 인증 이후 로직 필요시 추가
            } else {
                showAlert("비밀번호가 올바르지 않습니다.");
            }
        });
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.setHeaderText("알림");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
