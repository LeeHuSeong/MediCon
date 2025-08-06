package com.medicon.medicon.controller.medic;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import com.medicon.medicon.model.MedicalInterviewDTO;
import com.medicon.medicon.model.PatientDTO;
import com.medicon.medicon.model.ReservationDTO;
import com.medicon.medicon.service.MedicalInterviewApiService;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AddQuestionnaireFormController implements Initializable {
    
    // 날짜 표시용 라벨
    @FXML private Label addtitleField;
    
    // 과거 병력
    @FXML private TextField addhistoryLabel;
    
    // 현재 증상 체크박스들
    @FXML private CheckBox addcolloc;      // 기침
    @FXML private CheckBox addaddeugua;    // 발열
    @FXML private CheckBox addcaacTwet;    // 가래
    @FXML private CheckBox addheadache;    // 두통
    @FXML private CheckBox addstomachache; // 복통
    @FXML private CheckBox addguitar;      // 기타
    
    // 증상시작시점 라디오 버튼들
    @FXML private RadioButton addRadio1;   // 오늘
    @FXML private RadioButton addRadio2;   // 어제
    @FXML private RadioButton addRadio3;   // 3일 전
    @FXML private RadioButton addRadio4;   // 일주일 전
    @FXML private RadioButton addRadio5;   // 기타
    
    // 알레르기, 복용 중인 약, 기타
    @FXML private TextArea addallergyLabel;
    @FXML private TextArea addmedication;
    @FXML private TextArea addElse;
    
    private MedicalInterviewApiService interviewApiService;
    private PatientDTO selectedPatient;
    private ReservationDTO selectedReservation;
    private ToggleGroup symptomOnsetGroup;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        interviewApiService = new MedicalInterviewApiService();
        
        // 오늘 날짜 자동 설정
        LocalDate today = LocalDate.now();
        String todayStr = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        addtitleField.setText(todayStr);
        
        // 증상시작시점 라디오 버튼 그룹 설정
        symptomOnsetGroup = new ToggleGroup();
        addRadio1.setToggleGroup(symptomOnsetGroup);
        addRadio2.setToggleGroup(symptomOnsetGroup);
        addRadio3.setToggleGroup(symptomOnsetGroup);
        addRadio4.setToggleGroup(symptomOnsetGroup);
        addRadio5.setToggleGroup(symptomOnsetGroup);
    }
    
    // 선택된 환자와 예약 정보를 설정하는 메서드
    public void setSelectedPatient(PatientDTO patient, ReservationDTO reservation) {
        this.selectedPatient = patient;
        this.selectedReservation = reservation;
    }
    
    @FXML
    private void handleSave() {
        if (selectedPatient == null || selectedReservation == null) {
            showAlert("오류", "환자 정보가 없습니다.");
            return;
        }
        
        // 현재 증상 수집
        List<String> symptoms = new ArrayList<>();
        if (addcolloc.isSelected()) symptoms.add("기침");
        if (addaddeugua.isSelected()) symptoms.add("발열");
        if (addcaacTwet.isSelected()) symptoms.add("가래");
        if (addheadache.isSelected()) symptoms.add("두통");
        if (addstomachache.isSelected()) symptoms.add("복통");
        if (addguitar.isSelected()) symptoms.add("기타");
        
        String symptomsStr = String.join(", ", symptoms);
        
        // 증상시작시점 수집
        String symptomDuration = "";
        if (addRadio1.isSelected()) symptomDuration = "오늘";
        else if (addRadio2.isSelected()) symptomDuration = "어제";
        else if (addRadio3.isSelected()) symptomDuration = "3일 전";
        else if (addRadio4.isSelected()) symptomDuration = "일주일 전";
        else if (addRadio5.isSelected()) symptomDuration = "기타";
        
        // MedicalInterviewDTO 생성
        MedicalInterviewDTO interview = new MedicalInterviewDTO();
        interview.setInterview_id(generateInterviewId());
        interview.setPatient_id(selectedPatient.getPatient_id());
        interview.setReservation_id(selectedReservation.getReservation_id());
        interview.setPast_medical_history(addhistoryLabel.getText());
        interview.setSymptoms(symptomsStr);
        interview.setSymptom_duration(symptomDuration);
        interview.setAllergy(addallergyLabel.getText());
        interview.setCurrent_medication(addmedication.getText());
        
        // 서버로 저장
        interviewApiService.saveInterviewAsync(interview).thenAccept(success -> {
            Platform.runLater(() -> {
                if (success) {
                    showAlert("성공", "문진이 성공적으로 저장되었습니다.");
                    closeWindow();
                } else {
                    showAlert("오류", "문진 저장에 실패했습니다.");
                }
            });
        }).exceptionally(throwable -> {
            Platform.runLater(() -> {
                showAlert("오류", "문진 저장 중 오류가 발생했습니다: " + throwable.getMessage());
            });
            return null;
        });
    }
    
    @FXML
    private void handleClose() {
        closeWindow();
    }
    
    private String generateInterviewId() {
        return "INTERVIEW_" + System.currentTimeMillis();
    }
    
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    private void closeWindow() {
        Stage stage = (Stage) addtitleField.getScene().getWindow();
        stage.close();
    }
}