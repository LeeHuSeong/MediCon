package com.medicon.medicon.controller.medic.form;

import com.medicon.medicon.model.ChartDTO;
import com.medicon.medicon.service.ChartApiService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class TreatmentHistoryFormController implements Initializable {

    @FXML private ListView<ChartDTO> historyListView;

    private final ChartApiService chartService = new ChartApiService();
    private String patientUid;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 리스트 클릭 시 상세 보기 (예시)
        historyListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                ChartDTO selected = historyListView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    // TODO: 상세보기 로직
                    System.out.println("선택된 차트: " + selected.getChart_id());
                }
            }
        });

        // 셀 표시 포맷 설정 (날짜+진단명 등)
        historyListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(ChartDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getVisit_date()
                            + " " + item.getVisit_time()
                            + " — " + item.getDiagnosis());
                }
            }
        });
    }

    /**
     * 외부에서 환자 UID를 전달받는 세터
     */
    public void setPatientUid(String uid) {
        this.patientUid = uid;
        loadHistory();
    }

    /**
     * patientUid 기준으로 차트 목록을 불러와 ListView에 세팅
     */
    private void loadHistory() {
        if (patientUid == null) return;

        chartService.getChartsByPatientUidAsync(patientUid)
                .thenAccept(charts -> Platform.runLater(() -> {
                    if (charts != null && !charts.isEmpty()) {
                        historyListView.setItems(FXCollections.observableArrayList(charts));
                    } else {
                        historyListView.getItems().clear();
                    }
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        // TODO: 알림창 띄우기 등 에러 처리
                        System.err.println("이력 조회 실패: " + ex.getMessage());
                    });
                    return null;
                });
    }

    /** 닫기 버튼 핸들러 */
    @FXML
    private void handleClose() {
        Stage stage = (Stage) historyListView.getScene().getWindow();
        stage.close();
    }
}
