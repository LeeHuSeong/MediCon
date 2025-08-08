package com.medicon.medicon.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class AdminMainController {

    // FXML 경로 상수
    private static final String SALES_FSSSSXML_BASE = "/com/medicon/medicon/view/admin/sale";
    private static final String SALARY_FXML_BASE = "/com/medicon/medicon/view/admin/salary/";
    private static final String STAFF_FXML_BASE = "/com/medicon/medicon/view/admin/staff/";

    // FXML 요소
    @FXML private BorderPane adminBorderPane;     // BorderPane fx:id 추가 필요!
    @FXML private AnchorPane adminContentArea;

    @FXML
    public void initialize() {
        try {
            // AdminTopBar.fxml 수동 로드
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/medicon/medicon/view/admin/AdminTopBar.fxml"));
            Parent topBar = loader.load();

            // 컨트롤러 주입
            AdminTopBarController topBarController = loader.getController();
            topBarController.setMainController(this);

            // 상단 영역에 삽입
            adminBorderPane.setTop(topBar);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            // 컨트롤러 참조 얻기
            Object controller = loader.getController();

            // ManagementContainerController일 경우 초기 화면 로드
            if (controller instanceof ManagementContainerController mcc) {
                mcc.loadDefaultView(); // ← 여기서 초기 진입 화면 로드됨
            }

            adminContentArea.getChildren().setAll(view);
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
