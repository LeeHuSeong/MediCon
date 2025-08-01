package com.medicon.medicon.controller.salary;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.medicon.medicon.config.AppConfig;
import com.medicon.medicon.model.SalaryResponse;
import com.medicon.medicon.model.SalaryRecordRequest;
import com.medicon.medicon.model.StaffUser;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

public class SalaryManageController {

    @FXML private RadioButton doctorRadio;
    @FXML private RadioButton nurseRadio;
    @FXML private ToggleGroup roleToggleGroup;

    @FXML private TextField searchField;
    @FXML private ListView<StaffUser> userListView;
    @FXML private ListView<String> monthListView;

    @FXML private TextField basePayField;
    @FXML private TextField bonusField;

    @FXML private Label totalPayLabel;
    @FXML private Label pensionLabel;
    @FXML private Label healthLabel;
    @FXML private Label employmentLabel;
    @FXML private Label incomeLabel;
    @FXML private Label localTaxLabel;
    @FXML private Label netPayLabel;

    private List<StaffUser> fullUserList;
    private String selectedUid;
    private String selectedRole;
    private String selectedYearMonth;

    @FXML
    public void initialize() {
        doctorRadio.setUserData("doctor");
        nurseRadio.setUserData("nurse");

        roleToggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null) {
                selectedRole = newToggle.getUserData().toString();
                loadUsersByRole(selectedRole);
            }
        });

        userListView.setOnMouseClicked(this::onUserSelected);
        monthListView.setOnMouseClicked(this::onMonthSelected);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> onSearch());
    }

    @FXML
    public void onSearch() {
        String keyword = searchField.getText().toLowerCase();
        if (fullUserList == null) return;

        List<StaffUser> filtered = fullUserList.stream()
                .filter(user -> user.getName().toLowerCase().contains(keyword))
                .collect(Collectors.toList());
        userListView.getItems().setAll(filtered);
    }

    private void onUserSelected(MouseEvent event) {
        StaffUser selected = userListView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        selectedUid = selected.getUid();
        loadMonthList(selectedUid, selectedRole);
    }

    private void onMonthSelected(MouseEvent event) {
        selectedYearMonth = monthListView.getSelectionModel().getSelectedItem();
        if (selectedUid == null || selectedYearMonth == null) return;

        SalaryResponse salary = fetchSalary(selectedUid, selectedRole, selectedYearMonth);
        if (salary != null) {
            basePayField.setText(String.valueOf(salary.getBasePay()));
            bonusField.setText(String.valueOf(salary.getBonus()));
            updateCalculation(salary);
        }
    }

    private void updateCalculation(SalaryResponse salary) {
        long totalPay = salary.getBasePay() + salary.getBonus();
        long pension = Math.round(totalPay * 0.09);
        long health = Math.round(totalPay * 0.07);
        long employment = Math.round(totalPay * 0.009);
        long income = Math.round(totalPay * 0.03);
        long localTax = Math.round(income * 0.1);
        long net = totalPay - (pension + health + employment + income + localTax);

        totalPayLabel.setText(totalPay + "원");
        pensionLabel.setText(pension + "원");
        healthLabel.setText(health + "원");
        employmentLabel.setText(employment + "원");
        incomeLabel.setText(income + "원");
        localTaxLabel.setText(localTax + "원");
        netPayLabel.setText(net + "원");
    }

    @FXML
    public void onSave() {
        try {
            if (selectedUid == null || selectedRole == null || selectedYearMonth == null) {
                showAlert("사용자 및 날짜를 먼저 선택하세요.", Alert.AlertType.WARNING);
                return;
            }

            long basePay = Long.parseLong(basePayField.getText());
            long bonus = Long.parseLong(bonusField.getText());

            SalaryRecordRequest req = new SalaryRecordRequest(
                    Integer.parseInt(selectedYearMonth.split("-")[0]),
                    Integer.parseInt(selectedYearMonth.split("-")[1]),
                    basePay,
                    bonus
            );

            String urlStr = String.format("%s/api/staff/salary/%s?role=%s&yearMonth=%s",
                    AppConfig.SERVER_BASE_URL, selectedUid, selectedRole, selectedYearMonth);

            HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            Gson gson = new Gson();
            try (OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream())) {
                writer.write(gson.toJson(req));
                writer.flush();
            }

            if (conn.getResponseCode() == 200) {
                showAlert("수정 성공", Alert.AlertType.INFORMATION);
                onMonthSelected(null);
            } else {
                showAlert("수정 실패: " + conn.getResponseCode(), Alert.AlertType.ERROR);
            }

        } catch (Exception e) {
            showAlert("수정 오류: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void onDelete() {
        try {
            if (selectedUid == null || selectedRole == null || selectedYearMonth == null) {
                showAlert("삭제할 급여를 선택하세요.", Alert.AlertType.WARNING);
                return;
            }

            String urlStr = String.format("%s/api/staff/salary/%s?role=%s&yearMonth=%s",
                    AppConfig.SERVER_BASE_URL, selectedUid, selectedRole, selectedYearMonth);

            HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
            conn.setRequestMethod("DELETE");

            if (conn.getResponseCode() == 200) {
                showAlert("삭제 성공", Alert.AlertType.INFORMATION);
                basePayField.clear();
                bonusField.clear();
                totalPayLabel.setText("");
                pensionLabel.setText("");
                healthLabel.setText("");
                employmentLabel.setText("");
                incomeLabel.setText("");
                localTaxLabel.setText("");
                netPayLabel.setText("");
                loadMonthList(selectedUid, selectedRole);
            } else {
                showAlert("삭제 실패: " + conn.getResponseCode(), Alert.AlertType.ERROR);
            }

        } catch (Exception e) {
            showAlert("삭제 오류: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadUsersByRole(String role) {
        try {
            String urlStr = AppConfig.SERVER_BASE_URL + "/api/staff/list?role=" + role;
            HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String response = reader.lines().collect(Collectors.joining());
            reader.close();

            Gson gson = new Gson();
            JsonObject json = gson.fromJson(response, JsonObject.class);
            Type listType = new TypeToken<List<StaffUser>>() {}.getType();
            fullUserList = gson.fromJson(json.getAsJsonArray("data"), listType);

            userListView.getItems().setAll(fullUserList);
        } catch (Exception e) {
            showAlert("사용자 불러오기 실패: " + e.getMessage(), Alert.AlertType.WARNING);
        }
    }

    private void loadMonthList(String uid, String role) {
        try {
            String urlStr = String.format("%s/api/staff/salary/%s?role=%s", AppConfig.SERVER_BASE_URL, uid, role);
            HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String response = reader.lines().collect(Collectors.joining());
            reader.close();

            Gson gson = new Gson();
            JsonObject json = gson.fromJson(response, JsonObject.class);
            List<JsonObject> months = gson.fromJson(json.getAsJsonArray("data"), new TypeToken<List<JsonObject>>(){}.getType());

            List<String> ids = months.stream().map(m -> m.get("id").getAsString()).collect(Collectors.toList());
            monthListView.getItems().setAll(ids);
        } catch (Exception e) {
            showAlert("월 목록 불러오기 실패: " + e.getMessage(), Alert.AlertType.WARNING);
        }
    }

    private SalaryResponse fetchSalary(String uid, String role, String yearMonth) {
        try {
            String[] parts = yearMonth.split("-");
            String urlStr = String.format(
                    "%s/api/staff/salary/%s?role=%s&year=%s&month=%s",
                    AppConfig.SERVER_BASE_URL, uid, role, parts[0], parts[1]);

            HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String response = reader.lines().collect(Collectors.joining());
            reader.close();

            Gson gson = new Gson();
            JsonObject json = gson.fromJson(response, JsonObject.class);
            return gson.fromJson(json.get("data"), SalaryResponse.class);

        } catch (Exception e) {
            showAlert("급여 불러오기 실패: " + e.getMessage(), Alert.AlertType.ERROR);
            return null;
        }
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("알림");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
