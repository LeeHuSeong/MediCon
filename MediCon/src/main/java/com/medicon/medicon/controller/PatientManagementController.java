package com.medicon.medicon.controller;

import com.medicon.medicon.model.PatientDTO;
import com.medicon.medicon.model.ReservationDTO;
import com.medicon.medicon.model.MedicalInterviewDTO;
import com.medicon.medicon.service.PatientApiService;
import com.medicon.medicon.service.ReservationApiService;
import com.medicon.medicon.service.MedicalInterviewApiService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class PatientManagementController implements Initializable {

    @FXML private Button registerPatientButton;
    @FXML private Button todayPatientButton;
    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private ListView<PatientDTO> patientListView;
    @FXML private TextField nameField;
    @FXML private TextField genderField;
    @FXML private TextField birthField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private TextField addressField;
    @FXML private TextField detailAddressField;
    @FXML private Button changePatientButton;
    @FXML private Button updatePatientButton;
    @FXML private Label symptomLabel;
    @FXML private Label historyLabel;
    @FXML private Label allergyLabel;
    @FXML private Label medicationLabel;
    @FXML private Label dateLabel;
    @FXML private Label timeLabel;
    @FXML private Label departmentLabel;
    @FXML private ListView<String> historyListView;

    private final PatientApiService patientApiService;
    private final ReservationApiService reservationApiService;
    private final MedicalInterviewApiService medicalInterviewApiService;
    private final ObservableList<PatientDTO> patientData;
    private final ObservableList<String> historyData;
    private PatientDTO selectedPatient;
    private boolean isEditMode = false;

    public PatientManagementController() {
        this.patientApiService = new PatientApiService();
        this.reservationApiService = new ReservationApiService();
        this.medicalInterviewApiService = new MedicalInterviewApiService();
        this.patientData = FXCollections.observableArrayList();
        this.historyData = FXCollections.observableArrayList();
        this.selectedPatient = null;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupComponents();
        setupEventHandlers();
        Platform.runLater(() -> {
            loadAllPatients();
            setEditMode(false);
        });
    }

    private void setupComponents() {
        patientListView.setItems(patientData);
        patientListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(PatientDTO patient, boolean empty) {
                super.updateItem(patient, empty);
                if (empty || patient == null) setText(null);
                else setText(String.format("%s (%s)", patient.getName(), patient.getGender()));
            }
        });
        historyListView.setItems(historyData);
        patientListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                selectedPatient = newSel;
                displayPatientInfo(newSel);
                loadPatientHistory(newSel);
            } else {
                selectedPatient = null;
                clearAllFields();
            }
        });
    }

    private void setupEventHandlers() {
        searchButton.setOnAction(event -> handleSearch());
        searchField.setOnAction(event -> handleSearch());
        registerPatientButton.setOnAction(event -> handleRegisterPatient());
        todayPatientButton.setOnAction(event -> handleTodayPatients());
        changePatientButton.setOnAction(event -> handleChangePatient());
        updatePatientButton.setOnAction(event -> handleUpdatePatient());
    }

    private void loadAllPatients() {
        patientApiService.getAllPatientsAsync().thenAccept(patients -> {
            Platform.runLater(() -> {
                patientData.clear();
                if (patients != null) patientData.addAll(patients);
            });
        }).exceptionally(e -> {
            Platform.runLater(() -> showError("환자 목록 로드 실패: " + e.getMessage()));
            return null;
        });
    }

    @FXML
    private void handleSearch() {
        String searchName = searchField.getText().trim();
        if (searchName.isEmpty()) {
            loadAllPatients();
            return;
        }
        patientApiService.getPatientsByNameAsync(searchName).thenAccept(patients -> {
            Platform.runLater(() -> {
                patientData.clear();
                if (patients != null) patientData.addAll(patients);
            });
        }).exceptionally(e -> {
            Platform.runLater(() -> showError("검색 실패: " + e.getMessage()));
            return null;
        });
    }

    private void displayPatientInfo(PatientDTO patient) {
        nameField.setText(patient.getName());
        genderField.setText(patient.getGender());
        birthField.setText(patient.getRnn() != null && patient.getRnn().length() >= 6 ? patient.getRnn().substring(0, 6) : "");
        phoneField.setText(patient.getPhone());
        emailField.setText(patient.getEmail());
        addressField.setText(patient.getAddress());
        detailAddressField.setText("");

        reservationApiService.getReservationsByPatientId(patient.getPatient_id())
                .thenAccept(reservations -> {
                    Platform.runLater(() -> {
                        if (reservations != null && !reservations.isEmpty()) {
                            ReservationDTO reservation = reservations.get(0);
                            dateLabel.setText(reservation.getDate());
                            timeLabel.setText(reservation.getTime());

                            // ✅ department 필드 사용하도록 수정
                            String department = reservation.getDepartment();
                            if (department != null && !department.trim().isEmpty()) {
                                departmentLabel.setText(department);
                            } else {
                                departmentLabel.setText("일반의학과");
                            }

                            medicalInterviewApiService.getInterviewByReservationAsync(
                                    patient.getUid(),
                                    patient.getPatient_id(),
                                    reservation.getReservation_id()
                            ).thenAccept(interviews -> {
                                Platform.runLater(() -> {
                                    if (interviews != null && !interviews.isEmpty()) {
                                        MedicalInterviewDTO interview = interviews.get(0);
                                        symptomLabel.setText(interview.getSymptoms());
                                        historyLabel.setText(interview.getPast_medical_history());
                                        allergyLabel.setText(interview.getAllergy());
                                        medicationLabel.setText(interview.getCurrent_medication());
                                    } else {
                                        clearMedicalInfo();
                                    }
                                });
                            });
                        } else {
                            dateLabel.setText("-");
                            timeLabel.setText("-");
                            departmentLabel.setText("-");
                            clearMedicalInfo();
                        }
                    });
                });
    }

    private void clearMedicalInfo() {
        symptomLabel.setText("-");
        historyLabel.setText("-");
        allergyLabel.setText("-");
        medicationLabel.setText("-");
    }

    private void loadPatientHistory(PatientDTO patient) {
        historyData.clear();

        // 환자의 모든 예약 정보를 가져와서 방문 이력 생성
        reservationApiService.getReservationsByPatientId(patient.getPatient_id())
                .thenAccept(reservations -> {
                    Platform.runLater(() -> {
                        if (reservations != null && !reservations.isEmpty()) {
                            // 예약 날짜순으로 정렬 (최신순)
                            reservations.sort((r1, r2) -> r2.getDate().compareTo(r1.getDate()));

                            for (ReservationDTO reservation : reservations) {
                                // ✅ 실제 department 값 사용
                                String department = reservation.getDepartment();
                                if (department == null || department.trim().isEmpty()) {
                                    department = "일반의학과";
                                }

                                // 각 예약의 문진 정보도 가져와서 방문 이력에 포함
                                final String finalDepartment = department; // final 변수로 람다에서 사용

                                medicalInterviewApiService.getInterviewByReservationAsync(
                                        patient.getUid(),
                                        patient.getPatient_id(),
                                        reservation.getReservation_id()
                                ).thenAccept(interviews -> {
                                    Platform.runLater(() -> {
                                        String historyEntry;

                                        if (interviews != null && !interviews.isEmpty()) {
                                            MedicalInterviewDTO interview = interviews.get(0);
                                            // 날짜 - 진료과 (증상)
                                            historyEntry = String.format("%s - %s (%s)",
                                                    reservation.getDate(),
                                                    finalDepartment, // ✅ 실제 department 사용
                                                    interview.getSymptoms() != null ? interview.getSymptoms() : "진료");
                                        } else {
                                            // 문진 정보가 없는 경우
                                            historyEntry = String.format("%s - %s (%s)",
                                                    reservation.getDate(),
                                                    finalDepartment, // ✅ 실제 department 사용
                                                    "예약됨");
                                        }

                                        // 중복 방지를 위해 이미 있는지 확인
                                        if (!historyData.contains(historyEntry)) {
                                            historyData.add(historyEntry);
                                        }
                                    });
                                }).exceptionally(e -> {
                                    Platform.runLater(() -> {
                                        // 문진 조회 실패 시에도 예약 정보는 표시
                                        String historyEntry = String.format("%s - %s (%s)",
                                                reservation.getDate(),
                                                finalDepartment, // ✅ 실제 department 사용
                                                "진료");

                                        if (!historyData.contains(historyEntry)) {
                                            historyData.add(historyEntry);
                                        }
                                    });
                                    return null;
                                });
                            }
                        } else {
                            // 예약이 없는 경우
                            historyData.add("방문 이력이 없습니다.");
                        }
                    });
                }).exceptionally(e -> {
                    Platform.runLater(() -> {
                        historyData.add("방문 이력 조회 실패: " + e.getMessage());
                    });
                    return null;
                });
    }

    private void clearAllFields() {
        nameField.clear(); genderField.clear(); birthField.clear();
        phoneField.clear(); emailField.clear(); addressField.clear(); detailAddressField.clear();
        clearMedicalInfo();
        dateLabel.setText("-"); timeLabel.setText("-"); departmentLabel.setText("-");
        historyData.clear();
    }

    private void setEditMode(boolean editMode) {
        isEditMode = editMode;
        nameField.setEditable(editMode); genderField.setEditable(editMode);
        birthField.setEditable(editMode); phoneField.setEditable(editMode);
        emailField.setEditable(editMode); addressField.setEditable(editMode);
        detailAddressField.setEditable(editMode);
        updatePatientButton.setVisible(editMode);
    }

    @FXML
    private void handleRegisterPatient() {
        showInfo("🚧 신규 환자 등록 기능은 추후 구현 예정입니다.");
    }

    @FXML
    private void handleTodayPatients() {
        loadAllPatients();
        showInfo("금일 환자 목록을 로드했습니다.");
    }

    @FXML
    private void handleChangePatient() {
        if (selectedPatient == null) {
            showError("수정할 환자를 선택해주세요.");
            return;
        }
        setEditMode(true);
        showInfo("환자 정보를 수정할 수 있습니다.");
    }

    @FXML
    private void handleUpdatePatient() {
        if (selectedPatient == null) {
            showError("수정할 환자를 선택해주세요.");
            return;
        }
        selectedPatient.setName(nameField.getText().trim());
        selectedPatient.setGender(genderField.getText().trim());
        selectedPatient.setPhone(phoneField.getText().trim());
        selectedPatient.setEmail(emailField.getText().trim());
        selectedPatient.setAddress(addressField.getText().trim());

        patientApiService.updatePatientAsync(selectedPatient).thenAccept(success -> {
            Platform.runLater(() -> {
                if (success) {
                    showInfo("환자 정보가 수정되었습니다.");
                    setEditMode(false);
                    loadAllPatients();
                } else {
                    showError("환자 정보 수정 실패");
                }
            });
        });
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("오류");
        alert.setHeaderText("오류 발생");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("정보");
        alert.setHeaderText("알림");
        alert.setContentText(msg);
        alert.showAndWait();
    }
}