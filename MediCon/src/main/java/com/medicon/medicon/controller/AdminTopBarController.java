package com.medicon.medicon.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class AdminTopBarController implements Initializable {

    @FXML private ImageView logoImg;
    @FXML private Button businessBtn;
    @FXML private Button staffBtn;
    @FXML private Button logoutBtn;

    private AdminMainController mainController;

    public void setMainController(AdminMainController controller) {
        this.mainController = controller;
        businessBtn.fire();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logoImg.setImage(new Image(Objects.requireNonNull(getClass().getResource(
                "/com/medicon/medicon/images/onlyIcon_logo.png"
        )).toExternalForm()));
        logoImg.setFitWidth(100);
        logoImg.setFitHeight(80);

        businessBtn.setOnAction(e -> {
            selectTab(businessBtn);
            if (mainController != null) {
                mainController.setView("/com/medicon/medicon/view/admin/management_container.fxml");
            }
        });

        staffBtn.setOnAction(e -> {
            selectTab(staffBtn);
            if (mainController != null) {
                mainController.setView("/com/medicon/medicon/view/admin/staff/staff_container.fxml");
            }
        });

        logoutBtn.setOnAction(e -> {
            System.out.println("로그아웃 버튼 클릭");
            // 현재 Stage 닫기
            Stage currentStage = (Stage) logoutBtn.getScene().getWindow();
            currentStage.close();

            // 로그인 화면 로드
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/medicon/medicon/view/login.fxml"));
                Parent root = loader.load();
                Stage loginStage = new Stage();
                loginStage.setScene(new Scene(root));
                loginStage.setTitle("MediCon - 로그인");
                loginStage.show();
            } catch (IOException ex) {
                ex.printStackTrace();
                // 오류 처리
            }
        });

        if (mainController != null) {
            businessBtn.fire(); // 버튼 클릭 이벤트 강제 호출
        }
    }

    private void selectTab(Button selected) {
        businessBtn.getStyleClass().remove("selected");
        staffBtn.getStyleClass().remove("selected");
        selected.getStyleClass().add("selected");
    }
}
