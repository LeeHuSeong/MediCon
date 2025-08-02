package com.medicon.medicon.controller.medic;

import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.*;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class QuestionnaireManagementController implements Initializable {
    @FXML private RadioButton Radio1;
    @FXML private RadioButton Radio2;
    @FXML private RadioButton Radio3;
    @FXML private RadioButton Radio4;
    @FXML private RadioButton Radio5;

    @FXML private HBox symptoms; // 체크박스 그룹
    @FXML private HBox onsetOfSymptoms; // 라디오 버튼 그룹
    @FXML private TextArea symytomElse; // 기타 증상 텍스트 에어리어

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ToggleGroup group = new ToggleGroup();
        Radio1.setToggleGroup(group);
        Radio2.setToggleGroup(group);
        Radio3.setToggleGroup(group);
        Radio4.setToggleGroup(group);
        Radio5.setToggleGroup(group);

        // 체크박스 비활성화
        for (Node node : symptoms.getChildren()) {
            if (node instanceof CheckBox) {
                node.setDisable(true);
            }
        }

        // 라디오 버튼 비활성화
        for (Node node : onsetOfSymptoms.getChildren()) {
            if (node instanceof RadioButton) {
                node.setDisable(true);
            }
        }

        // 기타 텍스트 영역 비활성화 (이미 editable="false"지만 혹시 몰라 명시)
        symytomElse.setDisable(true);
    }
    @FXML
    private void handleAddQuestionnaire(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/medicon/medicon/view/medic/medic_main/AddQuestionnaireForm.fxml"));
            Parent popupRoot = loader.load();

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("추가 문진 작성");
            popupStage.setScene(new Scene(popupRoot));
            popupStage.setResizable(false);
            popupStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

