package com.medicon.medicon.controller.patient;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;

import java.net.URL;
import java.util.ResourceBundle;

public class InspectionController implements Initializable {

    @FXML private TableView<?> recordTable;
    @FXML private TableColumn<?, ?> dateColumn;
    @FXML private TableColumn<?, ?> departmentColumn;
    @FXML private TableColumn<?, ?> doctorColumn;
    @FXML private TableColumn<?, ?> diagnosisColumn;
    @FXML private TableColumn<?, ?> detailColumn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        recordTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // TODO: 열 연결 + 데이터 세팅
        // dateColumn.setCellValueFactory(...);
    }
}
