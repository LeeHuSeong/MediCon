package com.medicon.medicon.controller.medic.form;

import com.medicon.medicon.model.ChartDTO;
import com.medicon.medicon.model.PatientDTO;
import com.medicon.medicon.model.StaffUser;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.awt.Desktop;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ResourceBundle;

public class ReferralFormController implements Initializable {
    @FXML private TextField chartNumberField;       // 차트번호
    @FXML private TextField referralNumberField;    // 의뢰번호
    @FXML private DatePicker referralDatePicker;    // 의뢰일자

    // ▶ 환자 정보
    @FXML private TextField nameField;              // 환자 성명
    @FXML private TextField rrnField;               // 주민등록번호
    @FXML private ToggleGroup genderGroup;
    @FXML private RadioButton maleRadio;
    @FXML private RadioButton femaleRadio;
    @FXML private TextField phoneNumField;          // 연락처
    @FXML private TextField addressField;           // 주소

    // ▶ 발신 병원 정보
    @FXML private TextField hospitalNameField;      // 병원명
    @FXML private TextField hospitalPhoneNumField;  // 병원 연락처
    @FXML private TextField departmentField;        // 소속 과/부서
    @FXML private TextField referrerPositionField;  // 의뢰자 직위

    // ▶ 수신자 정보
    @FXML private TextField receiverInstitutionField; // 수신 기관명
    @FXML private TextField receiverDeptField;       // 수신 부서/의사
    @FXML private TextField receiverPhoneField;      // 수신 연락처

    // ▶ 검사/약물/의뢰 정보
    @FXML private TextArea testResultsField;        // 검사결과 요약
    @FXML private TextArea medicationsField;        // 복용 중인 약물
    @FXML private TextArea purposeField;            // 의뢰 목적/사유
    @FXML private TextField attachmentsField;       // 첨부자료

    // ▶ 환자 정보 자동 세팅
    public void setPatientInfo(PatientDTO patient) {
        if (patient == null) return;
        nameField.setText(patient.getName());
        rrnField.setText(patient.getRnn());
        addressField.setText(patient.getAddress());
        phoneNumField.setText(patient.getPhone());

        // 성별 자동 선택 (성별값은 "남"/"여" 가정)
        if ("남".equals(patient.getGender())) {
            maleRadio.setSelected(true);
        } else if ("여".equals(patient.getGender())) {
            femaleRadio.setSelected(true);
        }
    }

    // ▶ 의사 정보 자동 세팅
    public void setDoctorInfo(StaffUser doctor) {
        if (doctor == null) return;
//        doctorNameField.setText(doctor.getName());
//        licenseNumberField.setText(doctor.getUid());
        departmentField.setText(doctor.getDepartment());
        referrerPositionField.setText(doctor.getRank());
    }

    // 필요시 차트에서 진단, 증상, 비고 등도 자동 세팅 가능
    public void setChartInfo(ChartDTO chart) {
        if (chart == null) {
            System.out.println("차트 정보가 null입니다.");
            return;
        }
        System.out.println("받아온 chart_id: " + chart.getChart_id());
        chartNumberField.setText(chart.getChart_id());
//        diagnosisField.setText(chart.getDiagnosis());
//        notesField.setText(chart.getNote());
    }

    // ▶ HTML 템플릿 불러오기
    private String loadHtmlTemplate() {
        try (InputStream is = getClass().getResourceAsStream(
                "/com/medicon/medicon/templates/referral_letter_template.html")) {
            if (is == null) {
                System.err.println("템플릿 파일을 찾을 수 없습니다.");
                return null;
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // ▶ 템플릿에 값 채워넣기
    private String fillTemplate(String template) {
        String gender = ((RadioButton)genderGroup.getSelectedToggle()).getText();
        return template
                .replace("${chartNumber}", chartNumberField.getText())
                .replace("${referralNumber}", referralNumberField.getText())
                .replace("${referralDate}", referralDatePicker.getValue()!=null?referralDatePicker.getValue().toString():"")
                .replace("${name}", nameField.getText())
                .replace("${rrn}", rrnField.getText())
                .replace("${gender}", gender)
                .replace("${phoneNum}", phoneNumField.getText())
                .replace("${address}", addressField.getText())
                .replace("${hospitalName}", hospitalNameField.getText())
                .replace("${hospitalPhoneNum}", hospitalPhoneNumField.getText())
                .replace("${department}", departmentField.getText())
                .replace("${referrerPosition}", referrerPositionField.getText())
                .replace("${receiverInstitution}", receiverInstitutionField.getText())
                .replace("${receiverDept}", receiverDeptField.getText())
                .replace("${receiverPhone}", receiverPhoneField.getText())
                .replace("${testResults}", testResultsField.getText())
                .replace("${medications}", medicationsField.getText())
                .replace("${purpose}", purposeField.getText())
                .replace("${attachments}", attachmentsField.getText());
    }

    // ▶ HTML 임시 파일로 미리보기 실행
    private void previewHtml(String filledHtml) {
        try {
            // 1. output 폴더 생성
            Path outputDir = Paths.get("output");
            Files.createDirectories(outputDir);

            // 2. HTML 저장
            Path htmlPath = outputDir.resolve("referral_letter_preview.html");
            Files.writeString(htmlPath, filledHtml);

            // 3. CSS 복사 - classpath 기반
            try (InputStream cssStream = getClass().getResourceAsStream("/com/medicon/medicon/templates/ReferralLetterStyle.css")) {
                if (cssStream == null) {
                    throw new FileNotFoundException("ReferralLetterStyle.css 파일을 classpath에서 찾을 수 없습니다.");
                }

                Path cssTarget = outputDir.resolve("ReferralLetterStyle.css");
                Files.copy(cssStream, cssTarget, StandardCopyOption.REPLACE_EXISTING);
            }

            // 4. 브라우저로 열기
            Desktop.getDesktop().browse(htmlPath.toUri());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ▶ 미리보기 버튼 이벤트
    @FXML private void handlePrintPreview() {
        String template = loadHtmlTemplate();
        if (template == null) return;

        String filledHtml = fillTemplate(template);
        previewHtml(filledHtml);
    }

    // ▶ 저장 버튼 이벤트
    @FXML private void handleSave() {
        // TODO: 저장 처리
        closeWindow();
    }

    // ▶ 창 닫기 이벤트
    @FXML private void handleClose() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage)chartNumberField.getScene().getWindow(); stage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        genderGroup = new ToggleGroup();
        maleRadio.setToggleGroup(genderGroup);
        femaleRadio.setToggleGroup(genderGroup);
        maleRadio.setSelected(true);
    }
}
