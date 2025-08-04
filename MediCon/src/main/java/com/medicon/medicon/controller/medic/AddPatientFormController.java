package com.medicon.medicon.controller.medic;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class AddPatientFormController implements Initializable{
    @FXML private TextField addEmailField;
    public void initialize(URL location, ResourceBundle resources) {
    }

    @FXML
    private void handleSave() {
        String title = addEmailField.getText();

        // TODO: 저장 처리 (예: DB 저장 또는 상위 화면으로 전달 등)
        System.out.println("제목: " + title);

        closeWindow();
    }
    @FXML
    private void handleClose() {
        closeWindow();
    }
    @FXML
    private void closeWindow() {
        Stage stage = (Stage) addEmailField.getScene().getWindow();
        stage.close();
    }

}
