package com.medicon.medicon.controller.patient;

import com.medicon.medicon.model.PatientDTO;
import com.medicon.medicon.controller.patient.PatientUIManager;
import com.medicon.medicon.controller.patient.PatientDataManager;
import com.medicon.medicon.controller.patient.PatientValidator;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.collections.ObservableList;

import java.util.function.Consumer;

/**
 * 환자 관리 이벤트 처리를 담당하는 클래스
 */
public class PatientEventHandler {
    
    private final PatientUIManager uiManager;
    private final PatientDataManager dataManager;
    private final PatientValidator validator;
    private final ObservableList<PatientDTO> patientData;
    private final ObservableList<String> historyData;
    
    private PatientDTO selectedPatient;
    private boolean isUpdatingSelection = false;
    
    private Consumer<String> errorHandler;
    private Consumer<String> infoHandler;

    public PatientEventHandler(PatientUIManager uiManager, 
                             PatientDataManager dataManager,
                             PatientValidator validator,
                             ObservableList<PatientDTO> patientData,
                             ObservableList<String> historyData) {
        this.uiManager = uiManager;
        this.dataManager = dataManager;
        this.validator = validator;
        this.patientData = patientData;
        this.historyData = historyData;
    }

    /**
     * 에러 및 정보 메시지 핸들러 설정
     */
    public void setMessageHandlers(Consumer<String> errorHandler, Consumer<String> infoHandler) {
        this.errorHandler = errorHandler;
        this.infoHandler = infoHandler;
    }

