package com.medicon.medicon.controller.medic;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
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

public class MedicalCertificateFormController implements Initializable {

    // ▶ 기본 환자 정보
    @FXML private TextField nameField;
    @FXML private TextField rrnField;

    @FXML private ToggleGroup genderGroup;
    @FXML private RadioButton maleRadio;
    @FXML private RadioButton femaleRadio;

    @FXML private TextField addressField;
    @FXML private TextField phoneNumField;             // 휴대전화번호

    // ▶ 병원 정보
    @FXML private TextField hospitalNameField;         // 병원명
    @FXML private TextField hospitalPhoneNumField;     // 병원 연락처

    // ▶ 진료 정보
    @FXML private TextField diagnosisField;
    @FXML private TextField departmentField;
    @FXML private TextField doctorNameField;
    @FXML private TextField licenseNumberField;
    @FXML private TextField hospitalizationPeriodField;
    @FXML private TextField dischargeDateField;        // 퇴원일
    @FXML private TextField outpatientPeriodField;
    @FXML private TextField notesField;
    @FXML private DatePicker issueDatePicker;

    // ▶ HTML 템플릿 불러오기
    private String loadHtmlTemplate() {
        try (InputStream is = getClass().getResourceAsStream("/com/medicon/medicon/templates/medical_certificate_template.html")) {
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
                .replace("${name}", nameField.getText())
                .replace("${rrn}", rrnField.getText())
                .replace("${gender}", gender)
                .replace("${address}", addressField.getText())
                .replace("${phoneNum}", phoneNumField.getText())                          // 휴대전화
                .replace("${hospitalName}", hospitalNameField.getText())                  // 병원명
                .replace("${hospitalPhoneNum}", hospitalPhoneNumField.getText())          // 병원 연락처
                .replace("${diagnosis}", diagnosisField.getText())
                .replace("${department}", departmentField.getText())
                .replace("${doctorName}", doctorNameField.getText())
                .replace("${licenseNumber}", licenseNumberField.getText())
                .replace("${hospitalizationPeriod}", hospitalizationPeriodField.getText())
                .replace("${dischargeDate}", dischargeDateField.getText())                // 퇴원일
                .replace("${outpatientPeriod}", outpatientPeriodField.getText())
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
            Path htmlPath = outputDir.resolve("medical_certificate_preview.html");
            Files.writeString(htmlPath, filledHtml);

            // 3. CSS 복사 - classpath 기반
            try (InputStream cssStream = getClass().getResourceAsStream("/com/medicon/medicon/templates/CertificateStyle.css")) {
                if (cssStream == null) {
                    throw new FileNotFoundException("DiagnosisStyle.css 파일을 classpath에서 찾을 수 없습니다.");
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
