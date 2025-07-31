package com.medicon.medicon.controller.staff;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.medicon.medicon.config.AppConfig;
import com.medicon.medicon.model.StaffUser;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

public class ListController {

    @FXML private RadioButton doctorRadio;
    @FXML private RadioButton nurseRadio;
    @FXML private ToggleGroup roleToggleGroup;

    @FXML private TextField searchField;
    @FXML private ListView<StaffUser> userListView;

    private List<StaffUser> fullUserList;

    @FXML
    public void initialize() {
        doctorRadio.setUserData("doctor");
        nurseRadio.setUserData("nurse");

        // 역할 변경 시 API 호출
        roleToggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null) {
                String role = newToggle.getUserData().toString();
                loadUsersByRole(role);
            }
        });

        // 검색 기능
        searchField.textProperty().addListener((obs, oldVal, newVal) -> onSearch());

        userListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(StaffUser user, boolean empty) {
                super.updateItem(user, empty);
                if (empty || user == null) {
                    setText(null);
                } else {
                    setText(String.format("%s (%s)\n전화: %s | 직급: %s",
                            user.getName(), user.getUid(),
                            user.getPhone() != null ? user.getPhone() : "N/A",
                            user.getRank() != null ? user.getRank() : "N/A"));
                }
            }
        });
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

    private void loadUsersByRole(String role) {
        try {
            String urlStr = AppConfig.SERVER_BASE_URL + "/api/staff/list?role=" + role;
            HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            reader.close();

            Gson gson = new Gson();
            JsonObject json = gson.fromJson(sb.toString(), JsonObject.class);
            Type listType = new TypeToken<List<StaffUser>>(){}.getType();
            fullUserList = gson.fromJson(json.getAsJsonArray("data"), listType);

            userListView.getItems().setAll(fullUserList);
        } catch (Exception e) {
            showAlert("직원 목록 불러오기 실패: " + e.getMessage());
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("경고");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
