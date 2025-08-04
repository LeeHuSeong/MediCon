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

    private Stage popupStage;

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
                // 이미 열려 있으면 포커스만 줌
                if (popupStage != null && popupStage.isShowing()) {
                    popupStage.requestFocus();
                    return;
                }

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/medicon/medicon/view/admin/admin_pw_check.fxml"));
                Parent root = loader.load();

                popupStage = new Stage();
                popupStage.setTitle("관리자 로그인");
                popupStage.setScene(new Scene(root));
                popupStage.setResizable(false);

                // 창이 닫히면 popupStage를 null로 초기화
                popupStage.setOnHidden(ev -> popupStage = null);

                popupStage.show();

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

                    // 로그인 성공 후 Main.fxml로 화면 전환
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/medicon/medicon/view/medic/medic_main/MedicMain.fxml"));
                        Parent root = loader.load();
                        Stage stage;

                        if (loginButton.getScene() != null && loginButton.getScene().getWindow() instanceof Stage s) {
                            stage = s;
                        } else {
                            showError("로그인 화면이 제대로 로드되지 않았습니다. 다시 시도해 주세요.");
                            return;
                        }

                        stage.setScene(new Scene(root));
                        stage.setTitle("MediCon 메인 화면");

                        // 최소 크기 설정
                        stage.setMinWidth(1280);
                        stage.setMinHeight(720);
                        stage.setMaximized(true);
                        stage.show();
                    } catch (IOException ex) {
                        showError("메인 화면 로딩 실패: " + ex.getMessage());
                    }
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
