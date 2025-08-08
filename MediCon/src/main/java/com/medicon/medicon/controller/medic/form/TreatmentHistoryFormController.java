package com.medicon.medicon.controller.medic.form;

import com.medicon.medicon.controller.medic.TreatmentManagementController;
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

    private TreatmentManagementController treatmentManagementController;

    public void setTreatmentManagementController(TreatmentManagementController controller) {
        this.treatmentManagementController = controller;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        historyListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                ChartDTO selected = historyListView.getSelectionModel().getSelectedItem();
                if (selected != null && treatmentManagementController != null) {
                    treatmentManagementController.showChartDetail(selected.getChart_id());
                    Stage stage = (Stage) historyListView.getScene().getWindow();
                    stage.close();
                }
            }
        });

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

    public void setPatientUid(String uid) {
        this.patientUid = uid;
        loadHistory();
    }

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
                        System.err.println("이력 조회 실패: " + ex.getMessage());
                    });
                    return null;
                });
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) historyListView.getScene().getWindow();
        stage.close();
    }
}
