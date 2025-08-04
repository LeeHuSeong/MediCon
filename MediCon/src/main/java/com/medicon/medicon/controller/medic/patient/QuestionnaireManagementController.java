package com.medicon.medicon.controller.medic.patient;

import com.medicon.medicon.model.MedicalInterviewDTO;
import com.medicon.medicon.model.PatientDTO;
import com.medicon.medicon.model.ReservationDTO;
import com.medicon.medicon.service.PatientApiService;
import com.medicon.medicon.service.ReservationApiService;
import com.medicon.medicon.service.MedicalInterviewApiService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class QuestionnaireManagementController implements Initializable {

    @FXML private Button registerPatientButton;
    @FXML private Button todayPatientButton;
    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private ListView<PatientDTO> patientListView;
    @FXML private Label symptomLabel;
    @FXML private Label historyLabel;
    @FXML private Label allergyLabel;
    @FXML private Label medicationLabel;
    @FXML private ListView<String> historyQuestionnaireListView;

    private final PatientApiService patientApiService = new PatientApiService();
    private final ReservationApiService reservationApiService = new ReservationApiService();
    private final MedicalInterviewApiService interviewApiService = new MedicalInterviewApiService();
    private final PatientDataManager dataManager = new PatientDataManager();
    private final ObservableList<PatientDTO> patientData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // ListView와 ObservableList 연결
        patientListView.setItems(patientData);
        
        registerPatientButton.setOnAction(e -> loadAllPatients());
        todayPatientButton.setOnAction(e -> loadTodayPatients());
        searchButton.setOnAction(e -> handleSearch());

        patientListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                displayRecentInterview(newVal);
            }
        });
    }

    private void loadAllPatients() {
        patientApiService.getAllPatientsAsync().thenAccept(patients -> {
            Platform.runLater(() -> {
                patientData.clear();
                patientData.addAll(patients);
            });
        });
    }

    private void loadTodayPatients() {
        dataManager.loadTodayPatients(patientData, 
            errorMsg -> showInfo("금일 환자 조회 실패: " + errorMsg),
            () -> {
                if (patientData.isEmpty()) {
                    showInfo("오늘 예약된 환자가 없습니다.");
                }
            }
        );
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadAllPatients();
            return;
        }
        patientApiService.getPatientsByNameAsync(keyword).thenAccept(patients -> {
            Platform.runLater(() -> {
                patientData.clear();
                patientData.addAll(patients);
            });
        });
    }

    private void displayRecentInterview(PatientDTO patient) {
        reservationApiService.getReservationsByPatientId(patient.getPatient_id()).thenAccept(reservations -> {
            Platform.runLater(() -> {
                if (reservations != null && !reservations.isEmpty()) {
                    ReservationDTO latestReservation = reservations.get(0); // 최신 예약 하나만 예시로
                    interviewApiService.getInterviewByReservationAsync(
                            patient.getUid(), patient.getPatient_id(), latestReservation.getReservation_id()
                    ).thenAccept(interviews -> {
                        Platform.runLater(() -> {
                            if (interviews != null && !interviews.isEmpty()) {
                                MedicalInterviewDTO interview = interviews.get(0);
                                symptomLabel.setText(interview.getSymptoms());
                                historyLabel.setText(interview.getPast_medical_history());
                                allergyLabel.setText(interview.getAllergy());
                                medicationLabel.setText(interview.getCurrent_medication());
                            } else {
                                clearInterviewInfo();
                            }

                            historyQuestionnaireListView.getItems().clear();
                            for (MedicalInterviewDTO i : interviews) {
                                historyQuestionnaireListView.getItems().add(
                                        latestReservation.getDate() + " - " + i.getSymptoms()
                                );
                            }
                        });
                    });
                } else {
                    clearInterviewInfo();
                }
            });
        });
    }

    private void clearInterviewInfo() {
        symptomLabel.setText("-");
        historyLabel.setText("-");
        allergyLabel.setText("-");
        medicationLabel.setText("-");
        historyQuestionnaireListView.getItems().clear();
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("알림");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    // FXML에서 참조하는 메서드들
    @FXML
    private void handleRegisterPatient() {
        loadAllPatients();
    }

    @FXML
    private void handleTodayPatients() {
        loadTodayPatients();
    }

    @FXML
    private void handleAddQuestionnaire() {
        // 문진 추가 기능 (추후 구현)
        showInfo("문진 추가 기능은 추후 구현 예정입니다.");
    }
}