package com.medicon.medicon.controller.medic.patient;

import com.medicon.medicon.controller.patient.PerInfoController;
import com.medicon.medicon.controller.patient.PatientTopBarController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

public class PatientMainController {

    @FXML
    private BorderPane mainBorderPane;
    private String patientUid;


    public void setCenter(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node node = loader.load();

            Object controller = loader.getController();
            if (controller instanceof PerInfoController) {
                ((PerInfoController) controller).setUid(this.patientUid);
            }

            mainBorderPane.setCenter(node);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        try {
            FXMLLoader topBarLoader = new FXMLLoader(getClass().getResource("/com/medicon/medicon/view/patient/patient_header/PatientTopBar.fxml"));
            Node topBar = topBarLoader.load();

            PatientTopBarController controller = topBarLoader.getController();
            controller.setMainController(this);

            mainBorderPane.setTop(topBar);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setUid(String uid) {
        this.patientUid = uid;
        setCenter("/com/medicon/medicon/view/patient/patient_main/ReservationView.fxml");
    }
}
