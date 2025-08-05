package com.medicon.medicon.controller.medic;

import com.medicon.medicon.model.PatientSignupRequest;
import com.medicon.medicon.service.RegisterApiService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

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
    
    private final RegisterApiService registerApiService = new RegisterApiService();
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
        
        // PatientSignupRequest 객체 생성
        PatientSignupRequest signupRequest = createPatientSignupRequest();
        
        // Firebase Auth + Firestore에 저장
        registerApiService.registerPatient(signupRequest).thenAccept(success -> {
            Platform.runLater(() -> {
                if (success) {
                    showAlert("성공", "환자가 성공적으로 등록되었습니다.\n이메일과 비밀번호로 로그인할 수 있습니다.");
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
        
        // Firebase Authentication 요구사항: 비밀번호 최소 6자
        if (addPasswordField.getText().trim().length() < 6) {
            showAlert("입력 오류", "비밀번호는 최소 6자 이상이어야 합니다.");
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

    private PatientSignupRequest createPatientSignupRequest() {
        // 주소 설정
        String fullAddress = addAddress.getText().trim();
        if (!addAddressDetail.getText().trim().isEmpty()) {
            fullAddress += " " + addAddressDetail.getText().trim();
        }
        
        // 성별 설정
        String gender = addRadio1.isSelected() ? "남자" : "여자";
        
        // PatientSignupRequest 생성
        PatientSignupRequest signupRequest = new PatientSignupRequest(
            addEmailField.getText().trim(),           // 이메일
            addPasswordField.getText().trim(),        // 비밀번호 (이제 포함!)
            nameField.getText().trim(),               // 이름
            addmedication.getText().trim(),           // 휴대전화
            birthField.getText().trim(),              // 주민등록번호 (birthdate로 사용)
            gender,                                   // 성별
            fullAddress,                              // 주소
            birthField.getText().trim().replace("-", "") // RNN (하이픈 제거)
        );
        
        return signupRequest;
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
