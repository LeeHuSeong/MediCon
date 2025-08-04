package com.medicon.medicon.controller.medic;

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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class TreatmentManagementController implements Initializable {

    @FXML private Button registerPatientButton;
    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private ListView<PatientDTO> patientListView;
    
    // 환자 정보 라벨들
    @FXML private Label patientNameLabel;
    @FXML private Label patientGenderLabel;
    @FXML private Label patientAgeLabel;
    @FXML private Label patientPhoneLabel;
    
    // 문진 정보 라벨들
    @FXML private Label symptomLabel;
    @FXML private Label historyLabel;
    @FXML private Label allergyLabel;
    @FXML private Label medicationLabel;
    @FXML private TextArea symytomElse;

    private final PatientApiService patientApiService = new PatientApiService();
    private final ReservationApiService reservationApiService = new ReservationApiService();
    private final MedicalInterviewApiService interviewApiService = new MedicalInterviewApiService();
    private final ObservableList<PatientDTO> patientData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // ListView와 ObservableList 연결
        patientListView.setItems(patientData);
        
        // 환자 선택 이벤트 설정
        patientListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                displayPatientInfo(newVal);
                displayMedicalInterview(newVal);
            }
        });
    }

    @FXML
    private void handleRegisterPatient() {
        loadTodayPatients();
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadTodayPatients();
            return;
        }
        patientApiService.getPatientsByNameAsync(keyword).thenAccept(patients -> {
            Platform.runLater(() -> {
                patientData.clear();
                patientData.addAll(patients);
            });
        });
    }

    private void loadTodayPatients() {
        patientApiService.getAllPatientsAsync().thenAccept(allPatients -> {
            if (allPatients == null || allPatients.isEmpty()) {
                Platform.runLater(() -> {
                    patientData.clear();
                });
                return;
            }

            LocalDate today = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            List<PatientDTO> todayPatients = new java.util.ArrayList<>();
            AtomicInteger pendingRequests = new AtomicInteger(allPatients.size());

            for (PatientDTO patient : allPatients) {
                reservationApiService.getReservationsByPatientId(patient.getPatient_id())
                        .thenAccept(reservations -> {
                            boolean hasTodayReservation = false;

                            if (reservations != null && !reservations.isEmpty()) {
                                for (ReservationDTO reservation : reservations) {
                                    try {
                                        LocalDate reservationDate = LocalDate.parse(reservation.getDate(), formatter);
                                        if (reservationDate.isEqual(today)) {
                                            hasTodayReservation = true;
                                            break;
                                        }
                                    } catch (Exception e) {
                                        // 날짜 파싱 실패
                                    }
                                }
                            }

                            if (hasTodayReservation) {
                                synchronized (todayPatients) {
                                    todayPatients.add(patient);
                                }
                            }

                            if (pendingRequests.decrementAndGet() == 0) {
                                Platform.runLater(() -> {
                                    patientData.clear();
                                    if (!todayPatients.isEmpty()) {
                                        todayPatients.sort((p1, p2) -> p1.getName().compareTo(p2.getName()));
                                        patientData.addAll(todayPatients);
                                        System.out.println("오늘 예약 환자 조회 성공: " + patientData.size() + "명");
                                    }
                                });
                            }
                        })
                        .exceptionally(e -> {
                            if (pendingRequests.decrementAndGet() == 0) {
                                Platform.runLater(() -> {
                                    patientData.clear();
                                });
                            }
                            return null;
                        });
            }
        });
    }

    private void displayPatientInfo(PatientDTO patient) {
        Platform.runLater(() -> {
            patientNameLabel.setText(patient.getName());
            patientGenderLabel.setText(patient.getGender());
            patientPhoneLabel.setText(patient.getPhone());
            
            // 나이 계산 (생년월일에서)
            try {
                String birthYear = patient.getRnn().substring(0, 2);
                int birthYearInt = Integer.parseInt(birthYear);
                int currentYear = LocalDate.now().getYear() % 100;
                int age = currentYear - birthYearInt;
                if (age < 0) age += 100; // 1900년대 출생자 처리
                patientAgeLabel.setText(String.valueOf(age));
            } catch (Exception e) {
                patientAgeLabel.setText("-");
            }
        });
    }

    private void displayMedicalInterview(PatientDTO patient) {
        reservationApiService.getReservationsByPatientId(patient.getPatient_id()).thenAccept(reservations -> {
            Platform.runLater(() -> {
                if (reservations != null && !reservations.isEmpty()) {
                    ReservationDTO latestReservation = reservations.get(0);
                    interviewApiService.getInterviewByReservationAsync(
                            patient.getUid(), patient.getPatient_id(), latestReservation.getReservation_id()
                    ).thenAccept(interviews -> {
                        Platform.runLater(() -> {
                            if (interviews != null && !interviews.isEmpty()) {
                                MedicalInterviewDTO interview = interviews.get(0);
                                symptomLabel.setText(interview.getSymptoms() != null ? interview.getSymptoms() : "-");
                                historyLabel.setText(interview.getPast_medical_history() != null ? interview.getPast_medical_history() : "-");
                                allergyLabel.setText(interview.getAllergy() != null ? interview.getAllergy() : "-");
                                medicationLabel.setText(interview.getCurrent_medication() != null ? interview.getCurrent_medication() : "-");
                                symytomElse.setText(interview.getSymptoms() != null ? interview.getSymptoms() : "");
                            } else {
                                clearMedicalInfo();
                            }
                        });
                    });
                } else {
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
        symytomElse.setText("");
    }
}
