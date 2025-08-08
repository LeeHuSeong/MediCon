package com.medicon.medicon.controller.staff;

import com.medicon.medicon.config.AppConfig;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.TilePane;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

//직원 추가를 위한 컨트롤러
public class RegisterController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField phoneField;
    @FXML private TilePane rankPane;
    @FXML private TilePane departmentPane;

    private String selectedRole = "";
    private String selectedRank = "";
    private String selectedDepartment = "";

    private final List<String> doctorRanks = List.of("인턴", "레지던트", "전임의", "조교수", "부교수", "교수");
    private final List<String> nurseRanks = List.of("간호사", "수간호사", "책임간호사", "수석간호사");

    private final List<String> departments = List.of(
            "내과", "외과", "소아과", "산부인과", "정형외과", "피부과", "비뇨기과", "신경외과", "정신과"
    );

    @FXML
    public void initialize() {
        generateDepartmentButtons();
    }

    @FXML
    public void handleDoctorSelect() {
        selectedRole = "doctor";
        generateRankButtons(doctorRanks);
    }

    @FXML
    public void handleNurseSelect() {
        selectedRole = "nurse";
        generateRankButtons(nurseRanks);
    }

    private void generateRankButtons(List<String> ranks) {
        rankPane.getChildren().clear();
        ToggleGroup rankGroup = new ToggleGroup();

        for (String rank : ranks) {
            ToggleButton btn = new ToggleButton(rank);
            btn.setToggleGroup(rankGroup);
            btn.getStyleClass().add("tile-button");  // 공통 스타일 사용
            rankPane.getChildren().add(btn);
        }

        rankGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedRank = ((ToggleButton) newVal).getText();
            }
        });
    }

    private void generateDepartmentButtons() {
        departmentPane.getChildren().clear();
        ToggleGroup deptGroup = new ToggleGroup();

        for (String dept : departments) {
            ToggleButton btn = new ToggleButton(dept);
            btn.setToggleGroup(deptGroup);
            btn.getStyleClass().add("tile-button");  // 공통 스타일 사용
            departmentPane.getChildren().add(btn);
        }

        deptGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedDepartment = ((ToggleButton) newVal).getText();
            }
        });
    }

    private void highlightSelected(Button selectedBtn, TilePane pane) {
        for (var node : pane.getChildren()) {
            if (node instanceof Button btn) {
                btn.setStyle("-fx-background-color: lightgray;");
            }
        }
        selectedBtn.setStyle("-fx-background-color: #2196f3; -fx-text-fill: white;");
    }

    @FXML
    public void handleRegister() {
        String name = nameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String phone = phoneField.getText();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty()
                || selectedRole.isEmpty() || selectedRank.isEmpty() || selectedDepartment.isEmpty()) {
            showAlert("모든 필드를 입력해주세요.");
            return;
        }

        try {
            URL url = new URL(AppConfig.SERVER_BASE_URL + AppConfig.REGISTER_STAFF_ENDPOINT);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setDoOutput(true);

            String json = String.format("""
            {
              "name": "%s",
              "email": "%s",
              "password": "%s",
              "phone": "%s",
              "role": "%s",
              "rank": "%s",
              "department": "%s"
            }
            """, name, email, password, phone, selectedRole, selectedRank, selectedDepartment);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = json.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == 200 || responseCode == 201) {
                showAlert("등록이 완료되었습니다.");
                clearForm();
            } else {
                showAlert("등록 실패 (응답 코드: " + responseCode + ")");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("서버 요청 중 오류 발생");
        }
    }

    private void clearForm() {
        nameField.clear();
        emailField.clear();
        passwordField.clear();
        phoneField.clear();
        selectedRole = "";
        selectedRank = "";
        selectedDepartment = "";
        rankPane.getChildren().clear();
        departmentPane.getChildren().clear();
        generateDepartmentButtons();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
