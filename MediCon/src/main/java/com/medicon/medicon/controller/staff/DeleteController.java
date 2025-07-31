package com.medicon.medicon.controller.staff;

import com.google.gson.JsonObject;
import com.medicon.medicon.config.AppConfig;
import com.medicon.medicon.model.StaffUser;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class DeleteController {

    @FXML private ComboBox<StaffUser> staffComboBox;

    @FXML
    public void initialize() {
        // 서버에서 직원 목록 받아오기 (예시용 하드코딩)
        List<StaffUser> dummyList = List.of(
                new StaffUser("uid1", "홍길동", "doctor", "전문의"),
                new StaffUser("uid2", "김간호", "nurse", "수간호사")
        );
        staffComboBox.setItems(FXCollections.observableArrayList(dummyList));
    }

    @FXML
    public void onDeleteStaff() {
        StaffUser selected = staffComboBox.getValue();
        if (selected == null) {
            showAlert("삭제할 직원을 선택하세요.");
            return;
        }

        try {
            String urlStr = AppConfig.SERVER_BASE_URL + "/api/staff/delete/" + selected.getUid();
            HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
            conn.setRequestMethod("DELETE");

            conn.connect();
            if (conn.getResponseCode() == 200) {
                showAlert("삭제가 완료되었습니다.");
                staffComboBox.getItems().remove(selected);
            } else {
                showAlert("삭제 실패: " + conn.getResponseCode());
            }

        } catch (Exception e) {
            showAlert("오류 발생: " + e.getMessage());
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("알림");
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
