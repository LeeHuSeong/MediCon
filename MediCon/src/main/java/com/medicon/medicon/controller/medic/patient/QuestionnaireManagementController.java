package com.medicon.medicon.controller.medic.patient;

import com.medicon.medicon.model.MedicalInterviewDTO;
import com.medicon.medicon.model.PatientDTO;
import com.medicon.medicon.model.ReservationDTO;
import com.medicon.medicon.service.PatientApiService;
import com.medicon.medicon.service.ReservationApiService;
import com.medicon.medicon.service.MedicalInterviewApiService;
import com.medicon.medicon.controller.medic.AddQuestionnaireFormController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

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
    @FXML private Button addReservationButton;
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
        reservationApiService.getReservationsByPatientId(patient.getUid()).thenAccept(reservations -> {
            Platform.runLater(() -> {
                if (reservations != null && !reservations.isEmpty()) {
                    // 예약을 날짜순으로 정렬 (최신순)
                    reservations.sort((r1, r2) -> r2.getDate().compareTo(r1.getDate()));
                    
                    ReservationDTO latestReservation = reservations.get(0);
                    interviewApiService.getInterviewByReservationAsync(
                            patient.getUid(), patient.getUid(), latestReservation.getReservation_id()
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
                    patient.getUid(), patient.getUid(), reservation.getReservation_id()
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
        // 선택된 환자가 있는지 확인
        PatientDTO selectedPatient = patientListView.getSelectionModel().getSelectedItem();
        if (selectedPatient == null) {
            showInfo("환자를 선택해주세요.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/medicon/medicon/view/medic/medic_main/AddQuestionnaireForm.fxml"));
            Parent root = loader.load();

            // 컨트롤러 가져오기
            AddQuestionnaireFormController controller = loader.getController();
            
            // 선택된 환자의 최신 예약 정보 가져오기
            ReservationDTO latestReservation = getLatestReservation(selectedPatient);
            if (latestReservation != null) {
                controller.setSelectedPatient(selectedPatient, latestReservation);
            } else {
                showInfo("해당 환자의 예약 정보가 없습니다. 먼저 예약을 생성해주세요.");
                return;
            }

            Stage stage = new Stage();
            stage.setTitle("추가 문진 작성 - " + selectedPatient.getName());
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showInfo("문진 작성 창을 열 수 없습니다.");
        }
    }

    @FXML
    private void handleAddReservation(ActionEvent event) {
        // 선택된 환자가 있는지 확인
        PatientDTO selectedPatient = patientListView.getSelectionModel().getSelectedItem();
        if (selectedPatient == null) {
            showInfo("환자를 선택해주세요.");
            return;
        }

        // 간단한 예약 추가 다이얼로그 생성
        Dialog<ReservationDTO> dialog = new Dialog<>();
        dialog.setTitle("예약 추가 - " + selectedPatient.getName());
        dialog.setHeaderText("새로운 예약을 추가합니다.");

        // 다이얼로그 버튼 설정
        ButtonType saveButtonType = new ButtonType("예약 추가", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // 다이얼로그 내용 생성
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        DatePicker datePicker = new DatePicker();
        datePicker.setValue(LocalDate.now());
        
        ComboBox<String> timeComboBox = new ComboBox<>();
        timeComboBox.getItems().addAll(
            "09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
            "13:00", "13:30", "14:00", "14:30", "15:00", "15:30",
            "16:00", "16:30", "17:00", "17:30"
        );
        timeComboBox.setValue("09:00");

        ComboBox<String> departmentComboBox = new ComboBox<>();
        departmentComboBox.getItems().addAll(
            "내과", "외과", "정형외과", "신경외과", "소아과", "산부인과",
            "피부과", "안과", "이비인후과", "정신건강의학과", "재활의학과"
        );
        departmentComboBox.setValue("내과");

        grid.add(new Label("날짜:"), 0, 0);
        grid.add(datePicker, 1, 0);
        grid.add(new Label("시간:"), 0, 1);
        grid.add(timeComboBox, 1, 1);
        grid.add(new Label("진료과:"), 0, 2);
        grid.add(departmentComboBox, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // 결과 처리
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                ReservationDTO newReservation = new ReservationDTO();
                newReservation.setPatient_id(selectedPatient.getUid());
                newReservation.setDate(datePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                newReservation.setTime(timeComboBox.getValue());
                newReservation.setDepartment(departmentComboBox.getValue());
                newReservation.setReservation_id("RES_" + System.currentTimeMillis());
                return newReservation;
            }
            return null;
        });

        Optional<ReservationDTO> result = dialog.showAndWait();
        result.ifPresent(reservation -> {
            // 서버에 예약 저장
            reservationApiService.saveReservationAsync(reservation).thenAccept(success -> {
                Platform.runLater(() -> {
                    if (success) {
                        showInfo("예약이 성공적으로 저장되었습니다: " + reservation.getDate() + " " + reservation.getTime());
                        // 환자 목록 새로고침
                        loadAllPatients();
                    } else {
                        showInfo("예약 저장에 실패했습니다.");
                    }
                });
            });
        });
    }
    
    // 선택된 환자의 오늘 날짜 예약 정보 가져오기
    private ReservationDTO getLatestReservation(PatientDTO patient) {
        try {
            // 환자의 모든 예약 조회
            List<ReservationDTO> allReservations = reservationApiService.getReservationsByPatientId(patient.getUid()).get();
            
            if (allReservations != null && !allReservations.isEmpty()) {
                // 오늘 날짜의 예약 찾기
                String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                
                for (ReservationDTO reservation : allReservations) {
                    if (today.equals(reservation.getDate())) {
                        System.out.println("오늘 날짜 예약 발견: " + reservation.getReservation_id());
                        return reservation;
                    }
                }
                
                // 오늘 날짜 예약이 없으면 가장 최근 예약 반환
                System.out.println("오늘 날짜 예약이 없어서 최근 예약 사용: " + allReservations.get(0).getReservation_id());
                return allReservations.get(0);
            }
        } catch (Exception e) {
            System.err.println("예약 조회 실패: " + e.getMessage());
        }
        
        // 예약이 없거나 오류 발생 시 null 반환
        System.err.println("사용 가능한 예약이 없습니다.");
        return null;
    }
}
