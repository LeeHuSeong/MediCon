package com.medicon.medicon.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class ManagementContainerController {

    @FXML private Button salesButton;
    @FXML private Button salaryButton;
    @FXML private AnchorPane managementContentArea;

    @FXML
    public void initialize() {
        // 버튼 이벤트 등록
        salesButton.setOnAction(e -> loadContent("/com/medicon/medicon/view/admin/sale/sales_view.fxml"));
        salaryButton.setOnAction(e -> loadContent("/com/medicon/medicon/view/admin/salary/salary_container.fxml"));
    }

    //밖에서 초기 화면 로드
    public void loadDefaultView() {
        loadContent("/com/medicon/medicon/view/admin/sale/sales_view.fxml");
    }

    private void loadContent(String fxmlPath) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource(fxmlPath));
            managementContentArea.getChildren().setAll(view);
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
