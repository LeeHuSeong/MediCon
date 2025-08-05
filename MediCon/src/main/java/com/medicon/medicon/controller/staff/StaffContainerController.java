package com.medicon.medicon.controller.staff;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class StaffContainerController {

    @FXML private Button staffRegisterButton;
    @FXML private Button staffManageButton;
    @FXML private AnchorPane staffContentArea;

    @FXML
    public void initialize() {
        // 기본 진입 시 직원 등록 화면으로
        loadContent("/com/medicon/medicon/view/admin/staff/staff_manage.fxml");

        staffRegisterButton.setOnAction(e ->
                loadContent("/com/medicon/medicon/view/admin/staff/staff_register.fxml"));

        staffManageButton.setOnAction(e ->
                loadContent("/com/medicon/medicon/view/admin/staff/staff_manage.fxml"));
    }

    private void loadContent(String fxmlPath) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource(fxmlPath));
            staffContentArea.getChildren().setAll(view);
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
