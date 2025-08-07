package com.medicon.medicon.controller.patient;

import com.medicon.medicon.model.ChartDTO;
import com.medicon.medicon.model.UserDTO;
import com.medicon.medicon.service.ChartApiService;
import com.medicon.medicon.service.UserApiService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

public class MedicalRecordController implements Initializable {

    @FXML private ListView<ChartDTO> recordListView;

    private String currentUid;
    private ChartApiService chartApiService;
    private UserApiService userApiService;
    private Map<String, String> doctorNamesCache = new HashMap<>();

    public void setUid(String uid) {
        this.currentUid = uid;
        System.out.println("MedicalRecordController - UID 설정: " + uid);
        if (uid != null) {
            loadMedicalRecords();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("MedicalRecordController 초기화 시작");

        chartApiService = new ChartApiService();
        userApiService = new UserApiService();

        recordListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(ChartDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String doctorName = doctorNamesCache.getOrDefault(item.getDoctor_uid(), "알 수 없음");
                    setText(item.getVisit_date()
                            + " " + item.getVisit_time()
                            + " - " + item.getDiagnosis()
                            + " (의사: " + doctorName + ")");
                }
            }
        });

        System.out.println("MedicalRecordController 초기화 완료");
    }

    private void loadMedicalRecords() {
        if (currentUid == null) {
            System.out.println("UID가 설정되지 않았습니다.");
            return;
        }

        chartApiService.getChartsByPatientUidAsync(currentUid)
                .thenAccept(chartDTOs -> {
                    System.out.println("API 응답 수신. 차트 개수: " + chartDTOs.size());
                    if (chartDTOs != null && !chartDTOs.isEmpty()) {
                        chartDTOs.forEach(chart -> System.out.println("ChartDTO doctor_uid: " + chart.getDoctor_uid()));
                        // 의사 이름 캐시를 채우기 위한 비동기 작업
                        CompletableFuture<?>[] futures = chartDTOs.stream()
                                .map(chart -> userApiService.getUserByUidAsync(chart.getDoctor_uid())
                                        .thenAccept(user -> {
                                            if (user != null && user.getName() != null) {
                                                doctorNamesCache.put(user.getUid(), user.getName());
                                            }
                                        })
                                        .exceptionally(ex -> {
                                            System.err.println("의사 정보 로드 중 오류: " + ex.getMessage());
                                            return null;
                                        }))
                                .toArray(CompletableFuture[]::new);

                        CompletableFuture.allOf(futures).thenRun(() -> {
                            Platform.runLater(() -> {
                                recordListView.setItems(FXCollections.observableArrayList(chartDTOs));
                                System.out.println("진료 기록 로드 완료: " + chartDTOs.size() + "개");
                            });
                        });
                    } else {
                        Platform.runLater(() -> {
                            recordListView.getItems().clear();
                            System.out.println("로드된 진료 기록이 없습니다.");
                        });
                    }
                })
                .exceptionally(throwable -> {
                    Platform.runLater(() -> {
                        System.err.println("진료 기록 로드 중 오류 발생: " + throwable.getMessage());
                        throwable.printStackTrace();
                    });
                    return null;
                });
    }
}
