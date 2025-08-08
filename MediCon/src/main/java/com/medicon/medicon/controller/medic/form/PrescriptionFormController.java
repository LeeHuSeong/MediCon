package com.medicon.medicon.controller.medic.form;

import com.medicon.medicon.model.ChartDTO;
import com.medicon.medicon.model.PatientDTO;
import com.medicon.medicon.model.StaffUser;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class PrescriptionFormController implements Initializable {
    @FXML private TextField chartNumberField;
    @FXML private TextField prescriptionNumberField;
    @FXML private DatePicker prescriptionDatePicker;
    @FXML private DatePicker validUntilDatePicker;

    @FXML private TextField nameField;
    @FXML private TextField rrnField;
    @FXML private ToggleGroup genderGroup;
    @FXML private RadioButton maleRadio;
    @FXML private RadioButton femaleRadio;
    @FXML private TextField phoneNumField;
    @FXML private TextField addressField;

    @FXML private TextField hospitalNameField;
    @FXML private TextField hospitalPhoneNumField;
    @FXML private TextField doctorNameField;
    @FXML private TextField licenseNumberField;

    @FXML private TableView<Medication> medicationTable;
    @FXML private TableColumn<Medication, String> colDrugName;
    @FXML private TableColumn<Medication, String> colDosage;
    @FXML private TableColumn<Medication, String> colRoute;
    @FXML private TableColumn<Medication, String> colDuration;
    @FXML private TableColumn<Medication, String> colInstructions;
    @FXML private TextArea generalInstructionsField;
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
//        departmentField.setText(doctor.getDepartment());
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

    // 모델 클래스
    public static class Medication {
        private final SimpleStringProperty drugName;
        private final SimpleStringProperty dosage;
        private final SimpleStringProperty route;
        private final SimpleStringProperty duration;
        private final SimpleStringProperty instructions;
        public Medication(String drugName, String dosage, String route, String duration, String instructions) {
            this.drugName = new SimpleStringProperty(drugName);
            this.dosage = new SimpleStringProperty(dosage);
            this.route = new SimpleStringProperty(route);
            this.duration = new SimpleStringProperty(duration);
            this.instructions = new SimpleStringProperty(instructions);
        }
        public SimpleStringProperty drugNameProperty() { return drugName; }
        public SimpleStringProperty dosageProperty() { return dosage; }
        public SimpleStringProperty routeProperty() { return route; }
        public SimpleStringProperty durationProperty() { return duration; }
        public SimpleStringProperty instructionsProperty() { return instructions; }
        public String getDrugName() { return drugName.get(); }
        public String getDosage() { return dosage.get(); }
        public String getRoute() { return route.get(); }
        public String getDuration() { return duration.get(); }
        public String getInstructions() { return instructions.get(); }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        genderGroup = new ToggleGroup();
        maleRadio.setToggleGroup(genderGroup);
        femaleRadio.setToggleGroup(genderGroup);
        maleRadio.setSelected(true);

        medicationTable.setEditable(true);

        colDrugName.setCellFactory(TextFieldTableCell.forTableColumn());
        colDosage   .setCellFactory(TextFieldTableCell.forTableColumn());
        colRoute    .setCellFactory(TextFieldTableCell.forTableColumn());
        colDuration .setCellFactory(TextFieldTableCell.forTableColumn());
        colInstructions.setCellFactory(TextFieldTableCell.forTableColumn());

        colDrugName.setOnEditCommit(e -> e.getRowValue().drugNameProperty().set(e.getNewValue()));
        colDosage   .setOnEditCommit(e -> e.getRowValue().dosageProperty().set(e.getNewValue()));
        colRoute    .setOnEditCommit(e -> e.getRowValue().routeProperty().set(e.getNewValue()));
        colDuration .setOnEditCommit(e -> e.getRowValue().durationProperty().set(e.getNewValue()));
        colInstructions
                .setOnEditCommit(e -> e.getRowValue().instructionsProperty().set(e.getNewValue()));
    }

    @FXML
    private void handleAddRow() {
        medicationTable.getItems().add(new Medication("", "", "", "", ""));
    }

    @FXML
    private void handleRemoveRow() {
        Medication selected = medicationTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            medicationTable.getItems().remove(selected);
        }
    }

    // HTML 불러오기
    private String loadHtmlTemplate() {
        try (InputStream is = getClass().getResourceAsStream(
                "/com/medicon/medicon/templates/prescription_template.html")) {
            if (is == null) {
                System.err.println("템플릿을 찾을 수 없습니다.");
                return null;
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 템플릿 채우기
    private String fillTemplate(String template) {
        String gender = ((RadioButton)genderGroup.getSelectedToggle()).getText();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        StringBuilder medRows = new StringBuilder();
        for (Medication m : medicationTable.getItems()) {
            medRows.append("<tr>")
                    .append("<td>").append(m.getDrugName()).append("</td>")
                    .append("<td>").append(m.getDosage()).append("</td>")
                    .append("<td>").append(m.getRoute()).append("</td>")
                    .append("<td>").append(m.getDuration()).append("</td>")
                    .append("<td colspan='3'>").append(m.getInstructions()).append("</td>")
                    .append("</tr>");
        }
        return template
                .replace("${chartNumber}", chartNumberField.getText())
                .replace("${prescriptionNumber}", prescriptionNumberField.getText())
                .replace("${prescriptionDate}", prescriptionDatePicker.getValue()!=null?prescriptionDatePicker.getValue().format(fmt):"")
                .replace("${validUntil}", validUntilDatePicker.getValue()!=null?validUntilDatePicker.getValue().format(fmt):"")
                .replace("${name}", nameField.getText())
                .replace("${rrn}", rrnField.getText())
                .replace("${gender}", gender)
                .replace("${phoneNum}", phoneNumField.getText())
                .replace("${address}", addressField.getText())
                .replace("${hospitalName}", hospitalNameField.getText())
                .replace("${hospitalPhoneNum}", hospitalPhoneNumField.getText())
                .replace("${doctorName}", doctorNameField.getText())
                .replace("${licenseNumber}", licenseNumberField.getText())
                .replace("${medicationRows}", medRows.toString())
                .replace("${generalInstructions}", generalInstructionsField.getText());
    }

    // 웹 미리보기
    private void previewHtml(String html) {
        try {
            Path dir = Paths.get("output"); Files.createDirectories(dir);
            Path htmlFile = dir.resolve("prescription_preview.html");
            Files.writeString(htmlFile, html);
            try (InputStream css = getClass().getResourceAsStream(
                    "/com/medicon/medicon/templates/PrescriptionStyle.css")) {
                Path cssFile = dir.resolve("PrescriptionStyle.css");
                Files.copy(css, cssFile, StandardCopyOption.REPLACE_EXISTING);
            }
            Desktop.getDesktop().browse(htmlFile.toUri());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handlePrintPreview() {
        String tpl = loadHtmlTemplate(); if (tpl==null) return;
        previewHtml(fillTemplate(tpl));
    }

    @FXML
    private void handleSave() {
        // TODO: 저장 로직 구현
        closeWindow();
    }

    @FXML
    private void handleClose() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) chartNumberField.getScene().getWindow();
        stage.close();
    }
}
