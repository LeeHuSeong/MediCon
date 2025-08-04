package com.medicon.medicon.controller.medic;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class TreatmentManagementController {

    @FXML
    private Button medicalCertificateBtn; // FXML에 이 id가 있어야 함

    @FXML
    private void handleMedicalCertificate(ActionEvent event) {
        try {
            // FXML 파일 로드
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/medicon/medicon/view/medic/medic_main/MedicalCertificateForm.fxml"));
            Parent root = loader.load();

            // 새 창 설정
            Stage stage = new Stage();
            stage.setTitle("진료확인서");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // 모달로 띄우기 (기존 창이 잠김)
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}