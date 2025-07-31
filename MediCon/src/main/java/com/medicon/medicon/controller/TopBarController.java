package com.medicon.medicon.controller;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

public class TopBarController implements Initializable {

    @FXML private ImageView logoImg;
    @FXML private Button patientBtn;
    @FXML private Button questionBtn;
    @FXML private Button treatmentBtn;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // 이미지 로딩
        logoImg.setImage(new Image(Objects.requireNonNull(getClass().getResource(
                "/com/medicon/medicon/images/onlyIcon_logo.png"
        )).toExternalForm()));
        logoImg.setFitWidth(100);
        logoImg.setFitHeight(80);
        // 버튼 클릭 이벤트 연결
        patientBtn.setOnAction(e -> {
            // 버튼 클릭시 실행할 코드
            System.out.println("환자관리 버튼 클릭!");
            selectTab(patientBtn);
        });
        questionBtn.setOnAction(e -> {
            System.out.println("문진관리 버튼 클릭!");
            selectTab(questionBtn);
        });
        treatmentBtn.setOnAction(e -> {
            System.out.println("진료관리 버튼 클릭!");
            selectTab(treatmentBtn);
        });
    }

    private void selectTab(Button selected) {
        patientBtn.getStyleClass().remove("selected");
        questionBtn.getStyleClass().remove("selected");
        treatmentBtn.getStyleClass().remove("selected");

        selected.getStyleClass().add("selected");
    }
}
