package com.medicon.medicon.controller.medic.patient;

import com.medicon.medicon.model.PatientDTO;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.collections.ObservableList;

/**
 * 환자 관리 UI 관련 기능을 담당하는 클래스
 */
public class PatientUIManager {

    private final TextField nameField;
    private final TextField genderField;
    private final TextField birthField;
    private final TextField phoneField;
    private final TextField emailField;
    private final TextField addressField;
    private final TextField detailAddressField;
    private final TextField searchField;
    private final ListView<PatientDTO> patientListView;
    private final ListView<String> historyListView;
    private final ObservableList<PatientDTO> patientData;
    private final ObservableList<String> historyData;

    // 의료 정보 라벨들
    private final Label symptomLabel;
    private final Label historyLabel;
    private final Label allergyLabel;
    private final Label medicationLabel;
    private final Label dateLabel;
    private final Label timeLabel;
    private final Label departmentLabel;

    // 버튼들
    private final Button updatePatientButton;
    private final Button changePatientButton;
    private final Button searchButton;
    private final Button registerPatientButton;
    private final Button todayPatientButton;

    private boolean isEditMode = false;

    public PatientUIManager(TextField nameField, TextField genderField, TextField birthField,
                            TextField phoneField, TextField emailField, TextField addressField,
                            TextField detailAddressField, TextField searchField,
                            ListView<PatientDTO> patientListView, ListView<String> historyListView,
                            ObservableList<PatientDTO> patientData, ObservableList<String> historyData,
                            Label symptomLabel, Label historyLabel, Label allergyLabel,
                            Label medicationLabel, Label dateLabel, Label timeLabel, Label departmentLabel,
                            Button updatePatientButton, Button changePatientButton, Button searchButton,
                            Button registerPatientButton, Button todayPatientButton) {

        this.nameField = nameField;
        this.genderField = genderField;
        this.birthField = birthField;
        this.phoneField = phoneField;
        this.emailField = emailField;
        this.addressField = addressField;
        this.detailAddressField = detailAddressField;
        this.searchField = searchField;
        this.patientListView = patientListView;
        this.historyListView = historyListView;
        this.patientData = patientData;
        this.historyData = historyData;
        this.symptomLabel = symptomLabel;
        this.historyLabel = historyLabel;
        this.allergyLabel = allergyLabel;
        this.medicationLabel = medicationLabel;
        this.dateLabel = dateLabel;
        this.timeLabel = timeLabel;
        this.departmentLabel = departmentLabel;
        this.updatePatientButton = updatePatientButton;
        this.changePatientButton = changePatientButton;
        this.searchButton = searchButton;
        this.registerPatientButton = registerPatientButton;
        this.todayPatientButton = todayPatientButton;
    }

    /**
     * UI 컴포넌트 초기 설정
     */
    public void setupComponents() {
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
    }

    /**
     * 한글 입력 문제 해결을 위한 TextField 설정
     */
    public void configureTextFields() {
        TextField[] fields = {
                nameField, genderField, phoneField, emailField, addressField, searchField, birthField, detailAddressField
        };

        for (TextField field : fields) {
            // IME(한글 입력기) 처리 개선 - 실시간 이벤트 제거
            field.textProperty().addListener((obs, oldVal, newVal) -> {
                // 빈 리스너 - 실시간 처리 방지하여 한글 조합 문제 해결
            });
        }

        System.out.println("✅ TextField 한글 처리 설정 완료");
    }

    /**
     * 환자 정보를 UI에 표시
     */
    public void displayPatientInfo(PatientDTO patient) {
        if (patient == null) {
            clearAllFields();
            return;
        }

        Platform.runLater(() -> {
            nameField.setText(patient.getName() != null ? patient.getName() : "");
            genderField.setText(patient.getGender() != null ? patient.getGender() : "");
            birthField.setText(patient.getRnn() != null && patient.getRnn().length() >= 6 ?
                    patient.getRnn().substring(0, 6) : "");
            phoneField.setText(patient.getPhone() != null ? patient.getPhone() : "");
            emailField.setText(patient.getEmail() != null ? patient.getEmail() : "");
            addressField.setText(patient.getAddress() != null ? patient.getAddress() : "");
            detailAddressField.setText("");
        });
    }

    /**
     * 의료 정보 표시
     */
    public void displayMedicalInfo(String symptom, String history, String allergy, String medication) {
        Platform.runLater(() -> {
            symptomLabel.setText(symptom != null ? symptom : "-");
            historyLabel.setText(history != null ? history : "-");
            allergyLabel.setText(allergy != null ? allergy : "-");
            medicationLabel.setText(medication != null ? medication : "-");
        });
    }

