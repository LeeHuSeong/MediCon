package com.medicon.medicon.controller.medic.form;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class TreatmentHistoryFormController implements Initializable {

    @FXML private ListView<String> historyListView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 예시 데이터 초기화
        historyListView.setItems(FXCollections.observableArrayList(
                "2025-01-10: 감기 증상 진료",
                "2025-03-22: 정기 검진",
                "2025-06-15: 알레르기 검사"
        ));
    }

    /**
     * 선택된 이력을 더블클릭했을 때 상세보기 처리 등
     */
    @FXML
    private void handleHistoryClick() {
        String selected = historyListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // TODO: 상세보기 로직 구현
            System.out.println("선택된 이력: " + selected);
        }
    }

    /**
     * 창 닫기 (필요시 호출)
     */
    @FXML
    private void handleClose() {
        Stage stage = (Stage) historyListView.getScene().getWindow();
        stage.close();
    }
}
