package com.medicon.medicon.controller.medic;

import com.medicon.medicon.model.StaffUser;
import com.medicon.medicon.service.StaffApiService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

public class MedicMainController {

    @FXML private BorderPane mainBorderPane;
    @FXML private String medicUid;
    private final StaffApiService staffApiService = new StaffApiService();
    private StaffUser staffUser;

    public void setUid(String uid) {
        staffApiService.getStaffByUidAsync(uid).thenAccept(user -> {
            Platform.runLater(() -> {
                if (user != null) {
                    this.staffUser = user;
                }
            });
        });
    }
    public StaffUser getStaffUser() {
        return this.staffUser;
    }

    public void setCenter(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node node = loader.load();

            // 컨트롤러 인스턴스 가져오기
            Object controller = loader.getController();

            // 만약 TreatmentManagementController라면
            if (controller instanceof TreatmentManagementController & staffUser != null) {
                ((TreatmentManagementController) controller).setStaffUser(this.staffUser);
            }
            // 다른 컨트롤러가 있을 경우도 if문 추가로 분기 가능

            mainBorderPane.setCenter(node);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        try {
            FXMLLoader topBarLoader = new FXMLLoader(getClass().getResource("/com/medicon/medicon/view/medic/medic_header/MedicTopBar.fxml"));
            Node topBar = topBarLoader.load();

            MedicTopBarController controller = topBarLoader.getController();
            controller.setMainController(this); // 🔗 MainController 연결

            mainBorderPane.setTop(topBar);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}