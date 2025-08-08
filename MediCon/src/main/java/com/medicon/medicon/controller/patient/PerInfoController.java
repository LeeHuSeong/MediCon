package com.medicon.medicon.controller.patient;

import com.medicon.medicon.model.PatientDTO;
import com.medicon.medicon.model.ReservationDTO;
import com.medicon.medicon.service.PatientApiService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.stream.Collectors;

public class PerInfoController implements Initializable {

    // FXML로 연결된 환자 정보 필드들
    @FXML private TextField nameField;
    @FXML private RadioButton btn_male;
    @FXML private RadioButton btn_female;
    @FXML private TextField birthField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private TextField addressField;
    @FXML private TextField detailAddressField;

    // 예약 관련 FXML 요소들
    @FXML private TextArea reservationList;
    @FXML private DatePicker datePicker;
    @FXML private Label selectedTimeDisplayLabel;

    // 버튼들
    @FXML private Button changePatientButton;
    @FXML private Button updatePatientButton;
    @FXML private Button reserveButton;

    // ToggleGroup for 성별 라디오 버튼
    private ToggleGroup genderToggleGroup;

    // 현재 로그인된 환자 정보
    private PatientDTO currentPatient;

    // 선택된 예약 시간
    private String selectedTime;
    private Button lastSelectedTimeButton;

    // 편집 모드 상태
    private boolean isEditMode = false;

    // 로그인된 사용자 UID (PatientMainController에서 전달받음)
    private String currentUid;

    // API 서비스
    private PatientApiService patientApiService;

