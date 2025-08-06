package com.medicon.medicon.controller.medic;

import com.medicon.medicon.controller.medic.form.TreatmentHistoryFormController;
import com.medicon.medicon.model.ChartDTO;
import com.medicon.medicon.model.MedicalInterviewDTO;
import com.medicon.medicon.model.PatientDTO;
import com.medicon.medicon.model.ReservationDTO;
import com.medicon.medicon.service.ChartApiService;
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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

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

    // 진단 입력 폼
    @FXML private TextField tfVisitPurpose;
    @FXML private TextField tfDiagnosisName;
    @FXML private TextArea taOpinion;
    @FXML private Button btnSaveChart;

    // 서비스 인스턴스
    private final PatientApiService patientApiService = new PatientApiService();
    private final ReservationApiService reservationApiService = new ReservationApiService();
    private final MedicalInterviewApiService medicalInterviewApiService = new MedicalInterviewApiService();
    private final ChartApiService chartService = new ChartApiService();

    // 선택된 환자 UID
    private String currentPatientUid;
    // 예시로 고정된 의사 UID, 실제 로그인 정보 사용 권장
    private final String currentDoctorUid = "1";

    @FXML
    public void initialize() {
        // 오늘 예약된 환자 조회
        registerPatientButton.setOnAction(this::handleTodayReservations);
        // 환자 검색
        searchButton.setOnAction(this::handleSearch);

        // 환자 선택 시 상세 정보 및 문진 불러오기
        patientListView.getSelectionModel().selectedItemProperty().addListener((obs, oldP, newP) -> {
            if (newP != null) {
                currentPatientUid = newP.getUid();
                loadPatientDetails(newP);
            }
        });

        // 저장 버튼 핸들러 등록
        btnSaveChart.setOnAction(this::handleSaveChart);
    }

    /** 변경사항 저장 */
    @FXML
    private void handleSaveChart(ActionEvent event) {
        if (currentPatientUid == null) {
            showAlert("오류", "먼저 환자를 선택해주세요.");
            return;
        }

        ChartDTO chart = new ChartDTO();
        chart.setChart_id(UUID.randomUUID().toString());
        chart.setPatient_uid(currentPatientUid);
        chart.setDoctor_uid(currentDoctorUid);
        chart.setSymptoms(tfVisitPurpose.getText());
        chart.setDiagnosis(tfDiagnosisName.getText());
        chart.setNote(taOpinion.getText());
        chart.setVisit_date(LocalDate.now().toString());
        chart.setVisit_time(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));

        chartService.saveChartAsync(chart)
                .thenAccept(success -> Platform.runLater(() -> {
                    if (Boolean.TRUE.equals(success)) {
                        showAlert("완료", "진료 차트가 저장되었습니다.");
                        tfVisitPurpose.clear();
                        tfDiagnosisName.clear();
                        taOpinion.clear();
                    } else {
                        showAlert("오류", "차트 저장에 실패했습니다.");
                    }
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> showAlert("오류", "저장 중 오류: " + ex.getMessage()));
                    return null;
                });
    }

    /** 오늘 예약된 환자 불러오기 */
    @FXML
    private void handleTodayReservations(ActionEvent event) {
        String todayStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        reservationApiService.getReservationsByDate(todayStr)
                .thenAccept(reservations -> {
                    if (reservations == null || reservations.isEmpty()) {
                        Platform.runLater(() -> {
                            showAlert("알림", "오늘 예약된 환자가 없습니다.");
                            patientListView.getItems().clear();
                        });
                    } else {
                        loadPatientsFromReservations(reservations);
                    }
                }).exceptionally(e -> {
                    Platform.runLater(() -> showAlert("오류", "예약 조회 중 오류: " + e.getMessage()));
                    return null;
                });
    }

    /** 예약 목록에서 환자 정보 로드 */
    private void loadPatientsFromReservations(List<ReservationDTO> reservations) {
        ObservableList<PatientDTO> list = FXCollections.observableArrayList();
        for (ReservationDTO r : reservations) {
            patientApiService.getPatientByPatientIdAsync(r.getPatient_id())
                    .thenAccept(p -> {
                        if (p != null) {
                            Platform.runLater(() -> { if (!list.contains(p)) list.add(p); });
                        }
                    });
        }
        Platform.runLater(() -> {
            patientListView.setItems(list);
            patientListView.setCellFactory(param -> new ListCell<PatientDTO>() {
                @Override
                protected void updateItem(PatientDTO item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getName() + " (" + item.getGender() + ")");
                }
            });
        });
    }

    /** 환자 이름 검색 */
    @FXML
    private void handleSearch(ActionEvent event) {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            showAlert("알림", "검색어를 입력해주세요.");
            return;
        }
        patientApiService.getPatientsByNameAsync(keyword)
                .thenAccept(patients -> Platform.runLater(() -> {
                    if (patients != null && !patients.isEmpty()) {
                        patientListView.setItems(FXCollections.observableArrayList(patients));
                    } else {
                        showAlert("알림", "검색 결과가 없습니다.");
                        patientListView.getItems().clear();
                    }
                })).exceptionally(e -> {
                    Platform.runLater(() -> showAlert("오류", "검색 중 오류: " + e.getMessage()));
                    return null;
                });
    }

    /** 환자 상세 정보 및 최근 문진 로드 */
    private void loadPatientDetails(PatientDTO patient) {
        patientNameLabel.setText(patient.getName());
        patientGenderLabel.setText(patient.getGender());
        patientAgeLabel.setText(String.valueOf(calculateAge(patient.getRnn())));
        patientPhoneLabel.setText(patient.getPhone());
        reservationApiService.getReservationsByPatientId(patient.getPatient_id())
                .thenAccept(reservations -> {
                    if (reservations != null && !reservations.isEmpty()) {
                        reservations.sort((r1, r2) -> r2.getDate().compareTo(r1.getDate()));
                        ReservationDTO recent = reservations.get(0);
                        loadRecentMedicalInterview(patient.getUid(), patient.getPatient_id(), recent.getReservation_id());
                    } else {
                        clearMedicalInterviewDisplay();
                    }
                });
    }

    private void loadRecentMedicalInterview(String uid, String patientId, String reservationId) {
        medicalInterviewApiService.getInterviewByReservationAsync(uid, patientId, reservationId)
                .thenAccept(interviews -> Platform.runLater(() -> {
                    if (interviews != null && !interviews.isEmpty()) {
                        MedicalInterviewDTO mi = interviews.get(0);
                        symptomLabel.setText(mi.getSymptoms());
                        symptomDurationLabel.setText(mi.getSymptom_duration());
                        historyLabel.setText(mi.getPast_medical_history());
                        allergyLabel.setText(mi.getAllergy());
                        medicationLabel.setText(mi.getCurrent_medication());
//                        interviewDateLabel.setText(mi.getVisit_date());
                    } else {
                        clearMedicalInterviewDisplay();
                    }
                }));
    }

    private void clearMedicalInterviewDisplay() {
        interviewDateLabel.setText("");
        symptomLabel.setText("");
        symptomDurationLabel.setText("");
        historyLabel.setText("");
        allergyLabel.setText("");
        medicationLabel.setText("");
        symytomElse.clear();
    }

    private int calculateAge(String rnn) {
        try {
            int year = Integer.parseInt(rnn.substring(0,2));
            int fullYear = year > 23 ? 1900 + year : 2000 + year;
            return LocalDate.now().getYear() - fullYear + 1;
        } catch (Exception e) {
            return 0;
        }
    }

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
        // 1) 환자 선택 여부 체크
        if (currentPatientUid == null) {
            showAlert("오류", "먼저 환자를 선택해주세요.");
            return;
        }

        try {
            // 2) FXML 로드
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/medicon/medicon/view/medic/medic_main/form/TreatmentHistoryForm.fxml"
            ));
            Parent root = loader.load();

            // 3) 컨트롤러에 UID 전달
            TreatmentHistoryFormController controller = loader.getController();
            controller.setPatientUid(currentPatientUid);

            // 4) 새로운 모달 창 띄우기
            Stage stage = new Stage();
            stage.setTitle("과거 진료 이력");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("오류", "과거 진료 이력 창을 열 수 없습니다: " + e.getMessage());
        }
    }

    /**
     * 진료확인서 버튼 클릭 처리
     */
    @FXML
    private void handleMedicalCertificate(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/medicon/medicon/view/medic/medic_main/form/AttendenceCertificateForm.fxml"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/medicon/medicon/view/medic/medic_main/form/DiagnosisCertificateForm.fxml"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/medicon/medicon/view/medic/medic_main/form/OpinionCertificateForm.fxml"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/medicon/medicon/view/medic/medic_main/form/ReferralLetterForm.fxml"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/medicon/medicon/view/medic/medic_main/form/PrescriptionForm.fxml"));
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