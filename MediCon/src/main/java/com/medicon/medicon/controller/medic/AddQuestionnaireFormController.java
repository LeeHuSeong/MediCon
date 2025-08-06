package com.medicon.medicon.controller.medic;

import com.medicon.medicon.model.MedicalInterviewDTO;
import com.medicon.medicon.model.PatientDTO;
import com.medicon.medicon.model.ReservationDTO;
import com.medicon.medicon.service.MedicalInterviewApiService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;

public class AddQuestionnaireFormController implements Initializable {

    @FXML private Label addtitleField;
    @FXML private TextField addhistoryLabel;
    @FXML private TextArea addallergyLabel;
    @FXML private TextArea addmedication;
    @FXML private TextArea addElse;
    
    // 증상 체크박스들
    @FXML private CheckBox addcolloc;      // 기침
    @FXML private CheckBox addaddeugua;    // 발열
    @FXML private CheckBox addcaacTwet;    // 가래
    @FXML private CheckBox addheadache;    // 두통
    @FXML private CheckBox addstomachache; // 복통
    @FXML private CheckBox addguitar;      // 기타
    
    // 증상 시작 시점 라디오 버튼들
    @FXML private RadioButton addRadio1;   // 오늘
    @FXML private RadioButton addRadio2;   // 어제
    @FXML private RadioButton addRadio3;   // 3일 전
    @FXML private RadioButton addRadio4;   // 일주일 전
    @FXML private RadioButton addRadio5;   // 기타

    private final MedicalInterviewApiService interviewApiService = new MedicalInterviewApiService();
    private PatientDTO selectedPatient;
    private ReservationDTO selectedReservation;
    private ToggleGroup symptomOnsetGroup;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // 오늘 날짜 설정
        addtitleField.setText(java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        
        // 증상 시작 시점 라디오 버튼 그룹 설정
        symptomOnsetGroup = new ToggleGroup();
        addRadio1.setToggleGroup(symptomOnsetGroup);
        addRadio2.setToggleGroup(symptomOnsetGroup);
        addRadio3.setToggleGroup(symptomOnsetGroup);
        addRadio4.setToggleGroup(symptomOnsetGroup);
        addRadio5.setToggleGroup(symptomOnsetGroup);
        addRadio1.setSelected(true);
    }

    public void setSelectedPatient(PatientDTO patient, ReservationDTO reservation) {
        this.selectedPatient = patient;
        this.selectedReservation = reservation;
    }

    @FXML
    private void handleSave() {
        try {
            // 증상 체크박스에서 선택된 항목들 수집
            StringBuilder symptomsStr = new StringBuilder();
            if (addcolloc.isSelected()) symptomsStr.append("기침, ");
            if (addaddeugua.isSelected()) symptomsStr.append("발열, ");
            if (addcaacTwet.isSelected()) symptomsStr.append("가래, ");
            if (addheadache.isSelected()) symptomsStr.append("두통, ");
            if (addstomachache.isSelected()) symptomsStr.append("복통, ");
            if (addguitar.isSelected()) symptomsStr.append("기타, ");

            // 증상 시작 시점 라디오 버튼에서 선택된 값 가져오기
            String symptomDuration = "";
            if (addRadio1.isSelected()) symptomDuration = "오늘";
            else if (addRadio2.isSelected()) symptomDuration = "어제";
            else if (addRadio3.isSelected()) symptomDuration = "3일 전";
            else if (addRadio4.isSelected()) symptomDuration = "일주일 전";
            else if (addRadio5.isSelected()) symptomDuration = "기타";

            // MedicalInterviewDTO 생성
            MedicalInterviewDTO interview = new MedicalInterviewDTO();
            interview.setInterview_id(generateInterviewId());
            interview.setPatient_id(selectedPatient.getUid()); // uid 사용
            interview.setReservation_id(selectedReservation.getReservation_id());
            interview.setPast_medical_history(addhistoryLabel.getText());
            interview.setSymptoms(symptomsStr.toString());
            interview.setSymptom_duration(symptomDuration);
            interview.setAllergy(addallergyLabel.getText());
            interview.setCurrent_medication(addmedication.getText());

            // 서버에 저장
            interviewApiService.saveInterviewAsync(interview).thenAccept(success -> {
                Platform.runLater(() -> {
                    if (success) {
                        showAlert("성공", "문진이 성공적으로 저장되었습니다.");
                        closeWindow();
                    } else {
                        showAlert("오류", "문진 저장에 실패했습니다.");
                    }
                });
            });

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("오류", "문진 저장 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @FXML
    private void handleClose() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) addtitleField.getScene().getWindow();
        stage.close();
    }

    private String generateInterviewId() {
        return "INT_" + System.currentTimeMillis();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 