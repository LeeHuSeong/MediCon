package com.medicon.medicon.controller;

import com.medicon.medicon.controller.medic.MedicMainController;
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

    private MedicMainController mainController;

    public void setMainController(MedicMainController medicMainController) {
        this.mainController = medicMainController;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        logoImg.setImage(new Image(Objects.requireNonNull(getClass().getResource(
                "/com/medicon/medicon/images/onlyIcon_logo.png"
        )).toExternalForm()));
        logoImg.setFitWidth(100);
        logoImg.setFitHeight(80);

        patientBtn.setOnAction(e -> {
            System.out.println("환자관리 버튼 클릭!");
            selectTab(patientBtn);
            if (mainController != null) {
                mainController.setCenter("/com/medicon/medicon/view/medic/medic_main/PatientManagement.fxml");
            }
        });

        questionBtn.setOnAction(e -> {
            System.out.println("문진관리 버튼 클릭!");
            selectTab(questionBtn);
            if (mainController != null) {
                mainController.setCenter("/com/medicon/medicon/view/medic/medic_main/QuestionnaireManagement.fxml");
            }
        });

        treatmentBtn.setOnAction(e -> {
            System.out.println("진료관리 버튼 클릭!");
            selectTab(treatmentBtn);
            if (mainController != null) {
                mainController.setCenter("/com/medicon/medicon/view/medic/medic_main/TreatmentManagement.fxml");
            }
        });

        // ✅ 초기 탭 기본 선택
        selectTab(patientBtn);
    }


    private void selectTab(Button selected) {
        patientBtn.getStyleClass().remove("selected");
        questionBtn.getStyleClass().remove("selected");
        treatmentBtn.getStyleClass().remove("selected");

        selected.getStyleClass().add("selected");
    }
}
