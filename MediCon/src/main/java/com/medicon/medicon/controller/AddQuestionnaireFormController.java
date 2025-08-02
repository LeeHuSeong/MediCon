package com.medicon.medicon.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class AddQuestionnaireFormController implements Initializable{
    @FXML private RadioButton addRadio1;
    @FXML private RadioButton addRadio2;
    @FXML private RadioButton addRadio3;
    @FXML private RadioButton addRadio4;
    @FXML private RadioButton addRadio5;

    @FXML private TextField addtitleField;
    @FXML private TextArea addElse;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ToggleGroup group = new ToggleGroup();
        addRadio1.setToggleGroup(group);
        addRadio2.setToggleGroup(group);
        addRadio3.setToggleGroup(group);
        addRadio4.setToggleGroup(group);
        addRadio5.setToggleGroup(group);
    }
    @FXML
    private void handleSave() {
        String title = addtitleField.getText();
        String memo = addElse.getText();

        // TODO: 저장 처리 (예: DB 저장 또는 상위 화면으로 전달 등)
        System.out.println("제목: " + title);
        System.out.println("기타 메모: " + memo);

        closeWindow();
    }

    @FXML
    private void handleClose() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) addtitleField.getScene().getWindow();
        stage.close();
    }
}
