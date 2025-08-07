package com.medicon.medicon.controller.medic;

import com.medicon.medicon.controller.medic.form.AttendenceCertificateFormController;
import com.medicon.medicon.controller.medic.form.TreatmentHistoryFormController;
import com.medicon.medicon.model.*;
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

    @FXML private Label doctorLabel;
    @FXML private Label doctorNameLabel;

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
    @FXML private TextArea symptomElse;

    // 진단 입력 폼
    @FXML private TextField tfVisitPurpose;
    @FXML private TextField tfDiagnosisName;
    @FXML private TextArea taOpinion;
    @FXML private TextArea taAttachedFiles;
    @FXML private Button btnSaveChart;

    // 서비스 인스턴스
    private final PatientApiService patientApiService = new PatientApiService();
    private final ReservationApiService reservationApiService = new ReservationApiService();
    private final MedicalInterviewApiService medicalInterviewApiService = new MedicalInterviewApiService();
    private final ChartApiService chartService = new ChartApiService();

    private PatientDTO selectedPatient; //선택한 환자 DTO
    private StaffUser staffUser;      // 로그인한 의사 DTO (로그인 시 저장해둘 것)
    private ChartDTO selectedChart;         // 최근 차트 DTO(필요하다면)
    
    // 로그인 되어있는 유저 정보 불러옴
    public void setStaffUser(StaffUser staffUser) {
        this.staffUser = staffUser;
        // 화면에 자동입력 등 필요 처리
        doctorNameLabel.setText(staffUser.getName());
    }

    // 선택된 환자 UID
    private String currentPatientUid;
    private String medicUid; // 메인 컨트롤러에서 setMedicUid로 받은 값

