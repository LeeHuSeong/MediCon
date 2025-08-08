package com.medicon.medicon.controller.medic.form;

import com.medicon.medicon.model.ChartDTO;
import com.medicon.medicon.model.PatientDTO;
import com.medicon.medicon.model.StaffUser;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ResourceBundle;

public class OpinionCertificateFormController implements Initializable {
    @FXML private TextField chartNumberField;
    @FXML private DatePicker visitDatePicker;     // 진료일

    // ▶ 기본 환자 정보
    @FXML private TextField nameField;
    @FXML private TextField rrnField;
    @FXML private ToggleGroup genderGroup;
    @FXML private RadioButton maleRadio;
    @FXML private RadioButton femaleRadio;
    @FXML private TextField phoneNumField;
    @FXML private TextField addressField;

    // ▶ 병원 정보
    @FXML private TextField hospitalNameField;
    @FXML private TextField hospitalPhoneNumField;
    @FXML private TextField departmentField;

    // ▶ 의사 및 소견 정보
    @FXML private TextField doctorNameField;
    @FXML private TextField licenseNumberField;
    @FXML private TextArea doctorOpinionField;
    @FXML private TextArea treatmentPlanField;
    @FXML private TextField notesField;
    @FXML private DatePicker issueDatePicker;

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
        doctorNameField.setText(doctor.getName());
        licenseNumberField.setText(doctor.getUid());
        departmentField.setText(doctor.getDepartment());
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
        doctorOpinionField.setText(chart.getNote());
    }

    // ▶ HTML 템플릿 불러오기
    private String loadHtmlTemplate() {
        try (InputStream is = getClass().getResourceAsStream("/com/medicon/medicon/templates/opinion_certificate_template.html")) {
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
        String gender = ((RadioButton) genderGroup.getSelectedToggle()).getText();
        return template
                .replace("${chartNumber}", chartNumberField.getText())
                .replace("${visitDate}", visitDatePicker.getValue() != null ? visitDatePicker.getValue().toString() : "")
                .replace("${name}", nameField.getText())
                .replace("${rrn}", rrnField.getText())
                .replace("${gender}", gender)
                .replace("${phoneNum}", phoneNumField.getText())
                .replace("${address}", addressField.getText())
                .replace("${hospitalName}", hospitalNameField.getText())
                .replace("${hospitalPhoneNum}", hospitalPhoneNumField.getText())
                .replace("${department}", departmentField.getText())
                .replace("${doctorName}", doctorNameField.getText())
                .replace("${licenseNumber}", licenseNumberField.getText())
                .replace("${doctorOpinion}", doctorOpinionField.getText())
                .replace("${treatmentPlan}", treatmentPlanField.getText())
                .replace("${notes}", notesField.getText())
                .replace("${issueDate}", issueDatePicker.getValue() != null ? issueDatePicker.getValue().toString() : "");

    }


    // ▶ HTML 임시 파일로 미리보기 실행
    private void previewHtml(String filledHtml) {
        try {
            // 1. output 폴더 생성
            Path outputDir = Paths.get("output");
            Files.createDirectories(outputDir);

            // 2. HTML 저장
            Path htmlPath = outputDir.resolve("opinion_certificate_preview.html");
            Files.writeString(htmlPath, filledHtml);

            // 3. CSS 복사 - classpath 기반
            try (InputStream cssStream = getClass().getResourceAsStream("/com/medicon/medicon/templates/CertificateStyle.css")) {
                if (cssStream == null) {
                    throw new FileNotFoundException("CertificateStyle.css 파일을 classpath에서 찾을 수 없습니다.");
                }

                Path cssTarget = outputDir.resolve("CertificateStyle.css");
                Files.copy(cssStream, cssTarget, StandardCopyOption.REPLACE_EXISTING);
            }

            // 4. 브라우저로 열기
            Desktop.getDesktop().browse(htmlPath.toUri());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // ▶ 미리보기 버튼 이벤트
    @FXML
    private void handlePrintPreview() {
        String template = loadHtmlTemplate();
        if (template == null) return;

        String filledHtml = fillTemplate(template);
        previewHtml(filledHtml);
    }

    // ▶ 저장 버튼 이벤트
    @FXML
    private void handleSave() {
        String title = nameField.getText();
        // TODO: 저장 처리 (예: DB 저장 또는 상위 화면으로 전달 등)
        System.out.println("제목: " + title);
        closeWindow();
    }

    // ▶ 창 닫기 이벤트
    @FXML
    private void handleClose() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        genderGroup = new ToggleGroup();
        maleRadio.setToggleGroup(genderGroup);
        femaleRadio.setToggleGroup(genderGroup);
        maleRadio.setSelected(true); // 기본 선택
    }
}