    /**
     * 예약 정보 표시
     */
    public void displayReservationInfo(String date, String time, String department) {
        Platform.runLater(() -> {
            dateLabel.setText(date != null ? date : "-");
            timeLabel.setText(time != null ? time : "-");
            departmentLabel.setText(department != null ? department : "-");
        });
    }

    /**
     * 의료 정보 초기화
     */
    public void clearMedicalInfo() {
        Platform.runLater(() -> {
            symptomLabel.setText("-");
            historyLabel.setText("-");
            allergyLabel.setText("-");
            medicationLabel.setText("-");
        });
    }

    /**
     * 모든 입력 필드 초기화
     */
    public void clearAllFields() {
        Platform.runLater(() -> {
            nameField.clear();
            genderField.clear();
            birthField.clear();
            phoneField.clear();
            emailField.clear();
            addressField.clear();
            detailAddressField.clear();
            clearMedicalInfo();
            dateLabel.setText("-");
            timeLabel.setText("-");
            departmentLabel.setText("-");
            historyData.clear();
        });
    }

    /**
     * 수정 모드 설정
     */
    public void setEditMode(boolean editMode) {
        System.out.println("🔧 수정 모드 변경: " + isEditMode + " → " + editMode);
        this.isEditMode = editMode;

        Platform.runLater(() -> {
            // 입력 필드 활성화/비활성화
            nameField.setEditable(editMode);
            genderField.setEditable(editMode);
            birthField.setEditable(editMode);
            phoneField.setEditable(editMode);
            emailField.setEditable(editMode);
            addressField.setEditable(editMode);
            detailAddressField.setEditable(editMode);

            // 버튼 표시/숨김
            updatePatientButton.setVisible(editMode);
            changePatientButton.setDisable(editMode);

            // 배경색 변경
            String backgroundColor = editMode ? "-fx-background-color: #fff3cd;" : "-fx-background-color: white;";
            nameField.setStyle(backgroundColor);
            genderField.setStyle(backgroundColor);
            birthField.setStyle(backgroundColor);
            phoneField.setStyle(backgroundColor);
            emailField.setStyle(backgroundColor);
            addressField.setStyle(backgroundColor);
            detailAddressField.setStyle(backgroundColor);

            // 다른 기능들 잠금/해제
            patientListView.setDisable(editMode);
            searchField.setDisable(editMode);
            searchButton.setDisable(editMode);
            registerPatientButton.setDisable(editMode);
            todayPatientButton.setDisable(editMode);
        });

        if (editMode) {
            System.out.println("수정 모드 진입");
        } else {
            System.out.println("수정 모드 종료");
        }
    }

    /**
     * 필드 검증 - 이름
     */
    public void validateNameField() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            nameField.setStyle("-fx-background-color: #ffebee;"); // 연한 빨간색
        } else {
            nameField.setStyle(isEditMode ? "-fx-background-color: #fff3cd;" : "-fx-background-color: white;");
        }
    }

    /**
     * 필드 검증 - 전화번호
     */
    public void validatePhoneField() {
        String phone = phoneField.getText().trim();
        if (!phone.isEmpty() && !phone.matches("^[0-9-]+$")) {
            phoneField.setStyle("-fx-background-color: #ffebee;"); // 연한 빨간색
        } else {
            phoneField.setStyle(isEditMode ? "-fx-background-color: #fff3cd;" : "-fx-background-color: white;");
        }
    }

    /**
     * 필드 검증 - 이메일
     */
    public void validateEmailField() {
        String email = emailField.getText().trim();
        if (!email.isEmpty() && !email.contains("@")) {
            emailField.setStyle("-fx-background-color: #ffebee;"); // 연한 빨간색
        } else {
            emailField.setStyle(isEditMode ? "-fx-background-color: #fff3cd;" : "-fx-background-color: white;");
        }
    }

    /**
     * 환자 목록 업데이트 시 선택 유지
     */
    public void selectPatientById(String patientId) {
        if (patientId == null) return;

        Platform.runLater(() -> {
            for (PatientDTO patient : patientData) {
                if (patient.getPatient_id().equals(patientId)) {
                    patientListView.getSelectionModel().select(patient);
                    break;
                }
            }
        });
    }

    /**
     * 업데이트 버튼 로딩 상태 설정
     */
    public void setUpdateButtonLoading(boolean loading) {
        Platform.runLater(() -> {
            updatePatientButton.setDisable(loading);
            updatePatientButton.setText(loading ? "수정 중..." : "수정 완료");
        });
    }

    // Getter 메서드들
    public TextField getNameField() { return nameField; }
    public TextField getGenderField() { return genderField; }
    public TextField getBirthField() { return birthField; }
    public TextField getPhoneField() { return phoneField; }
    public TextField getEmailField() { return emailField; }
    public TextField getAddressField() { return addressField; }
    public TextField getSearchField() { return searchField; }
    public boolean isEditMode() { return isEditMode; }
}