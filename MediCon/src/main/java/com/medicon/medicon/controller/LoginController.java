package com.medicon.medicon.controller;

import com.medicon.medicon.service.AuthService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML private TextField idField;
    @FXML private PasswordField pwField;
    @FXML private Button loginButton;
    @FXML private ImageView logoImageView;
    @FXML private ImageView settingIcon;
    @FXML private Button settingButton;

    private final AuthService authService = new AuthService();

    @FXML
    public void initialize() {
        logoImageView.setImage(
                new Image(getClass().getResource("/com/medicon/medicon/images/logo.png").toExternalForm())
        );

        settingIcon.setImage(
                new Image(getClass().getResource("/com/medicon/medicon/images/settings.png").toExternalForm())
        );

        loginButton.setOnAction(e -> onLoginButtonClick());
        settingButton.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/medicon/medicon/view/admin_pw_check.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) settingButton.getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (IOException ex) {
                ex.printStackTrace();
                showError("설정 화면 로딩 실패: " + ex.getMessage());
            }
        });
    }

    public void onLoginButtonClick() {
        String email = idField.getText();
        String password = pwField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showError("이메일과 비밀번호를 모두 입력하세요.");
            return;
        }

        new Thread(() -> {
            try {
                // 핵심 로직 위임
                String jwt = authService.loginAndGetJwt(email, password);

                Platform.runLater(() -> {
                    System.out.println("로그인 성공! JWT: " + jwt);
                    // TODO: JWT 저장 로직
                    // TODO: MainView.fxml 등으로 전환
                });

            } catch (Exception e) {
                Platform.runLater(() -> showError("로그인 실패: " + e.getMessage()));
            }
        }).start();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.showAndWait();
    }
}
