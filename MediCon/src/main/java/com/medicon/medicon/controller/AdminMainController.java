package com.medicon.medicon.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminMainController {

    @FXML private AnchorPane contentArea;
    @FXML private Button salesButton;
    @FXML private Button salaryButton;

    @FXML
    public void initialize() {
        salesButton.setOnAction(e -> showSales());
        salaryButton.setOnAction(e -> openSalaryWindow());
    }

    @FXML
    public void showSales() {
        try {
            Parent salesView = FXMLLoader.load(getClass().getResource("/com/medicon/medicon/view/sales_view.fxml"));
            contentArea.getChildren().setAll(salesView);
            AnchorPane.setTopAnchor(salesView, 0.0);
            AnchorPane.setBottomAnchor(salesView, 0.0);
            AnchorPane.setLeftAnchor(salesView, 0.0);
            AnchorPane.setRightAnchor(salesView, 0.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openSalaryWindow() {
        try {
            Parent salaryRoot = FXMLLoader.load(getClass().getResource("/com/medicon/medicon/view/module/salary_view.fxml"));
            Stage salaryStage = new Stage();
            salaryStage.setTitle("급여 관리");
            salaryStage.setScene(new Scene(salaryRoot, 800, 600));
            salaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
