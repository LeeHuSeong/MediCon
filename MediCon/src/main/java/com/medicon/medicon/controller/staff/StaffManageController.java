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
import java.net.URLEncoder;
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

    @FXML
    private void onSearch() {
        String keyword = searchField.getText().trim();  // 입력된 검색어
        String role = (String) roleToggleGroup.getSelectedToggle().getUserData();  // 선택된 역할

        // 역할에 따른 값 설정 (전체, 의사, 간호사)
        if (role.equals("전체")) {
            role = "all";
        } else if (role.equals("의사")) {
            role = "doctor";
        } else if (role.equals("간호사")) {
            role = "nurse";
        }

        // 빈 검색어 입력을 막고, 검색어가 있을 때만 요청 보내기
        if (keyword.isEmpty()) {
            keyword = null;
            showAlert("검색 오류", "검색어를 입력해 주세요.");
            return;
        }

        try {
            String encodedKeyword = URLEncoder.encode(keyword, "UTF-8");
            fetchStaffList(role, encodedKeyword);  // 역할과 검색어에 맞춰 직원 목록 가져오기
        } catch (UnsupportedEncodingException e) {
            showAlert("검색 오류", "검색어 인코딩에 실패했습니다.");
            e.printStackTrace();
        }
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

    // 직원 목록 가져오는 메서드
    private void fetchStaffList(String role, String keyword) {
        staffList.clear();  // 기존 리스트 초기화

        try {
            // URL 문자열을 잘 확인하면서 보내기
            String urlStr = AppConfig.SERVER_BASE_URL + "/api/staff/list?role=" + role + "&keyword=" + keyword;

            // 서버로 요청 보내기
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            //System.out.println("응답 코드: " + responseCode);  // 응답 코드 로그

            if (responseCode == 200) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) sb.append(line);

                    // 서버 응답을 JSON으로 파싱
                    Map<String, Object> response = new Gson().fromJson(sb.toString(), Map.class);
                    List<Map<String, Object>> data = (List<Map<String, Object>>) response.get("data");

                    // 로그: 응답 데이터 확인
                    //System.out.println("응답 데이터: " + data);

                    // 데이터가 존재하는 경우 리스트에 추가
                    if (data != null && !data.isEmpty()) {
                        for (Map<String, Object> entry : data) {
                            StaffUser user = new Gson().fromJson(new Gson().toJson(entry), StaffUser.class);
                            staffList.add(user);  // 리스트에 추가
                        }
                    } else {
                        showAlert("검색 결과 없음", "검색 조건에 맞는 직원이 없습니다.");
                    }
                }
            } else {
                showAlert("조회 실패", "직원 목록을 불러올 수 없습니다.");
            }
        } catch (Exception e) {
            showAlert("네트워크 오류", "에러: " + e.getMessage());
        }

        // UI 업데이트
        Platform.runLater(() -> {
            userListView.setItems(staffList);  // ObservableList를 ListView에 설정
        });
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
            btn.getStyleClass().add("tile-button");
            rankPane.getChildren().add(btn);
        }

        // 버튼 클릭 시 직급 선택 저장
        rankGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null) {
                selectedRank = ((ToggleButton) newToggle).getText();  // 선택된 직급 저장
            }
        });

        // 선택된 직급 버튼 강조
        for (Toggle toggle : rankGroup.getToggles()) {
            if (((ToggleButton) toggle).getText().equals(selectedRank)) {
                toggle.setSelected(true);  // 이걸 추가
                break;
            }
        }
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
            btn.getStyleClass().add("tile-button");
            departmentPane.getChildren().add(btn);
        }

        // 버튼 클릭 시 부서 선택 저장
        deptGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedDepartment = ((ToggleButton) newVal).getText();  // 선택된 부서 저장
            }
        });

        // 선택된 부서 버튼 강조
        for (Toggle toggle : deptGroup.getToggles()) {
            if (((ToggleButton) toggle).getText().equals(selectedDepartment)) {
                toggle.setSelected(true);
                break;
            }
        }
    }
}
