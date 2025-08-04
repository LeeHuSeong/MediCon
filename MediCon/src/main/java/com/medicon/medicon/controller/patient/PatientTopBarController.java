package com.medicon.medicon.controller.patient;

import com.medicon.medicon.controller.medic.patient.PatientMainController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class PatientTopBarController implements Initializable {

    @FXML private ImageView logoImg;
    @FXML private Button perInfoBtn;
    @FXML private Button reservationBtn;
    @FXML private Button medicalRecordBtn;
    @FXML private Button inspectionResultBtn;

    private PatientMainController mainController;

    public void setMainController(PatientMainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        logoImg.setImage(new Image(Objects.requireNonNull(getClass().getResource(
                "/com/medicon/medicon/images/onlyIcon_logo.png"
        )).toExternalForm()));
        logoImg.setFitWidth(100);
        logoImg.setFitHeight(80);

        perInfoBtn.setOnAction(e -> {
            System.out.println("개인정보관리 버튼 클릭!");
            selectTab(perInfoBtn);
            if (mainController != null) {
                mainController.setCenter("/com/medicon/medicon/view/patient/patient_main/PerInfoView.fxml");
            }
        });

        reservationBtn.setOnAction(e -> {
            System.out.println("진료예약 버튼 클릭!");
            selectTab(reservationBtn);
            if (mainController != null) {
                mainController.setCenter("/com/medicon/medicon/view/patient/patient_main/ReservationView.fxml");
            }
        });

        medicalRecordBtn.setOnAction(e -> {
            System.out.println("진료기록 버튼 클릭!");
            selectTab(medicalRecordBtn);
            if (mainController != null) {
                mainController.setCenter("/com/medicon/medicon/view/patient/patient_main/MedicalRecordView.fxml");
            }
        });
        inspectionResultBtn.setOnAction(e -> {
            System.out.println("검사결과 버튼 클릭!");
            selectTab(inspectionResultBtn);
            if (mainController != null) {
                mainController.setCenter("/com/medicon/medicon/view/patient/patient_main/InspectionView.fxml");
            }
        });


        // ✅ 초기 탭 기본 선택
        selectTab(perInfoBtn);
    }


    private void selectTab(Button selected) {
        perInfoBtn.getStyleClass().remove("selected");
        reservationBtn.getStyleClass().remove("selected");
        medicalRecordBtn.getStyleClass().remove("selected");
        inspectionResultBtn.getStyleClass().remove("selected");

        selected.getStyleClass().add("selected");
    }
}
