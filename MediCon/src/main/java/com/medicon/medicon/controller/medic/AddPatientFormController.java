package com.medicon.medicon.controller.medic;

import com.medicon.medicon.model.PatientDTO;
import com.medicon.medicon.service.PatientApiService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.UUID;

public class AddPatientFormController implements Initializable {
    
    @FXML private TextField addEmailField;
    @FXML private PasswordField addPasswordField;
    @FXML private TextField nameField;
    @FXML private RadioButton addRadio1; // 남자
    @FXML private RadioButton addRadio2; // 여자
    @FXML private TextField birthField;
    @FXML private TextField addmedication; // 휴대전화 (필드명이 잘못되어 있음)
    @FXML private TextField addAddress;
    @FXML private TextField addAddressDetail;
    
    private final PatientApiService patientApiService = new PatientApiService();
    private ToggleGroup genderToggleGroup;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 성별 라디오 버튼 그룹 설정
        genderToggleGroup = new ToggleGroup();
        addRadio1.setToggleGroup(genderToggleGroup);
        addRadio2.setToggleGroup(genderToggleGroup);
        addRadio1.setSelected(true); // 기본값으로 남자 선택
        
        // 비밀번호 필드를 PasswordField로 설정
        addPasswordField.setPromptText("비밀번호를 입력하세요");
    }

    @FXML
    private void handleSave() {
        // 입력값 검증
        if (!validateInputs()) {
            return;
        }
        
        // PatientDTO 객체 생성
        PatientDTO patient = createPatientDTO();
        
        // Firebase에 저장
        patientApiService.savePatientAsync(patient).thenAccept(success -> {
            Platform.runLater(() -> {
                if (success) {
                    showAlert("성공", "환자가 성공적으로 등록되었습니다.");
                    clearForm();
                    closeWindow(); // 창 닫기
                } else {
                    showAlert("오류", "환자 등록에 실패했습니다. 다시 시도해주세요.");
                }
            });
        }).exceptionally(throwable -> {
            Platform.runLater(() -> {
                showAlert("오류", "환자 등록 중 오류가 발생했습니다: " + throwable.getMessage());
            });
            return null;
        });
    }

    @FXML
    private void handleClose() {
        closeWindow();
    }

    private boolean validateInputs() {
        // 필수 필드 검증
        if (addEmailField.getText().trim().isEmpty()) {
            showAlert("입력 오류", "이메일을 입력해주세요.");
            addEmailField.requestFocus();
            return false;
        }
        
        if (addPasswordField.getText().trim().isEmpty()) {
            showAlert("입력 오류", "비밀번호를 입력해주세요.");
            addPasswordField.requestFocus();
            return false;
        }
        
        if (nameField.getText().trim().isEmpty()) {
            showAlert("입력 오류", "이름을 입력해주세요.");
            nameField.requestFocus();
            return false;
        }
        
        if (birthField.getText().trim().isEmpty()) {
            showAlert("입력 오류", "주민등록번호를 입력해주세요.");
            birthField.requestFocus();
            return false;
        }
        
        if (addmedication.getText().trim().isEmpty()) {
            showAlert("입력 오류", "휴대전화를 입력해주세요.");
            addmedication.requestFocus();
            return false;
        }
        
        // 이메일 형식 검증
        if (!isValidEmail(addEmailField.getText().trim())) {
            showAlert("입력 오류", "올바른 이메일 형식을 입력해주세요.");
            addEmailField.requestFocus();
            return false;
        }
        
        // 주민등록번호 형식 검증 (하이픈 제거 후 13자리)
        String rnn = birthField.getText().trim().replace("-", "");
        if (rnn.length() != 13) {
            showAlert("입력 오류", "주민등록번호는 13자리로 입력해주세요. (하이픈 포함 가능)");
            birthField.requestFocus();
            return false;
        }
        
        return true;
    }

    private PatientDTO createPatientDTO() {
        PatientDTO patient = new PatientDTO();
        
        // 고유 ID 생성
        String uid = UUID.randomUUID().toString();
        String patientId = generatePatientId();
        
        // 기본 정보 설정
        patient.setUid(uid);
        patient.setPatient_id(patientId);
        patient.setEmail(addEmailField.getText().trim());
        patient.setName(nameField.getText().trim());
        patient.setPhone(addmedication.getText().trim()); // 휴대전화
        patient.setRnn(birthField.getText().trim().replace("-", "")); // 주민등록번호 (하이픈 제거)
        
        // 성별 설정
        if (addRadio1.isSelected()) {
            patient.setGender("남자");
        } else {
            patient.setGender("여자");
        }
        
        // 주소 설정
        String fullAddress = addAddress.getText().trim();
        if (!addAddressDetail.getText().trim().isEmpty()) {
            fullAddress += " " + addAddressDetail.getText().trim();
        }
        patient.setAddress(fullAddress);
        
        // 역할 및 권한 설정
        patient.setRole("patient");
        patient.setAuthority(1); // 환자 권한
        
        // 생성 시간 설정
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        patient.setCreateAt(currentTime);
        
        return patient;
    }

    private String generatePatientId() {
        // 환자 ID 생성 (P + 현재 시간 기반)
        String timestamp = String.valueOf(System.currentTimeMillis());
        return "P" + timestamp.substring(timestamp.length() - 8);
    }

    private boolean isValidEmail(String email) {
        // 간단한 이메일 형식 검증
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    private void clearForm() {
        addEmailField.clear();
        addPasswordField.clear();
        nameField.clear();
        birthField.clear();
        addmedication.clear();
        addAddress.clear();
        addAddressDetail.clear();
        addRadio1.setSelected(true);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) addEmailField.getScene().getWindow();
        stage.close();
    }
}
