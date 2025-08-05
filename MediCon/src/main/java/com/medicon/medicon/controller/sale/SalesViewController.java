package com.medicon.medicon.controller.sale;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class SalesViewController {

    @FXML private Label dailySalesLabel;
    @FXML private TableView<?> monthlySalesTable;
    @FXML private TableColumn<?, ?> monthColumn;
    @FXML private TableColumn<?, ?> amountColumn;

    @FXML
    public void initialize() {
        dailySalesLabel.setText("₩1,200,000"); // 예시 데이터

        // TableView 초기화도 여기에 작성 가능
    }
}
