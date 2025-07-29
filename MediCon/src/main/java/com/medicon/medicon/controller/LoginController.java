package com.medicon.medicon.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import com.medicon.medicon.service.AuthService;

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
        // 로그인 버튼 액션
        loginButton.setOnAction(e -> handleLogin());

        // 로고 이미지 세팅
        logoImageView.setImage(
                new Image(getClass().getResource("/com/medicon/medicon/images/logo.png").toExternalForm())
        );

        // 설정 아이콘 세팅
        settingIcon.setImage(
                new Image(getClass().getResource("/com/medicon/medicon/images/settings.png").toExternalForm())
        );

        // 설정 버튼 클릭
        settingButton.setOnAction(e -> {
            System.out.println("설정 버튼 클릭됨!");
            // 설정 창 띄우거나 팝업 구현 예정
        });
    }

    private void handleLogin() {
        String id = idField.getText();
        String pw = pwField.getText();

        if (authService.login(id, pw)) {
            System.out.println("로그인 성공");
            // → 메인 화면 전환 예정
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "아이디 또는 비밀번호가 잘못되었습니다.");
            alert.showAndWait();
        }
    }
}