    /**
     * PatientMainController에서 UID 설정 (로그인 정보 전달)
     */
    public void setUid(String uid) {
        this.currentUid = uid;
        System.out.println("PerInfoController - UID 설정: " + uid);

        // UID가 설정되면 환자 정보 다시 로드
        if (uid != null) {
            loadCurrentPatientInfo();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("PerInfoController 초기화 시작");

        // API 서비스 초기화
        this.patientApiService = new PatientApiService();

        // 성별 라디오 버튼 그룹 설정
        setupGenderToggleGroup();

        // 한글 입력 문제 해결을 위한 TextField 설정
        configureTextFields();

        

        // 예약 목록 로드
        loadPatientReservations();

        // 이벤트 핸들러 설정
        setupEventHandlers();

        // 초기 상태: 환자 정보 필드들을 읽기 전용으로 설정
        setEditMode(false);

        // 초기화 시 시간 버튼 스타일 및 선택된 시간 레이블 초기화
        resetTimeButtonStyles();

        System.out.println("PerInfoController 초기화 완료");
    }

    /**
     * 성별 라디오 버튼 그룹 설정
     */
    private void setupGenderToggleGroup() {
        genderToggleGroup = new ToggleGroup();
        btn_male.setToggleGroup(genderToggleGroup);
        btn_female.setToggleGroup(genderToggleGroup);
    }

    /**
     * 한글 입력 문제 해결을 위한 TextField 설정
     */
    private void configureTextFields() {
        TextField[] fields = {
                nameField, phoneField, emailField, addressField, birthField, detailAddressField
        };

        for (TextField field : fields) {
            // IME(한글 입력기) 처리 개선
            field.textProperty().addListener((obs, oldVal, newVal) -> {
                // 빈 리스너 - 실시간 처리 방지하여 한글 조합 문제 해결
            });
        }

        System.out.println("TextField 한글 처리 설정 완료");
    }

    /**
     * 이벤트 핸들러 설정
     */
    private void setupEventHandlers() {
        // 환자 정보 변경 버튼
        changePatientButton.setOnAction(this::handleChangePatient);

        // 완료 버튼
        updatePatientButton.setOnAction(this::handleUpdatePatient);

        // 예약하기 버튼
        reserveButton.setOnAction(this::handleReservation);

        // 날짜 선택 이벤트
        datePicker.setOnAction(e -> updateAvailableTimes());

        // 필드 검증 이벤트
        nameField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) validateNameField();
        });

        phoneField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) validatePhoneField();
        });

        emailField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) validateEmailField();
        });
    }

    /**
     * 로그인된 환자 정보를 로드하여 화면에 표시
     */
    private void loadCurrentPatientInfo() {
        if (currentUid == null) {
            System.out.println("UID가 설정되지 않았습니다.");
            return;
        }

        // 비동기로 환자 정보 조회
        getCurrentLoggedInPatient();
    }

    /**
     * 환자 정보를 UI에 표시 (기존 PatientUIManager 로직 활용)
     */
    private void displayPatientInfo(PatientDTO patient) {
        if (patient == null) {
            clearAllFields();
            return;
        }

        Platform.runLater(() -> {
            nameField.setText(patient.getName() != null ? patient.getName() : "");
            birthField.setText(patient.getRnn() != null && patient.getRnn().length() >= 6 ?
                    patient.getRnn().substring(0, 6) : "");
            phoneField.setText(patient.getPhone() != null ? patient.getPhone() : "");
            emailField.setText(patient.getEmail() != null ? patient.getEmail() : "");
            addressField.setText(patient.getAddress() != null ? patient.getAddress() : "");
            detailAddressField.setText(""); // 상세주소는 별도 관리

            // 성별 라디오 버튼 선택
            if ("남자".equals(patient.getGender())) {
                btn_male.setSelected(true);
            } else if ("여자".equals(patient.getGender())) {
                btn_female.setSelected(true);
            } else {
                genderToggleGroup.selectToggle(null);
            }
        });
    }

    /**
     * 환자의 예약 목록을 로드
     */
    private void loadPatientReservations() {
        if (currentPatient != null) {
            patientApiService.getPatientReservationsAsync(currentPatient.getUid())
                    .thenAccept(reservations -> {
                        String reservationText = reservations.stream()
                                .map(r -> String.format("%s %s - %s", r.getDate(), r.getTime(), r.getDepartment()))
                                .collect(Collectors.joining("\n"));
                        Platform.runLater(() -> {
                            reservationList.setText(reservationText);
                        });
                    });
        }
    }

    /**
     * 환자 정보 변경 버튼 핸들러
     */
    @FXML
    private void handleChangePatient(ActionEvent event) {
        setEditMode(true);
        System.out.println("환자 정보 수정 모드 진입");
    }

    /**
     * 환자 정보 업데이트 완료 버튼 핸들러
     */
    @FXML
    private void handleUpdatePatient(ActionEvent event) {
        if (validateAllFields()) {
            // 환자 정보 업데이트 (비동기 처리)
            updateCurrentPatientInfo();

            // UI 상태는 API 응답 후에 변경됨 (savePatientToDatabase에서 처리)
            setEditMode(false);

            System.out.println("환자 정보 업데이트 요청 완료");
        }
    }

    /**
     * 예약하기 버튼 핸들러
     */
    @FXML
    private void handleReservation(ActionEvent event) {
        LocalDate selectedDate = datePicker.getValue();

        if (selectedDate == null) {
            showAlert("경고", "날짜를 선택해주세요.");
            return;
        }

        if (selectedTime == null) {
            showAlert("경고", "시간을 선택해주세요.");
            return;
        }

        if (currentPatient == null) {
            showAlert("오류", "환자 정보가 없습니다.");
            return;
        }

        ReservationDTO newReservation = new ReservationDTO();
        newReservation.setReservation_id(UUID.randomUUID().toString());
        newReservation.setPatient_uid(currentPatient.getUid());
        newReservation.setPatient_id(currentPatient.getPatient_id());
        newReservation.setDate(selectedDate.toString());
        newReservation.setTime(selectedTime);
        newReservation.setDepartment("내과"); // TODO: 진료과 선택 기능 추가 필요

        patientApiService.createReservationAsync(newReservation).thenAccept(success -> {
            if (success) {
                Platform.runLater(() -> {
                    showAlert("성공", "예약이 성공적으로 완료되었습니다.");
                    loadPatientReservations(); // 예약 목록 새로고침
                    datePicker.setValue(null);
                    selectedTime = null;
                    resetTimeButtonStyles();
                });
            } else {
                Platform.runLater(() -> showAlert("실패", "예약 중 오류가 발생했습니다."));
            }
        });
    }

    /**
     * 시간 버튼 클릭 핸들러 (FXML에서 onAction으로 연결 필요)
     */
    @FXML
    private void selectTime(ActionEvent event) {
        Button timeButton = (Button) event.getSource();
        selectedTime = timeButton.getText();

        // 이전에 선택된 버튼의 스타일 초기화
        if (lastSelectedTimeButton != null) {
            lastSelectedTimeButton.getStyleClass().remove("time-button-selected");
        }

        // 현재 선택된 버튼 스타일 적용
        timeButton.getStyleClass().add("time-button-selected");
        lastSelectedTimeButton = timeButton;

        selectedTimeDisplayLabel.setText(selectedTime);
    }

    // === UI 상태 관리 메서드들 ===

    /**
     * 수정 모드 설정 (기존 PatientUIManager 로직 활용)
     */
    private void setEditMode(boolean editMode) {
        this.isEditMode = editMode;

        Platform.runLater(() -> {
            // 입력 필드 활성화/비활성화
            nameField.setEditable(editMode);
            btn_male.setDisable(!editMode);
            btn_female.setDisable(!editMode);
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
            birthField.setStyle(backgroundColor);
            phoneField.setStyle(backgroundColor);
            emailField.setStyle(backgroundColor);
            addressField.setStyle(backgroundColor);
            detailAddressField.setStyle(backgroundColor);
        });
    }

    /**
     * 모든 입력 필드 초기화
     */
    private void clearAllFields() {
        Platform.runLater(() -> {
            nameField.clear();
            btn_male.setSelected(false);
            btn_female.setSelected(false);
            birthField.clear();
            phoneField.clear();
            emailField.clear();
            addressField.clear();
            detailAddressField.clear();
            genderToggleGroup.selectToggle(null);
        });
    }

    /**
     * 업데이트 버튼 로딩 상태 설정
     */
    private void setUpdateButtonLoading(boolean loading) {
        Platform.runLater(() -> {
            updatePatientButton.setDisable(loading);
            updatePatientButton.setText(loading ? "수정 중..." : "완료");
        });
    }

    // === 필드 검증 메서드들 ===

    /**
     * 필드 검증 - 이름
     */
    private void validateNameField() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            nameField.setStyle("-fx-background-color: #ffebee;");
        } else {
            nameField.setStyle(isEditMode ? "-fx-background-color: #fff3cd;" : "-fx-background-color: white;");
        }
    }

    /**
     * 필드 검증 - 전화번호
     */
    private void validatePhoneField() {
        String phone = phoneField.getText().trim();
        if (!phone.isEmpty() && !phone.matches("^[0-9-]+$")) {
            phoneField.setStyle("-fx-background-color: #ffebee;");
        } else {
            phoneField.setStyle(isEditMode ? "-fx-background-color: #fff3cd;" : "-fx-background-color: white;");
        }
    }

    /**
     * 필드 검증 - 이메일
     */
    private void validateEmailField() {
        String email = emailField.getText().trim();
        if (!email.isEmpty() && !email.contains("@")) {
            emailField.setStyle("-fx-background-color: #ffebee;");
        } else {
            emailField.setStyle(isEditMode ? "-fx-background-color: #fff3cd;" : "-fx-background-color: white;");
        }
    }

    /**
     * 모든 필드 검증
     */
    private boolean validateAllFields() {
        validateNameField();
        validatePhoneField();
        validateEmailField();

        if (nameField.getText().trim().isEmpty()) {
            showAlert("경고", "이름을 입력해주세요.");
            nameField.requestFocus();
            return false;
        }

        return true;
    }

    // === 데이터 처리 메서드들 ===

    /**
     * 현재 환자 정보 업데이트
     */
    private void updateCurrentPatientInfo() {
        if (currentPatient != null) {
            currentPatient.setName(nameField.getText().trim());
            currentPatient.setPhone(phoneField.getText().trim());
            currentPatient.setEmail(emailField.getText().trim());
            currentPatient.setAddress(addressField.getText().trim());

            // 성별 설정
            Toggle selectedGender = genderToggleGroup.getSelectedToggle();
            if (selectedGender != null) {
                currentPatient.setGender(((RadioButton) selectedGender).getText());
            }

            // 데이터베이스 업데이트 (실제 구현 필요)
            savePatientToDatabase(currentPatient);
        }
    }

    // === 유틸리티 메서드들 ===

    private void updateAvailableTimes() {
        // 선택된 날짜에 따라 이용 가능한 시간 업데이트
        // 실제로는 데이터베이스에서 해당 날짜의 예약된 시간을 확인
        System.out.println("선택된 날짜: " + datePicker.getValue());
    }

    private void resetTimeButtonStyles() {
        if (lastSelectedTimeButton != null) {
            lastSelectedTimeButton.getStyleClass().remove("time-button-selected");
            lastSelectedTimeButton = null;
        }
        selectedTimeDisplayLabel.setText("");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // === 데이터베이스/서비스 연동 메서드들 (실제 구현 필요) ===

    /**
     * 현재 로그인된 환자 정보 가져오기
     */
    private void getCurrentLoggedInPatient() {
        if (currentUid == null) {
            System.out.println("UID가 없습니다.");
            return;
        }

        try {
            // 기존 PatientApiService의 비동기 메서드 사용
            patientApiService.getPatientByUidAsync(currentUid).thenAccept(patient -> {
                Platform.runLater(() -> {
                    if (patient != null) {
                        this.currentPatient = patient;
                        displayPatientInfo(patient);
                        loadPatientReservations(); // 예약 목록 로드
                        System.out.println("환자 정보 조회 성공: " + patient.getName());
                    } else {
                        System.out.println("환자 정보를 찾을 수 없습니다.");
                    }
                });
            }).exceptionally(throwable -> {
                Platform.runLater(() -> {
                    System.err.println("환자 정보 조회 중 오류 발생: " + throwable.getMessage());
                    throwable.printStackTrace();
                });
                return null;
            });

        } catch (Exception e) {
            System.err.println("환자 정보 조회 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 환자 정보 데이터베이스 저장
     */
    private void savePatientToDatabase(PatientDTO patient) {
        try {
            // 기존 PatientApiService의 비동기 업데이트 메서드 사용
            setUpdateButtonLoading(true);

            patientApiService.updatePatientAsync(patient).thenAccept(success -> {
                Platform.runLater(() -> {
                    setUpdateButtonLoading(false);

                    if (success) {
                        System.out.println("환자 정보 저장 성공: " + patient.getName());
                        showAlert("성공", "환자 정보가 성공적으로 업데이트되었습니다.");
                    } else {
                        showAlert("오류", "환자 정보 저장에 실패했습니다.");
                    }
                });
            }).exceptionally(throwable -> {
                Platform.runLater(() -> {
                    setUpdateButtonLoading(false);
                    System.err.println("환자 정보 저장 중 오류 발생: " + throwable.getMessage());
                    showAlert("오류", "환자 정보 저장 중 오류가 발생했습니다.");
                });
                return null;
            });

        } catch (Exception e) {
            setUpdateButtonLoading(false);
            System.err.println("환자 정보 저장 중 오류 발생: " + e.getMessage());
            showAlert("오류", "환자 정보 저장 중 오류가 발생했습니다.");
        }
    }
}