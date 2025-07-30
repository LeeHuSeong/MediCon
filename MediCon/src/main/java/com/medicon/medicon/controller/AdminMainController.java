package com.medicon.medicon.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class AdminMainController {

    @FXML private AnchorPane contentArea;
    @FXML private Button salesButton;
    @FXML private Button salaryButton;

    @FXML
    public void initialize() {
        salesButton.setOnAction(e -> showSales());
        salaryButton.setOnAction(e -> showSalary());
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

    @FXML
    public void showSalary() {
        try {
            Parent salaryView = FXMLLoader.load(getClass().getResource("/com/medicon/medicon/view/module/salary_view.fxml"));
            contentArea.getChildren().setAll(salaryView);
            AnchorPane.setTopAnchor(salaryView, 0.0);
            AnchorPane.setBottomAnchor(salaryView, 0.0);
            AnchorPane.setLeftAnchor(salaryView, 0.0);
            AnchorPane.setRightAnchor(salaryView, 0.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
