package com.medicon.medicon.controller.medic.patient;

import com.medicon.medicon.model.PatientDTO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * 환자 관리 메인 컨트롤러 (리팩토링됨)
 * 각 기능별 클래스로 분리하여 코드 가독성과 유지보수성 향상
 */
public class PatientManagementController implements Initializable {

    // FXML 컴포넌트들
    @FXML private Button registerPatientButton;
    @FXML private Button todayPatientButton;
    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private ListView<PatientDTO> patientListView;
    @FXML private TextField nameField;
    @FXML private TextField genderField;
    @FXML private TextField birthField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private TextField addressField;
    @FXML private TextField detailAddressField;
    @FXML private Button changePatientButton;
    @FXML private Button updatePatientButton;
    @FXML private Label symptomLabel;
    @FXML private Label historyLabel;
    @FXML private Label allergyLabel;
    @FXML private Label medicationLabel;
    @FXML private Label dateLabel;
    @FXML private Label timeLabel;
    @FXML private Label departmentLabel;
    @FXML private ListView<String> historyListView;

    // 데이터
    private final ObservableList<PatientDTO> patientData;
    private final ObservableList<String> historyData;

    // 관리 클래스들
    private PatientUIManager uiManager;
    private PatientDataManager dataManager;
    private PatientEventHandler eventHandler;
    private PatientValidator validator;

    public PatientManagementController() {
        this.patientData = FXCollections.observableArrayList();
        this.historyData = FXCollections.observableArrayList();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 한글 처리를 위한 시스템 속성 설정
        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("java.awt.headless", "false");
        
        // 관리 클래스들 초기화
        initializeManagers();
        
        // UI 설정
        setupUI();
        
        // 이벤트 핸들러 설정
        setupEventHandlers();
        
        // 초기 데이터 로드
        Platform.runLater(() -> {
            loadInitialData();
        });
    }

    /**
     * 관리 클래스들 초기화
     */
    private void initializeManagers() {
        // UI 관리자 초기화
        uiManager = new PatientUIManager(
            nameField, genderField, birthField, phoneField, emailField, addressField, detailAddressField, searchField,
            patientListView, historyListView, patientData, historyData,
            symptomLabel, historyLabel, allergyLabel, medicationLabel, dateLabel, timeLabel, departmentLabel,
            updatePatientButton, changePatientButton, searchButton, registerPatientButton, todayPatientButton
        );

        // 데이터 관리자 초기화
        dataManager = new PatientDataManager();

        // 검증자 초기화
        validator = new PatientValidator();

        // 이벤트 핸들러 초기화
        eventHandler = new PatientEventHandler(uiManager, dataManager, validator, patientData, historyData);
        
        // 에러 및 정보 메시지 핸들러 설정
        eventHandler.setMessageHandlers(this::showError, this::showInfo);
    }

    /**
     * UI 초기 설정
     */
    private void setupUI() {
        // UI 컴포넌트 설정
        uiManager.setupComponents();
        
        // 한글 입력 문제 해결을 위한 TextField 설정
        uiManager.configureTextFields();
        
        // 초기 수정 모드 비활성화
        uiManager.setEditMode(false);
        
        System.out.println("UI 초기화 완료");
    }

    /**
     * 이벤트 핸들러 설정
     */
    private void setupEventHandlers() {
        // 버튼 이벤트 연결
        searchButton.setOnAction(event -> eventHandler.handleSearch());
        searchField.setOnAction(event -> eventHandler.handleSearch());
        registerPatientButton.setOnAction(event -> eventHandler.handleRegisterPatient());
        todayPatientButton.setOnAction(event -> eventHandler.handleTodayPatients());
        changePatientButton.setOnAction(event -> eventHandler.handleChangePatient());
        updatePatientButton.setOnAction(event -> eventHandler.handleUpdatePatient());
        
        // 환자 선택 이벤트 설정
        eventHandler.setupPatientSelectionHandler(patientListView);
        
        // TextField 포커스 이벤트 설정
        eventHandler.setupTextFieldHandlers();
        
        System.out.println(" 이벤트 핸들러 설정 완료");
    }

    /**
     * 초기 데이터 로드
     */
    private void loadInitialData() {
        dataManager.loadAllPatients(patientData, this::showError, 
            () -> System.out.println("초기 환자 목록 로드 완료"));
    }

    // ===============================
    // FXML 이벤트 메서드들 (기존 호환성 유지)
    // ===============================

    @FXML
    private void handleSearch() {
        eventHandler.handleSearch();
    }

    @FXML
    private void handleRegisterPatient() {
        eventHandler.handleRegisterPatient();
    }

    @FXML
    private void handleTodayPatients() {
        eventHandler.handleTodayPatients();
    }

    @FXML
    private void handleChangePatient() {
        eventHandler.handleChangePatient();
    }

    @FXML
    private void handleUpdatePatient() {
        eventHandler.handleUpdatePatient();
    }

    // ===============================
    // 유틸리티 메서드들
    // ===============================

    /**
     * 에러 메시지 표시
     */
    private void showError(String msg) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("오류");
            alert.setHeaderText("오류 발생");
            alert.setContentText(msg);
            alert.showAndWait();
        });
    }

    /**
     * 정보 메시지 표시
     */
    private void showInfo(String msg) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("정보");
            alert.setHeaderText("알림");
            alert.setContentText(msg);
            alert.showAndWait();
        });
    }

    /**
     * 환자 목록 새로고침 (외부에서 호출 가능)
     */
    public void refreshPatientList() {
        eventHandler.refreshPatientList();
    }

    /**
     * 현재 선택된 환자 정보 반환
     */
    public PatientDTO getSelectedPatient() {
        return eventHandler.getSelectedPatient();
    }

    /**
     * 수정 모드 설정 (외부에서 호출 가능)
     */
    public void setEditMode(boolean editMode) {
        uiManager.setEditMode(editMode);
    }

    /**
     * 특정 환자 선택 (외부에서 호출 가능)
     */
    public void selectPatient(String patientId) {
        eventHandler.selectPatientById(patientId);
    }

    // ===============================
    // Getter 메서드들 (필요시 사용)
    // ===============================

    public PatientUIManager getUiManager() {
        return uiManager;
    }

    public PatientDataManager getDataManager() {
        return dataManager;
    }

    public PatientEventHandler getEventHandler() {
        return eventHandler;
    }

    public PatientValidator getValidator() {
        return validator;
    }

    public ObservableList<PatientDTO> getPatientData() {
        return patientData;
    }

    public ObservableList<String> getHistoryData() {
        return historyData;
    }
}