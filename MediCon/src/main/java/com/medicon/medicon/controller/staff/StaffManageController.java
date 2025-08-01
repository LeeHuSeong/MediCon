package com.medicon.medicon.controller.staff;

import com.google.gson.Gson;
import com.medicon.medicon.config.AppConfig;
import com.medicon.medicon.model.StaffUser;
import com.medicon.medicon.util.SalaryBaseTable;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.TilePane;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class StaffManageController {
    @FXML private ToggleGroup roleToggleGroup;

    @FXML private TextField searchField;
    @FXML private ListView<StaffUser> userListView;

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private ComboBox<String> authorityComboBox;  // 권한 콤보박스

    @FXML private TilePane departmentPane;
    @FXML private TilePane rankPane;

    private ObservableList<StaffUser> staffList = FXCollections.observableArrayList();
    private String selectedDepartment;
    private String selectedRank;

    @FXML
    public void initialize() {
        // 권한 콤보박스 초기화 (환자, 간호사, 의사)
        authorityComboBox.setItems(FXCollections.observableArrayList("환자", "간호사", "의사"));

        // 직원 목록 보기 설정
        userListView.setItems(staffList);
        userListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(StaffUser item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName() + " (" + item.getRole() + ") - " + item.getDepartment() + " / " + item.getRank());
            }
        });

        // 직원 선택 시 해당 정보 채우기
        userListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                nameField.setText(newVal.getName());
                emailField.setText(newVal.getEmail());
                phoneField.setText(newVal.getPhone());
                authorityComboBox.setValue(mapAuthorityToString(newVal.getAuthority())); // 권한 설정 (수정 불가)

                // 역할에 따라 부서 및 직급 버튼 초기화
                initializeDepartmentButtons(newVal.getRole());
                initializeRankButtons(newVal.getRole());

                // 선택된 부서 및 직급 저장
                selectedDepartment = newVal.getDepartment();
                selectedRank = newVal.getRank();
            }
        });

        // 역할 선택 시 직원 목록 불러오기 및 직급, 부서 버튼 초기화
        roleToggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null) {
                String selectedRole = (String) newToggle.getUserData();
                fetchStaffList(selectedRole);  // 선택된 역할로 직원 목록 불러오기
                initializeRankButtons(selectedRole); // 직급 버튼 초기화
                initializeDepartmentButtons(selectedRole); // 부서 버튼 초기화
            }
        });

        // 기본적으로 "전체" 역할 선택하고 직원 목록 불러오기
        fetchStaffList("all");
    }

    // 검색 기능
    @FXML
    private void onSearch() {
        String keyword = searchField.getText().trim();
        String role = (String) roleToggleGroup.getSelectedToggle().getUserData();
        fetchStaffList(role, keyword);  // 선택된 역할과 키워드로 직원 목록 검색
    }

    // 수정 버튼 클릭 시
    @FXML
    private void onUpdateUser() {
        StaffUser selected = userListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("수정 오류", "수정할 직원을 선택하세요.");
            return;
        }

        // 수정할 값 가져오기
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String department = selectedDepartment;  // 클릭한 부서
        String rank = selectedRank;  // 클릭한 직급
        String role = selected.getRole();  // 직원 역할 (의사/간호사 등)

        long basePay = SalaryBaseTable.getBasePay(role, rank);  // 직급에 따른 기본급

        // 서버로 보내는 URL 설정
        try {
            URL url = new URL(AppConfig.SERVER_BASE_URL + "/api/staff/update/" + selected.getUid() + "?role=" + role);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);

            // 수정할 정보 담기
            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("name", name);
            requestMap.put("phone", phone);
            requestMap.put("department", department);  // 선택된 부서
            requestMap.put("rank", rank);  // 선택된 직급
            requestMap.put("basePay", basePay);  // 기본급

            // JSON으로 변환하여 서버로 보내기
            String json = new Gson().toJson(requestMap);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes(StandardCharsets.UTF_8));
            }

            // 서버 응답 확인
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                showAlert("수정 완료", "직원 정보가 성공적으로 수정되었습니다.");
                String currentRole = (String) roleToggleGroup.getSelectedToggle().getUserData(); // Get the currently selected role
                fetchStaffList(currentRole);  // Fetch staff list for the selected role
            } else {
                showAlert("수정 실패", "서버 응답 오류: " + responseCode);
            }
        } catch (Exception e) {
            showAlert("수정 실패", "에러: " + e.getMessage());
        }
    }

    // Delete selected employee
    @FXML
    private void onDeleteUser() {
        StaffUser selected = userListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("삭제 오류", "삭제할 직원을 선택하세요.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "정말 삭제하시겠습니까?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText("직원 삭제 확인");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.YES) return;

        String uid = selected.getUid();
        String role = selected.getRole();

        try {
            URL url = new URL(AppConfig.SERVER_BASE_URL + "/api/staff/delete/" + uid + "?role=" + role);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                showAlert("삭제 완료", "직원이 성공적으로 삭제되었습니다.");
                fetchStaffList(role);  // Fetch staff list for the current role after deletion
            } else {
                showAlert("삭제 실패", "서버 응답 오류: " + responseCode);
            }
        } catch (Exception e) {
            showAlert("삭제 실패", "에러: " + e.getMessage());
        }
    }

    // Reset the fields and selection
    @FXML
    private void onReset() {
        userListView.getSelectionModel().clearSelection();
        nameField.clear();
        emailField.clear();
        phoneField.clear();
        authorityComboBox.getSelectionModel().clearSelection();
        // Reset selected department and rank as well
        selectedDepartment = null;
        selectedRank = null;
    }

    // Fetch staff list from server based on role and search keyword
    private void fetchStaffList(String role) {
        fetchStaffList(role, "");
    }

    private void fetchStaffList(String role, String keyword) {
        staffList.clear();  // Clear existing list

        try {
            String urlStr = AppConfig.SERVER_BASE_URL + "/api/staff/list?role=" + role;
            if (!keyword.isEmpty()) {
                urlStr += "&keyword=" + keyword;
            }

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) sb.append(line);

                    // Parse response and update staff list
                    Map<String, Object> response = new Gson().fromJson(sb.toString(), Map.class);
                    List<Map<String, Object>> data = (List<Map<String, Object>>) response.get("data");
                    for (Map<String, Object> entry : data) {
                        StaffUser user = new Gson().fromJson(new Gson().toJson(entry), StaffUser.class);
                        staffList.add(user);  // Add to staff list
                    }
                }
            } else {
                showAlert("조회 실패", "직원 목록을 불러올 수 없습니다.");
            }
        } catch (Exception e) {
            showAlert("네트워크 오류", "에러: " + e.getMessage());
        }
    }

    // Show alert dialog
    private void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    // Map authority code to string (환자, 간호사, 의사)
    private String mapAuthorityToString(int code) {
        switch (code) {
            case 0: return "환자";
            case 1: return "간호사";
            case 2: return "의사";
            default: return "알 수 없음";
        }
    }

    // Reverse map authority string to code
    private int mapAuthorityToStringReverse(String authority) {
        switch (authority) {
            case "환자": return 0;
            case "간호사": return 1;
            case "의사": return 2;
            default: return -1; // Invalid authority
        }
    }

    // Initialize rank buttons based on selected role
    private void initializeRankButtons(String role) {
        List<String> ranks = new ArrayList<>();

        if ("doctor".equalsIgnoreCase(role)) {
            ranks = Arrays.asList("인턴", "레지던트", "전임의", "조교수", "부교수", "교수");
        } else if ("nurse".equalsIgnoreCase(role)) {
            ranks = Arrays.asList("간호사", "수간호사", "책임간호사", "수석간호사");
        }

        rankPane.getChildren().clear();  // 기존 버튼 초기화
        ToggleGroup rankGroup = new ToggleGroup();

        // 직급 버튼을 TilePane에 추가
        for (String rank : ranks) {
            ToggleButton btn = new ToggleButton(rank);
            btn.setToggleGroup(rankGroup);
            rankPane.getChildren().add(btn);
        }

        // 버튼 클릭 시 직급 선택 저장
        rankGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null) {
                selectedRank = ((ToggleButton) newToggle).getText();  // 선택된 직급 저장
            }
        });
    }

    // Initialize department buttons based on selected role
    private void initializeDepartmentButtons(String role) {
        List<String> departments = Arrays.asList("내과", "외과", "소아과", "산부인과", "정형외과", "응급의학과");
        ToggleGroup deptGroup = new ToggleGroup();

        departmentPane.getChildren().clear();  // 기존 버튼 초기화

        // 부서 버튼을 TilePane에 추가
        for (String dept : departments) {
            ToggleButton btn = new ToggleButton(dept);
            btn.setToggleGroup(deptGroup);
            departmentPane.getChildren().add(btn);
        }

        // 버튼 클릭 시 부서 선택 저장
        deptGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedDepartment = ((ToggleButton) newVal).getText();  // 선택된 부서 저장
            }
        });
    }
}
