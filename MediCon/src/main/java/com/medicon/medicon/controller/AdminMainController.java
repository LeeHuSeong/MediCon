package com.medicon.medicon.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class AdminMainController {

    // FXML 경로 상수
    private static final String SALES_FXML_BASE = "/com/medicon/medicon/view/";
    private static final String SALARY_FXML_BASE = "/com/medicon/medicon/view/module/salary/";
    private static final String STAFF_FXML_BASE = "/com/medicon/medicon/view/module/staff/";

    // 경영관리 탭
    @FXML private AnchorPane contentArea;
    @FXML private Button salesButton;
    @FXML private Button salaryButton;

    // 직원관리 탭
    @FXML private AnchorPane staffContentArea;
    @FXML private Button staffRegisterButton;
    @FXML private Button staffListButton;
    @FXML private Button staffUpdateButton;
    @FXML private Button staffDeleteButton;

    @FXML
    public void initialize() {
        // 경영관리
        salesButton.setOnAction(e -> loadContent(contentArea, SALES_FXML_BASE, "sales_view.fxml"));
        salaryButton.setOnAction(e -> loadContent(contentArea, SALARY_FXML_BASE, "salary_manage.fxml"));

        // 직원관리
        staffRegisterButton.setOnAction(e -> loadContent(staffContentArea, STAFF_FXML_BASE, "register.fxml"));
        staffListButton.setOnAction(e -> loadContent(staffContentArea, STAFF_FXML_BASE, "list.fxml"));
        staffUpdateButton.setOnAction(e -> loadContent(staffContentArea, STAFF_FXML_BASE, "update.fxml"));
        staffDeleteButton.setOnAction(e -> loadContent(staffContentArea, STAFF_FXML_BASE, "delete.fxml"));
    }

    /** 공통 AnchorPane FXML 로더 */
    private void loadContent(AnchorPane targetPane, String basePath, String fileName) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource(basePath + fileName));
            targetPane.getChildren().setAll(view);
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