    /**
     * 환자 선택 이벤트 설정
     */
    public void setupPatientSelectionHandler(ListView<PatientDTO> patientListView) {
        patientListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (isUpdatingSelection) {
                System.out.println("⏸️ 선택 이벤트 스킵 (업데이트 중)");
                return;
            }

            // 수정 모드 중이면 경고
            if (uiManager.isEditMode() && oldSel != null && newSel != oldSel) {
                Alert warningAlert = new Alert(Alert.AlertType.WARNING);
                warningAlert.setTitle("수정 모드 활성화됨");
                warningAlert.setHeaderText("현재 환자 정보 수정 중입니다");
                warningAlert.setContentText("다른 환자를 선택하려면 먼저 수정을 완료하거나 취소해주세요.");
                warningAlert.showAndWait();

                // 선택을 원래대로 되돌림
                isUpdatingSelection = true;
                Platform.runLater(() -> {
                    patientListView.getSelectionModel().select(oldSel);
                    Platform.runLater(() -> isUpdatingSelection = false);
                });
                return;
            }

            if (newSel != null) {
                selectedPatient = newSel;
                handlePatientSelected(newSel);
                System.out.println("👤 환자 선택: " + newSel.getName());
            } else {
                selectedPatient = null;
                uiManager.clearAllFields();
            }
        });
    }

    /**
     * TextField 포커스 이벤트 설정
     */
    public void setupTextFieldHandlers() {
        // 검색 필드 포커스 이벤트
        uiManager.getSearchField().focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused && !uiManager.getSearchField().getText().trim().isEmpty()) {
                // 포커스를 잃었을 때 자동 검색 (선택사항)
                // handleSearch(); // 원하지 않으면 주석 처리
            }
        });
        
        // 수정 모드의 텍스트 필드들은 포커스 잃을 때만 검증
        uiManager.getNameField().focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused && uiManager.isEditMode()) {
                uiManager.validateNameField();
            }
        });
        
        uiManager.getPhoneField().focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused && uiManager.isEditMode()) {
                uiManager.validatePhoneField();
            }
        });
        
        uiManager.getEmailField().focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused && uiManager.isEditMode()) {
                uiManager.validateEmailField();
            }
        });
    }

    /**
     * 환자 선택 처리
     */
    private void handlePatientSelected(PatientDTO patient) {
        uiManager.displayPatientInfo(patient);
        dataManager.loadPatientHistory(patient, historyData);
        
        // 예약 정보 로드
        dataManager.loadPatientReservations(patient, 
            reservation -> {
                // 예약 정보 표시
                String department = reservation.getDepartment();
                if (department == null || department.trim().isEmpty()) {
                    department = "일반의학과";
                }
                uiManager.displayReservationInfo(reservation.getDate(), reservation.getTime(), department);
                
                // 문진 정보 로드
                dataManager.loadMedicalInterview(
                    patient.getUid(),
                    patient.getPatient_id(),
                    reservation.getReservation_id(),
                    interview -> {
                        uiManager.displayMedicalInfo(
                            interview.getSymptoms(),
                            interview.getPast_medical_history(),
                            interview.getAllergy(),
                            interview.getCurrent_medication()
                        );
                    },
                    () -> uiManager.clearMedicalInfo()
                );
            },
            () -> {
                uiManager.displayReservationInfo("-", "-", "-");
                uiManager.clearMedicalInfo();
            }
        );
    }

    /**
     * 검색 처리
     */
    public void handleSearch() {
        String searchName = uiManager.getSearchField().getText().trim();
        dataManager.searchPatientsByName(searchName, patientData, errorHandler);
    }

    /**
     * 오늘 환자 목록 로드
     */
    public void handleTodayPatients() {
        dataManager.loadTodayPatients(patientData, errorHandler, 
            () -> {
                if (infoHandler != null) {
                    infoHandler.accept("금일 예약 환자 목록을 로드했습니다.");
                }
            });
    }

    /**
     * 환자 정보 수정 모드 진입
     */
    public void handleChangePatient() {
        if (selectedPatient == null) {
            if (errorHandler != null) {
                errorHandler.accept("수정할 환자를 선택해주세요.");
            }
            return;
        }

        // 수정 모드 진입 시 확인 대화상자
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("환자 정보 수정");
        confirmAlert.setHeaderText("환자 정보 수정 모드");
        confirmAlert.setContentText(selectedPatient.getName() + " 환자의 정보를 수정하시겠습니까?");

        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            uiManager.setEditMode(true);
            if (infoHandler != null) {
                infoHandler.accept("환자 정보를 수정할 수 있습니다.\n수정 완료 후 '수정 완료' 버튼을 클릭하세요.");
            }
        }
    }

    /**
     * 환자 정보 업데이트 처리 (한글 입력 문제 해결)
     */
    public void handleUpdatePatient() {
        System.out.println("📝 환자 정보 수정 요청: " + (selectedPatient != null ? selectedPatient.getName() : "null") + 
                          " (ID: " + (selectedPatient != null ? selectedPatient.getPatient_id() : "null") + ")");
        
        // 한글 조합 완료를 위한 짧은 지연
        PauseTransition pause = new PauseTransition(Duration.millis(100));
        pause.setOnFinished(e -> {
            Platform.runLater(() -> doActualUpdate());
        });
        pause.play();
    }

    /**
     * 실제 업데이트 로직 실행
     */
    private void doActualUpdate() {
        try {
            System.out.println("✏환자 정보 수정 시작 - " + (selectedPatient != null ? selectedPatient.getName() : "null"));

            if (selectedPatient == null) {
                if (errorHandler != null) {
                    errorHandler.accept("수정할 환자를 선택해주세요.");
                }
                return;
            }

            // 필드 값 가져오기
            String name = uiManager.getNameField().getText().trim();
            String gender = uiManager.getGenderField().getText().trim();
            String phone = uiManager.getPhoneField().getText().trim();
            String email = uiManager.getEmailField().getText().trim();
            String address = uiManager.getAddressField().getText().trim();
            String rnn = uiManager.getBirthField().getText().trim();

            System.out.println("📋 기존 정보: " + selectedPatient.getName());
            System.out.println("📝 새 정보: " + name);

            // 입력 검증
            String validationError = validator.validatePatientInfo(name, gender, phone, email, address);
            if (validationError != null) {
                if (errorHandler != null) {
                    errorHandler.accept(validationError);
                }
                return;
            }

            // 변경사항 확인
            String changes = validator.getChanges(selectedPatient, name, gender, phone, email, address);
            if (changes == null) {
                Alert noChangeAlert = new Alert(Alert.AlertType.INFORMATION);
                noChangeAlert.setTitle("변경사항 없음");
                noChangeAlert.setHeaderText("수정할 내용이 없습니다");
                noChangeAlert.setContentText("변경된 정보가 없습니다.\n수정 모드를 종료하시겠습니까?");

                if (noChangeAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                    uiManager.setEditMode(false);
                }
                return;
            }

            // 변경사항 확인 대화상자
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("환자 정보 수정 확인");
            confirmAlert.setHeaderText("다음 정보를 수정하시겠습니까?");
            confirmAlert.setContentText(changes);

            if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
                return;
            }

            // 로딩 상태 설정
            uiManager.setUpdateButtonLoading(true);

            // 원본 정보 백업
            String originalName = selectedPatient.getName();
            String originalGender = selectedPatient.getGender();
            String originalPhone = selectedPatient.getPhone();
            String originalEmail = selectedPatient.getEmail();
            String originalAddress = selectedPatient.getAddress();

            // 환자 정보 업데이트
            selectedPatient.setName(name);
            selectedPatient.setGender(gender);
            selectedPatient.setPhone(phone);
            selectedPatient.setEmail(email.isEmpty() ? null : email);
            selectedPatient.setAddress(address.isEmpty() ? null : address);

            // 주민번호 업데이트
            if (!rnn.isEmpty() && rnn.length() == 6) {
                selectedPatient.setRnn(rnn + selectedPatient.getRnn().substring(6));
            }

            // 서버에 업데이트 요청
            dataManager.updatePatient(selectedPatient).thenAccept(success -> {
                Platform.runLater(() -> {
                    uiManager.setUpdateButtonLoading(false);

                    if (success) {
                        System.out.println("✅ 환자 정보 수정 완료: " + name);

                        if (infoHandler != null) {
                            infoHandler.accept("✅ 환자 정보가 성공적으로 수정되었습니다.\n\n" +
                                    "수정된 환자: " + name + "\n" +
                                    "수정 시간: " + java.time.LocalDateTime.now().format(
                                    java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                        }

                        uiManager.setEditMode(false);

                        // 개별 환자 정보만 업데이트 (중복 방지)
                        updatePatientInList(selectedPatient);

                        // UI 새로고침
                        Platform.runLater(() -> {
                            uiManager.displayPatientInfo(selectedPatient);
                        });

                    } else {
                        if (errorHandler != null) {
                            errorHandler.accept("❌ 환자 정보 수정에 실패했습니다.\n다시 시도해주세요.");
                        }

                        // 실패 시 원래 정보로 복원
                        restorePatientInfo(originalName, originalGender, originalPhone, originalEmail, originalAddress);
                    }
                });
            }).exceptionally(e -> {
                Platform.runLater(() -> {
                    uiManager.setUpdateButtonLoading(false);

                    if (errorHandler != null) {
                        errorHandler.accept("❌ 환자 정보 수정 중 오류가 발생했습니다.\n\n오류 내용: " + e.getMessage());
                    }

                    // 실패 시 원래 정보로 복원
                    restorePatientInfo(originalName, originalGender, originalPhone, originalEmail, originalAddress);
                });
                return null;
            });

        } catch (Exception e) {
            System.err.println("❌ 환자 정보 수정 중 예외 발생: " + e.getMessage());
            e.printStackTrace();

            Platform.runLater(() -> {
                uiManager.setUpdateButtonLoading(false);
                if (errorHandler != null) {
                    errorHandler.accept("❌ 환자 정보 수정 중 오류가 발생했습니다.\n\n오류 내용: " + e.getMessage());
                }
            });
        }
    }

    private void updatePatientInList(PatientDTO updatedPatient) {
        try {
            for (int i = 0; i < patientData.size(); i++) {
                PatientDTO patient = patientData.get(i);
                if (patient.getPatient_id().equals(updatedPatient.getPatient_id())) {
                    // 기존 환자 정보를 업데이트된 정보로 교체
                    patientData.set(i, updatedPatient);
                    System.out.println("🔄 목록에서 환자 정보 업데이트: " + updatedPatient.getName());
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("❌ 환자 목록 업데이트 실패: " + e.getMessage());
        }
    }
    /**
     * 환자 정보 복원
     */
    private void restorePatientInfo(String name, String gender, String phone, String email, String address) {
        selectedPatient.setName(name);
        selectedPatient.setGender(gender);
        selectedPatient.setPhone(phone);
        selectedPatient.setEmail(email);
        selectedPatient.setAddress(address);
        uiManager.displayPatientInfo(selectedPatient);
    }

    /**
     * 신규 환자 등록 처리
     */
    public void handleRegisterPatient() {
        if (infoHandler != null) {
            infoHandler.accept("신규 환자 등록 기능은 추후 구현 예정입니다.");
        }
    }

    /**
     * 환자 목록 새로고침
     */
    public void refreshPatientList() {
        String currentPatientId = selectedPatient != null ? selectedPatient.getPatient_id() : null;
        
        dataManager.loadAllPatients(patientData, errorHandler, () -> {
            if (currentPatientId != null) {
                uiManager.selectPatientById(currentPatientId);
            }
        });
    }

    /**
     * 환자 재선택 (목록 업데이트 후)
     */
    public void selectPatientById(String patientId) {
        if (patientId == null) return;
        
        try {
            System.out.println("🔍 환자 재선택 시도: " + patientId);

            if (isUpdatingSelection) {
                Platform.runLater(() -> {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    selectPatientById(patientId);
                });
                return;
            }

            isUpdatingSelection = true;

            boolean found = false;
            for (PatientDTO patient : patientData) {
                if (patient.getPatient_id().equals(patientId)) {
                    selectedPatient = patient;
                    System.out.println("🔄 환자 재선택 성공: " + patient.getName());
                    found = true;
                    break;
                }
            }

            if (!found) {
                System.err.println("❌ 환자를 찾을 수 없음: " + patientId);
            }

        } catch (Exception e) {
            System.err.println("❌ 환자 재선택 실패: " + e.getMessage());
            e.printStackTrace();
        } finally {
            Platform.runLater(() -> {
                isUpdatingSelection = false;
            });
        }
    }

    // Getter/Setter
    public PatientDTO getSelectedPatient() {
        return selectedPatient;
    }

    public void setSelectedPatient(PatientDTO patient) {
        this.selectedPatient = patient;
    }
}