//    private final UserApiService userApiService = new UserApiService();
//
//    public void loadDoctorDetailByUid() {
//        if (medicUid == null) {
//            doctorNameLabel.setText("의사 정보 없음");
//            doctorLabel.setVisible(false);
//            doctorNameLabel.setVisible(false);
//            return;
//        }
//
//        userApiService.getUserByUidAsync(medicUid).thenAccept(user -> {
//            Platform.runLater(() -> {
//                if (user == null || user.getName() == null) {
//                    doctorNameLabel.setText("의사 정보 없음");
//                    doctorLabel.setVisible(false);
//                    doctorNameLabel.setVisible(false);
//                } else if ("의사".equals(user.getRole())) {
//                    doctorNameLabel.setText(user.getName());
//                    doctorLabel.setVisible(true);
//                    doctorNameLabel.setVisible(true);
//                } else {
//                    doctorLabel.setVisible(false);
//                    doctorNameLabel.setVisible(false);
//                }
//            });
//        });
//    }
    public void setMedicUid(String medicUid) {
        this.medicUid = medicUid;
//        loadDoctorDetailByUid(); // uid가 주어질 때 바로 조회
    }


    @FXML
    public void loadDoctorDetail(StaffUser doctor){
        if (doctor == null || doctor.getName() == null) {
            doctorNameLabel.setText("의사 정보 없음");
            doctorLabel.setVisible(false);
            doctorNameLabel.setVisible(false); // 라벨 자체를 아예 숨김

            return;
        }
        // 의사만 보이게
        if (doctor.getRole().equals("의사")) {
            doctorNameLabel.setText(doctor.getName());
            doctorLabel.setVisible(true);
            doctorNameLabel.setVisible(true); // 라벨 보이게
        } else {
            doctorLabel.setVisible(false);
            doctorNameLabel.setVisible(false); // 간호사 또는 기타면 라벨 숨김
        }

    }


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
        // <----- 추가! ----->
        // 환자 리스트에서 클릭 시 항상 로드(같은 환자 연속 클릭 포함)
        patientListView.setOnMouseClicked(event -> {
            PatientDTO selected = patientListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                currentPatientUid = selected.getUid();
                loadPatientDetails(selected);
            }
        });

        // 저장 버튼 핸들러 등록
        btnSaveChart.setOnAction(this::handleSaveChart);
    }

    /** 진료차트 저장 */
    @FXML
    private void handleSaveChart(ActionEvent event) {
        if (currentPatientUid == null) {
            showAlert("오류", "먼저 환자를 선택해주세요.");
            return;
        }

        ChartDTO chart = new ChartDTO();
        chart.setChart_id(UUID.randomUUID().toString());
        chart.setPatient_uid(currentPatientUid);
//        chart.setDoctor_uid(currentDoctorUid);
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
            }).exceptionally(e -> {
                System.err.println("환자 정보 로드 실패: " + e.getMessage());
                return null;
            });
        }

        Platform.runLater(() -> {
            patientListView.setItems(patientList);
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

        this.selectedPatient = patient;

        // 진단 입력 폼 초기화 및 활성화
        clearDiagnosisInput();

        // 해당 환자의 예약 정보를 가져와서 최근 문진 로드
        reservationApiService.getReservationsByPatientId(patient.getUid()).thenAccept(reservations -> {
            if (reservations != null && !reservations.isEmpty()) {
                // 예약을 날짜순으로 정렬 (최신순)
                reservations.sort((r1, r2) -> r2.getDate().compareTo(r1.getDate()));
                ReservationDTO recent = reservations.get(0);
                // 최근 문진 정보 로드 - 환자관리와 동일한 방식 사용
                loadRecentMedicalInterview(patient.getUid(), patient.getUid(), recent.getReservation_id(), recent.getDate());            } else {
                clearMedicalInterviewDisplay();
            }
        }).exceptionally(e -> {
            System.err.println("환자 예약 정보 로드 실패: " + e.getMessage());
            Platform.runLater(() -> clearMedicalInterviewDisplay());
            return null;
        });
    }

    private void loadRecentMedicalInterview(String uid, String patientId, String reservationId, String reservationDate) {
        medicalInterviewApiService.getInterviewByReservationAsync(uid, patientId, reservationId)
                .thenAccept(interviews -> Platform.runLater(() -> {
                    if (interviews != null && !interviews.isEmpty()) {

                        MedicalInterviewDTO mi = interviews.get(0);

                        symptomLabel.setText(mi.getSymptoms() != null ? mi.getSymptoms() : "");
                        symptomDurationLabel.setText(mi.getSymptom_duration() != null ? mi.getSymptom_duration() : "");
                        historyLabel.setText(mi.getPast_medical_history() != null ? mi.getPast_medical_history() : "");
                        allergyLabel.setText(mi.getAllergy() != null ? mi.getAllergy() : "");
                        medicationLabel.setText(mi.getCurrent_medication() != null ? mi.getCurrent_medication() : "");
                        symptomElse.setText(""); // other_symptoms 필드가 없으므로 빈 문자열로 설정
                        interviewDateLabel.setText(reservationDate);
                        System.out.println("문진 정보 로드 완료: " + mi.getSymptoms());
                    } else {
                        clearMedicalInterviewDisplay();
                        System.out.println("문진 정보가 없습니다.");
                    }
                })).exceptionally(e -> {
                    System.err.println("문진 정보 로드 실패: " + e.getMessage());
                    Platform.runLater(() -> clearMedicalInterviewDisplay());
                    return null;
                });
    }

    private void clearMedicalInterviewDisplay() {
        interviewDateLabel.setText("");
        symptomLabel.setText("");
        symptomDurationLabel.setText("");
        historyLabel.setText("");
        allergyLabel.setText("");
        medicationLabel.setText("");
        symptomElse.clear();
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
    //진단 입력 폼 초기화 함수
    private void clearDiagnosisInput() {
        tfVisitPurpose.clear();
        tfDiagnosisName.clear();
        taOpinion.clear();
        taAttachedFiles.clear(); // 만약 첨부파일도 매번 초기화 원하면
        tfVisitPurpose.setDisable(false);
        tfDiagnosisName.setDisable(false);
        taOpinion.setDisable(false);
        taAttachedFiles.setDisable(false);
        btnSaveChart.setDisable(false);
        // 만약 파일첨부 버튼 등도 있다면 setDisable(false) 추가
    }


    public void showChartDetail(String chartId) {
        // 차트 서비스 통해서 차트 상세정보 비동기 조회 (ChartDTO 기준)
        chartService.getChartByChartIdAsync(chartId)
                .thenAccept(chart -> Platform.runLater(() -> {
                    if (chart == null) {
                        // 알림 등 처리
                        return;
                    }
                    // 1. 데이터 세팅
                    tfVisitPurpose.setText(chart.getSymptoms());
                    tfDiagnosisName.setText(chart.getDiagnosis());
                    taOpinion.setText(chart.getNote());
//                    taAttachedFiles.setText(chart.getAttachedFiles()); // 필요시

                    // 2. 비활성화 (읽기전용)
                    tfVisitPurpose.setDisable(true);
                    tfDiagnosisName.setDisable(true);
                    taOpinion.setDisable(true);
                    taAttachedFiles.setDisable(true); // 필요시
                    btnSaveChart.setDisable(true);

//                    // 파일첨부 버튼도 비활성화
//                    fileAttachButton.setDisable(true); // 필요시
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        // 예외 알림 등
                    });
                    return null;
                });
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
            controller.setChartSelectionListener(chartId -> {
                showChartDetail(chartId);
            });

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
    private void handleAttendenceCertificate(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/medicon/medicon/view/medic/medic_main/form/AttendenceCertificateForm.fxml"));
            Parent root = loader.load();

            AttendenceCertificateFormController controller = loader.getController();

            if (selectedPatient != null)
                controller.setPatientInfo(selectedPatient);

//            if (staffUser != null)
//                controller.setDoctorInfo(staffUser);

            if (selectedChart != null)
                controller.setChartInfo(selectedChart);

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