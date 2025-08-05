package com.medicon.medicon.controller.medic;

import com.medicon.medicon.model.MedicalInterviewDTO;
import com.medicon.medicon.model.PatientDTO;
import com.medicon.medicon.model.ReservationDTO;
import com.medicon.medicon.service.MedicalInterviewApiService;
import com.medicon.medicon.service.PatientApiService;
import com.medicon.medicon.service.ReservationApiService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TreatmentManagementController {

    @FXML private Button registerPatientButton;
    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private ListView<PatientDTO> patientListView;
    
    // 환자 정보 라벨들
    @FXML private Label patientNameLabel;
    @FXML private Label patientGenderLabel;
    @FXML private Label patientAgeLabel;
    @FXML private Label patientPhoneLabel;
    
    // 최근 문진 라벨들
    @FXML private Label interviewDateLabel;
    @FXML private Label symptomLabel;
    @FXML private Label historyLabel;
    @FXML private HBox symptoms;
    @FXML private HBox onsetOfSymptoms;
    @FXML private Label allergyLabel;
    @FXML private Label medicationLabel;
    @FXML private TextArea symytomElse;
    
    // 진료확인서 버튼
    @FXML private Button medicalCertificateBtn;

    private final PatientApiService patientApiService = new PatientApiService();
    private final ReservationApiService reservationApiService = new ReservationApiService();
    private final MedicalInterviewApiService medicalInterviewApiService = new MedicalInterviewApiService();
    
    private PatientDTO selectedPatient;
    private ReservationDTO selectedReservation;

    @FXML
    public void initialize() {
        // 예약 환자 조회 버튼 클릭 이벤트
        registerPatientButton.setOnAction(this::handleTodayReservations);
        
        // 검색 버튼 클릭 이벤트
        searchButton.setOnAction(this::handleSearch);
        
        // 환자 리스트 선택 이벤트
        patientListView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    selectedPatient = newValue;
                    loadPatientDetails(newValue);
                }
            }
        );
    }

    /**
     * 오늘 예약된 환자 조회
     */
    @FXML
    private void handleTodayReservations(ActionEvent event) {
        LocalDate today = LocalDate.now();
        String todayStr = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        
        reservationApiService.getReservationsByDate(todayStr).thenAccept(reservations -> {
            if (reservations == null || reservations.isEmpty()) {
                Platform.runLater(() -> {
                    showAlert("알림", "오늘 예약된 환자가 없습니다.");
                    patientListView.getItems().clear();
                });
                return;
            }
            
            // 예약된 환자들의 정보를 가져와서 리스트에 표시
            loadPatientsFromReservations(reservations);
        }).exceptionally(throwable -> {
            Platform.runLater(() -> {
                showAlert("오류", "예약 환자 조회 중 오류가 발생했습니다: " + throwable.getMessage());
            });
            return null;
        });
    }

    /**
     * 예약 목록에서 환자 정보를 가져와서 리스트에 표시
     */
    private void loadPatientsFromReservations(List<ReservationDTO> reservations) {
        ObservableList<PatientDTO> todayPatients = FXCollections.observableArrayList();
        
        for (ReservationDTO reservation : reservations) {
            patientApiService.getPatientByPatientIdAsync(reservation.getPatient_id()).thenAccept(patient -> {
                if (patient != null) {
                    Platform.runLater(() -> {
                        if (!todayPatients.contains(patient)) {
                            todayPatients.add(patient);
                        }
                    });
                }
            });
        }
        
        Platform.runLater(() -> {
            patientListView.setItems(todayPatients);
            patientListView.setCellFactory(param -> new ListCell<PatientDTO>() {
                @Override
                protected void updateItem(PatientDTO item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getName() + " (" + item.getGender() + ", " + calculateAge(item.getRnn()) + "세)");
                    }
                }
            });
        });
    }

    /**
     * 환자 검색
     */
    @FXML
    private void handleSearch(ActionEvent event) {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            showAlert("알림", "검색어를 입력해주세요.");
            return;
        }
        
        patientApiService.getPatientsByNameAsync(searchTerm).thenAccept(patients -> {
            Platform.runLater(() -> {
                if (patients == null || patients.isEmpty()) {
                    showAlert("알림", "검색 결과가 없습니다.");
                    patientListView.getItems().clear();
                } else {
                    patientListView.setItems(FXCollections.observableArrayList(patients));
                }
            });
        }).exceptionally(throwable -> {
            Platform.runLater(() -> {
                showAlert("오류", "환자 검색 중 오류가 발생했습니다: " + throwable.getMessage());
            });
            return null;
        });
    }

    /**
     * 선택된 환자의 상세 정보 로드
     */
    private void loadPatientDetails(PatientDTO patient) {
        // 환자 기본 정보 표시
        patientNameLabel.setText(patient.getName());
        patientGenderLabel.setText(patient.getGender());
        patientAgeLabel.setText(String.valueOf(calculateAge(patient.getRnn())));
        patientPhoneLabel.setText(patient.getPhone());
        
        // 해당 환자의 오늘 예약 찾기
        LocalDate today = LocalDate.now();
        String todayStr = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        
        reservationApiService.getReservationsByPatientId(patient.getPatient_id()).thenAccept(reservations -> {
            ReservationDTO todayReservation = null;
            for (ReservationDTO reservation : reservations) {
                if (todayStr.equals(reservation.getDate())) {
                    todayReservation = reservation;
                    break;
                }
            }
            
            if (todayReservation != null) {
                selectedReservation = todayReservation;
                // 최근 문진 정보 로드
                loadRecentMedicalInterview(patient.getUid(), patient.getPatient_id(), todayReservation.getReservation_id());
            }
        });
    }

    /**
     * 최근 문진 정보 로드
     */
    private void loadRecentMedicalInterview(String uid, String patientId, String reservationId) {
        medicalInterviewApiService.getInterviewByReservationAsync(uid, patientId, reservationId).thenAccept(interviews -> {
            Platform.runLater(() -> {
                if (interviews != null && !interviews.isEmpty()) {
                    MedicalInterviewDTO latestInterview = interviews.get(0); // 가장 최근 문진
                    displayMedicalInterview(latestInterview);
                    
                    // 예약 날짜를 날짜 라벨에 설정
                    if (selectedReservation != null) {
                        interviewDateLabel.setText(selectedReservation.getDate());
                    }
                } else {
                    clearMedicalInterviewDisplay();
                }
            });
        });
    }

    /**
     * 문진 정보를 UI에 표시
     */
    private void displayMedicalInterview(MedicalInterviewDTO interview) {
        // 증상 표시
        symptomLabel.setText(interview.getSymptoms());
        
        // 과거 병력 표시
        historyLabel.setText(interview.getPast_medical_history());
        
        // 알레르기 표시
        allergyLabel.setText(interview.getAllergy());
        
        // 복용 중인 약 표시
        medicationLabel.setText(interview.getCurrent_medication());
        
        // 증상 시작 시점 표시 (라디오 버튼 설정)
        String symptomDuration = interview.getSymptom_duration();
        if (symptomDuration != null) {
            for (javafx.scene.Node node : onsetOfSymptoms.getChildren()) {
                if (node instanceof RadioButton) {
                    RadioButton radioButton = (RadioButton) node;
                    if (radioButton.getText().trim().equals(symptomDuration.trim())) {
                        radioButton.setSelected(true);
                        break;
                    }
                }
            }
        }
    }

    /**
     * 문진 정보 표시 초기화
     */
    private void clearMedicalInterviewDisplay() {
        interviewDateLabel.setText("");
        symptomLabel.setText("");
        historyLabel.setText("");
        allergyLabel.setText("");
        medicationLabel.setText("");
        symytomElse.clear();
        
        // 라디오 버튼 선택 해제
        for (javafx.scene.Node node : onsetOfSymptoms.getChildren()) {
            if (node instanceof RadioButton) {
                ((RadioButton) node).setSelected(false);
            }
        }
        
        // 체크박스 선택 해제
        for (javafx.scene.Node node : symptoms.getChildren()) {
            if (node instanceof CheckBox) {
                ((CheckBox) node).setSelected(false);
            }
        }
    }

    /**
     * 주민등록번호로 나이 계산
     */
    private int calculateAge(String rnn) {
        if (rnn == null || rnn.length() < 6) {
            return 0;
        }
        
        try {
            String birthYearStr = rnn.substring(0, 2);
            int birthYear = Integer.parseInt(birthYearStr);
            
            // 2000년 이전 출생인지 2000년 이후 출생인지 판단
            int fullBirthYear;
            if (birthYear > 23) { // 1924년 ~ 1999년 출생
                fullBirthYear = 1900 + birthYear;
            } else { // 2000년 ~ 2023년 출생
                fullBirthYear = 2000 + birthYear;
            }
            
            int currentYear = LocalDate.now().getYear();
            return currentYear - fullBirthYear + 1; // 한국식 나이
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * 알림창 표시
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * 진료확인서 버튼 클릭 처리
     */
    @FXML
    private void handleMedicalCertificate(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/medicon/medicon/view/medic/medic_main/MedicalCertificateForm.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("진료확인서");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("오류", "진료확인서 창을 열 수 없습니다: " + e.getMessage());
        }
    }
}