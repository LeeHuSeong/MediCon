package com.medicon.medicon.controller.salary;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

public class SalaryViewController {

    @FXML private AnchorPane salaryContent;
    @FXML private Button registerBtn;
    @FXML private Button queryBtn;
    @FXML private Button updateBtn;
    @FXML private Button deleteBtn;

    @FXML
    public void initialize() {
        registerBtn.setOnAction(e -> loadView("register.fxml"));
        queryBtn.setOnAction(e -> loadView("query.fxml"));
        updateBtn.setOnAction(e -> loadView("update.fxml"));
        deleteBtn.setOnAction(e -> loadView("delete.fxml"));
    }

    private void loadView(String fxmlName) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource(
                    "/com/medicon/medicon/view/module/salary/" + fxmlName));
            salaryContent.getChildren().setAll(view);
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
