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
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
    @FXML private Label symptomDurationLabel;
    @FXML private Label historyLabel;
    @FXML private Label allergyLabel;
    @FXML private Label medicationLabel;
    @FXML private TextArea symytomElse;

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
                showAlert("오류", "예약 조회 중 오류가 발생했습니다: " + throwable.getMessage());
            });
            return null;
        });
    }

    /**
     * 예약 목록에서 환자 정보를 가져와서 리스트에 표시
     */
    private void loadPatientsFromReservations(List<ReservationDTO> reservations) {
        ObservableList<PatientDTO> patientList = FXCollections.observableArrayList();
        
        // 각 예약에 대해 환자 정보를 가져옴
        for (ReservationDTO reservation : reservations) {
            patientApiService.getPatientByUidAsync(reservation.getPatient_uid()).thenAccept(patient -> {
                if (patient != null) {
                    Platform.runLater(() -> {
                        if (!patientList.contains(patient)) {
                            patientList.add(patient);
                        }
                    });
                }
            });
        }
        
        Platform.runLater(() -> {
            patientListView.setItems(patientList);
            patientListView.setCellFactory(param -> new ListCell<PatientDTO>() {
                @Override
                protected void updateItem(PatientDTO item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getName() + " (" + item.getGender() + ")");
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
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            showAlert("알림", "검색어를 입력해주세요.");
            return;
        }
        
        patientApiService.getPatientsByNameAsync(keyword).thenAccept(patients -> {
            Platform.runLater(() -> {
                if (patients != null && !patients.isEmpty()) {
                    ObservableList<PatientDTO> patientList = FXCollections.observableArrayList(patients);
                    patientListView.setItems(patientList);
                    patientListView.setCellFactory(param -> new ListCell<PatientDTO>() {
                        @Override
                        protected void updateItem(PatientDTO item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty || item == null) {
                                setText(null);
                            } else {
                                setText(item.getName() + " (" + item.getGender() + ")");
                            }
                        }
                    });
                } else {
                    showAlert("알림", "검색 결과가 없습니다.");
                    patientListView.getItems().clear();
                }
            });
        }).exceptionally(throwable -> {
            Platform.runLater(() -> {
                showAlert("오류", "검색 중 오류가 발생했습니다: " + throwable.getMessage());
            });
            return null;
        });
    }

    /**
     * 환자 상세 정보 로드
     */
    private void loadPatientDetails(PatientDTO patient) {
        // 환자 기본 정보 표시
        patientNameLabel.setText(patient.getName());
        patientGenderLabel.setText(patient.getGender());
        patientAgeLabel.setText(String.valueOf(calculateAge(patient.getRnn())));
        patientPhoneLabel.setText(patient.getPhone());
        
        // 해당 환자의 예약 정보를 가져와서 최근 문진 로드
        reservationApiService.getReservationsByPatientId(patient.getUid()).thenAccept(reservations -> {
            if (reservations != null && !reservations.isEmpty()) {
                // 예약을 날짜순으로 정렬 (최신순)
                reservations.sort((r1, r2) -> r2.getDate().compareTo(r1.getDate()));
                selectedReservation = reservations.get(0); // 가장 최근 예약
                
                // 최근 문진 정보 로드
                loadRecentMedicalInterview(patient.getUid(), patient.getUid(), selectedReservation.getReservation_id());
            } else {
                clearMedicalInterviewDisplay();
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
        // 데이터베이스의 실제 값들을 표시
        symptomLabel.setText(interview.getSymptoms() != null ? interview.getSymptoms() : "-");
                        symptomDurationLabel.setText(interview.getSymptom_duration() != null ? interview.getSymptom_duration() : "-");
        historyLabel.setText(interview.getPast_medical_history() != null ? interview.getPast_medical_history() : "-");
        allergyLabel.setText(interview.getAllergy() != null ? interview.getAllergy() : "-");
        medicationLabel.setText(interview.getCurrent_medication() != null ? interview.getCurrent_medication() : "-");
    }

    /**
     * 문진 정보 표시 초기화
     */
    private void clearMedicalInterviewDisplay() {
        interviewDateLabel.setText("");
        symptomLabel.setText("");
        symptomDurationLabel.setText("");
        historyLabel.setText("");
        allergyLabel.setText("");
        medicationLabel.setText("");
        symytomElse.clear();
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
     * 과거문진기록 버튼 클릭 처리
     */
    @FXML
    private void handleQuestionnaireHistory(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/medicon/medicon/view/medic/medic_main/Form/TreatmentHistoryForm.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("과거진료이력");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("오류", "과거진료이력 창을 열 수 없습니다: " + e.getMessage());
        }
    }

    /**
     * 진료확인서 버튼 클릭 처리
     */
    @FXML
    private void handleMedicalCertificate(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/medicon/medicon/view/medic/medic_main/Form/AttendenceCertificateForm.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("진료확인서");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("오류", "진료확인서 창을 열 수 없습니다: " + e.getMessage());
        }
    }
    @FXML
    private void handleDiagnosisCertificate(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/medicon/medicon/view/medic/medic_main/Form/DiagnosisCertificateForm.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("진단서");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("오류", "진단서 창을 열 수 없습니다: " + e.getMessage());
        }
    }
    @FXML
    private void handleOpinionCertificate(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/medicon/medicon/view/medic/medic_main/Form/OpinionCertificateForm.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("진료소견서");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("오류", "진료소견서 창을 열 수 없습니다: " + e.getMessage());
        }
    }
    @FXML
    private void handleReferralLetter(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/medicon/medicon/view/medic/medic_main/Form/ReferralLetterForm.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("진료의뢰서");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("오류", "진료의뢰서 창을 열 수 없습니다: " + e.getMessage());
        }
    }
    @FXML
    private void handlePerscription(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/medicon/medicon/view/medic/medic_main/Form/PrescriptionForm.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("처방전");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("오류", "처방전 창을 열 수 없습니다: " + e.getMessage());
        }
    }
}