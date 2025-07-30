package com.medicon.medicon.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminPasswordController {

    @FXML private PasswordField adminPwField;
    @FXML private Button confirmButton;

    private final String adminPassword = "admin1234"; // 실제 비밀번호

    @FXML
    public void initialize() {
        confirmButton.setOnAction(event -> {
            String inputPw = adminPwField.getText();
            if (inputPw.equals(adminPassword)) {
                moveToAdminMain();
            } else {
                showAlert("비밀번호가 올바르지 않습니다.");
            }
        });
    }

    private void moveToAdminMain() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/medicon/medicon/view/admin_main.fxml"));
            Parent root = loader.load();

            Stage adminStage = new Stage();
            adminStage.setTitle("MediCon 관리자");
            adminStage.setScene(new Scene(root));
            adminStage.setMaximized(true);
            adminStage.show();

            // 현재 비밀번호 창 닫기
            Stage currentStage = (Stage) confirmButton.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("관리자 화면 이동 실패: " + e.getMessage());
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.setHeaderText("오류");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
