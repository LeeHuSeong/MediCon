package com.medicon.medicon.controller.salary;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class SalaryContainerController {

    @FXML private Button registerViewButton;
    @FXML private Button modifyViewButton;
    @FXML private AnchorPane salaryContentArea;
    @FXML private Button batchPayButton;


    @FXML
    public void initialize() {
        registerViewButton.setOnAction(e -> loadContent("salary_register.fxml"));
        modifyViewButton.setOnAction(e -> loadContent("salary_manage.fxml"));
        loadContent("salary_register.fxml"); // 초기 화면 등록
    }

    private void loadContent(String fxmlName) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource(
                    "/com/medicon/medicon/view/admin/salary/" + fxmlName));
            salaryContentArea.getChildren().setAll(view);
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBatchPay() {
        loadContent("salary_batch_register.fxml");
    }
}
