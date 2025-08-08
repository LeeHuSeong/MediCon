package com.medicon.medicon.controller;

import com.medicon.medicon.controller.medic.MedicMainController;
import com.medicon.medicon.controller.medic.patient.PatientMainController;
import com.medicon.medicon.model.LoginResult;
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

    @FXML
    private void onLoginButtonClick() {
        String email = idField.getText();
        String password = pwField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showError("이메일과 비밀번호를 모두 입력하세요.");
            return;
        }

        new Thread(() -> {
            try {
                LoginResult result = authService.loginAndGetResult(email, password);

                Platform.runLater(() -> {
                    if (!result.isSuccess() || result.getToken() == null) {
                        showError("로그인 실패: " + result.getMessage());
                        return;
                    }

                    System.out.println("로그인 성공: " + result.getUid() + ", 권한: " + result.getAuthority());
                    // TODO: result.getToken() 저장 필요 시 처리

                    String fxmlPath;
                    String title;
                    Integer authority = result.getAuthority();
                    if (authority == 0 && authority != null) {
                        fxmlPath = "/com/medicon/medicon/view/patient/patient_main/PatientMainView.fxml";
                        title = "MediCon 환자 메인 화면";
                    } else{
                        fxmlPath = "/com/medicon/medicon/view/medic/medic_main/MedicMain.fxml";
                        title = "MediCon 메인 화면";
                    }

                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                        Parent root = loader.load();

                        // *** 추가: 컨트롤러에 로그인 정보 전달 ***
                        Object controller = loader.getController();
                        if (controller instanceof MedicMainController) {
                            ((MedicMainController) controller).setUid(result.getUid());
                        } else if (controller instanceof PatientMainController) {
                            ((PatientMainController) controller).setUid(result.getUid());
                        }

                        Stage stage = (Stage) loginButton.getScene().getWindow();

                        stage.setScene(new Scene(root));
                        stage.setTitle(title);
                        stage.setMinWidth(1280);
                        stage.setMinHeight(720);

                        stage.show();
                        stage.setMaximized(true);

                    } catch (IOException ex) {
                        showError("화면 로딩 실패: " + ex.getMessage());
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
