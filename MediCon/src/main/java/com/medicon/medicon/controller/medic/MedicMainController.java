package com.medicon.medicon.controller.medic;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

public class MedicMainController {

    @FXML private BorderPane mainBorderPane;
    @FXML private String medicUid;

    public void setUid(String uid) {
        this.medicUid = uid;
    }

    public void setCenter(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node node = loader.load();
            mainBorderPane.setCenter(node);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        try {
            FXMLLoader topBarLoader = new FXMLLoader(getClass().getResource("/com/medicon/medicon/view/medic/medic_header/MedicTopBar.fxml"));
            Node topBar = topBarLoader.load();

            MedicTopBarController controller = topBarLoader.getController();
            controller.setMainController(this); // 🔗 MainController 연결

            mainBorderPane.setTop(topBar);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}