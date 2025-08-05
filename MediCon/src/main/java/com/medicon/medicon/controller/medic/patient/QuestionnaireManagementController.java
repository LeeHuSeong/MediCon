package com.medicon.medicon.controller.medic.patient;

import com.medicon.medicon.model.MedicalInterviewDTO;
import com.medicon.medicon.model.PatientDTO;
import com.medicon.medicon.model.ReservationDTO;
import com.medicon.medicon.service.PatientApiService;
import com.medicon.medicon.service.ReservationApiService;
import com.medicon.medicon.service.MedicalInterviewApiService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class QuestionnaireManagementController implements Initializable {

    @FXML private Button registerPatientButton;
    @FXML private Button todayPatientButton;
    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private ListView<PatientDTO> patientListView;
    @FXML private Label interviewDateLabel;
    @FXML private Label symptomLabel;
    @FXML private Label symptomDurationLabel;
    @FXML private Label historyLabel;
    @FXML private Label allergyLabel;
    @FXML private Label medicationLabel;
    @FXML private TextArea symytomElse;
    @FXML private Button addQuestionnaireButton;
    @FXML private ListView<String> historyQuestionnaireListView;

    private final PatientApiService patientApiService = new PatientApiService();
    private final ReservationApiService reservationApiService = new ReservationApiService();
    private final MedicalInterviewApiService interviewApiService = new MedicalInterviewApiService();
    private final PatientDataManager dataManager = new PatientDataManager();
    private final ObservableList<PatientDTO> patientData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("문진관리 컨트롤러 초기화 시작");
        
        // ListView와 ObservableList 연결
        patientListView.setItems(patientData);
        
        // 버튼 이벤트 설정
        registerPatientButton.setOnAction(e -> {
            System.out.println("전체 환자 조회 버튼 클릭");
            loadAllPatients();
        });
        
        todayPatientButton.setOnAction(e -> {
            System.out.println("금일 문진 조회 버튼 클릭");
            loadTodayPatients();
        });
        
        searchButton.setOnAction(e -> {
            System.out.println("검색 버튼 클릭");
            handleSearch();
        });

        patientListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                System.out.println("환자 선택: " + newVal.getName());
                displayRecentInterview(newVal);
            }
        });
        
        System.out.println("문진관리 컨트롤러 초기화 완료");
    }

    private void loadAllPatients() {
        System.out.println("전체 환자 조회 시작");
        patientApiService.getAllPatientsAsync().thenAccept(patients -> {
            Platform.runLater(() -> {
                patientData.clear();
                if (patients != null) {
                    patientData.addAll(patients);
                    System.out.println("전체 환자 조회 완료: " + patients.size() + "명");
                } else {
                    System.out.println("전체 환자 조회 실패: 데이터가 null");
                }
            });
        }).exceptionally(e -> {
            System.err.println("전체 환자 조회 오류: " + e.getMessage());
            Platform.runLater(() -> {
                showInfo("전체 환자 조회 실패: " + e.getMessage());
            });
            return null;
        });
    }

    private void loadTodayPatients() {
        System.out.println("금일 문진 조회 시작");
        dataManager.loadTodayPatients(patientData, 
            errorMsg -> {
                System.err.println("금일 환자 조회 실패: " + errorMsg);
                showInfo("금일 환자 조회 실패: " + errorMsg);
            },
            () -> {
                System.out.println("금일 환자 조회 완료: " + patientData.size() + "명");
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
                    // 예약을 날짜순으로 정렬 (최신순)
                    reservations.sort((r1, r2) -> r2.getDate().compareTo(r1.getDate()));
                    
                    ReservationDTO latestReservation = reservations.get(0);
                    interviewApiService.getInterviewByReservationAsync(
                            patient.getUid(), patient.getPatient_id(), latestReservation.getReservation_id()
                    ).thenAccept(interviews -> {
                        Platform.runLater(() -> {
                            if (interviews != null && !interviews.isEmpty()) {
                                MedicalInterviewDTO interview = interviews.get(0);
                                // 예약 날짜를 날짜 라벨에 설정
                                interviewDateLabel.setText(latestReservation.getDate());
                                // 데이터베이스의 실제 값들을 표시
                                symptomLabel.setText(interview.getSymptoms() != null ? interview.getSymptoms() : "-");
                                symptomDurationLabel.setText(interview.getSymptom_duration() != null ? interview.getSymptom_duration() : "-");
                                historyLabel.setText(interview.getPast_medical_history() != null ? interview.getPast_medical_history() : "-");
                                allergyLabel.setText(interview.getAllergy() != null ? interview.getAllergy() : "-");
                                medicationLabel.setText(interview.getCurrent_medication() != null ? interview.getCurrent_medication() : "-");
                            } else {
                                clearInterviewInfo();
                            }
                        });
                    });
                    
                    // 과거 문진 내역 로드 (오늘 날짜 기준으로 이후는 제외)
                    loadPastInterviewHistory(patient, reservations);
                } else {
                    clearInterviewInfo();
                }
            });
        });
    }

    private void loadPastInterviewHistory(PatientDTO patient, List<ReservationDTO> reservations) {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        // 오늘 이전의 예약만 필터링
        List<ReservationDTO> pastReservations = reservations.stream()
                .filter(reservation -> {
                    try {
                        LocalDate reservationDate = LocalDate.parse(reservation.getDate(), formatter);
                        return !reservationDate.isAfter(today); // 오늘 이후는 제외
                    } catch (Exception e) {
                        System.err.println("날짜 파싱 실패: " + reservation.getDate());
                        return false;
                    }
                })
                .collect(Collectors.toList());
        
        historyQuestionnaireListView.getItems().clear();
        
        // 각 과거 예약에 대해 문진 조회
        for (ReservationDTO reservation : pastReservations) {
            interviewApiService.getInterviewByReservationAsync(
                    patient.getUid(), patient.getPatient_id(), reservation.getReservation_id()
            ).thenAccept(interviews -> {
                Platform.runLater(() -> {
                    if (interviews != null && !interviews.isEmpty()) {
                        for (MedicalInterviewDTO interview : interviews) {
                            historyQuestionnaireListView.getItems().add(
                                    reservation.getDate() + " - " + interview.getSymptoms()
                            );
                        }
                    }
                });
            });
        }
    }

    private void clearInterviewInfo() {
        interviewDateLabel.setText("-");
        symptomLabel.setText("-");
        symptomDurationLabel.setText("-");
        historyLabel.setText("-");
        allergyLabel.setText("-");
        medicationLabel.setText("-");
        symytomElse.clear();
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
    private void handleAddQuestionnaire(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/medicon/medicon/view/medic/medic_main/AddQuestionnaireForm.fxml"));
            Parent popupRoot = loader.load();

            Stage popupStage = new Stage();
//            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("추가 문진 작성");
            popupStage.setScene(new Scene(popupRoot));
            popupStage.setResizable(false);
            popupStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}