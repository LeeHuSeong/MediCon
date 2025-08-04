package com.medicon.medicon.controller.medic;

import com.medicon.medicon.controller.TopBarController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

public class MedicMainController {

    @FXML private BorderPane mainBorderPane;

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
            FXMLLoader topBarLoader = new FXMLLoader(getClass().getResource("/com/medicon/medicon/view/medic/medic_header/TopBar.fxml"));
            Node topBar = topBarLoader.load();

            TopBarController controller = topBarLoader.getController();
            controller.setMainController(this); // 🔗 MainController 연결

            mainBorderPane.setTop(topBar);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}