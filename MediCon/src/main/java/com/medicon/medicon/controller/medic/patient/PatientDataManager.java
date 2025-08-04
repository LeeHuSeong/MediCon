package com.medicon.medicon.controller.medic.patient;

import com.medicon.medicon.model.PatientDTO;
import com.medicon.medicon.model.ReservationDTO;
import com.medicon.medicon.model.MedicalInterviewDTO;
import com.medicon.medicon.service.PatientApiService;
import com.medicon.medicon.service.ReservationApiService;
import com.medicon.medicon.service.MedicalInterviewApiService;
import javafx.application.Platform;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 환자 데이터 관리 기능을 담당하는 클래스
 */
public class PatientDataManager {

    private final PatientApiService patientApiService;
    private final ReservationApiService reservationApiService;
    private final MedicalInterviewApiService medicalInterviewApiService;

    public PatientDataManager() {
        this.patientApiService = new PatientApiService();
        this.reservationApiService = new ReservationApiService();
        this.medicalInterviewApiService = new MedicalInterviewApiService();
    }

    /**
     * 모든 환자 목록 로드
     */
    public void loadAllPatients(ObservableList<PatientDTO> patientData,
                                Consumer<String> onError,
                                Runnable onSuccess) {
        patientApiService.getAllPatientsAsync().thenAccept(patients -> {
            Platform.runLater(() -> {
                patientData.clear();
                if (patients != null) {
                    // 이름순으로 정렬
                    patients.sort((p1, p2) -> p1.getName().compareTo(p2.getName()));
                    patientData.addAll(patients);
                    System.out.println("👥 전체 환자 " + patients.size() + "명 로드 완료");
                    if (onSuccess != null) onSuccess.run();
                }
            });
        }).exceptionally(e -> {
            Platform.runLater(() -> {
                if (onError != null) onError.accept("환자 목록 로드 실패: " + e.getMessage());
            });
            return null;
        });
    }

    /**
     * 이름으로 환자 검색
     */
    public void searchPatientsByName(String searchName,
                                     ObservableList<PatientDTO> patientData,
                                     Consumer<String> onError) {
        if (searchName.trim().isEmpty()) {
            loadAllPatients(patientData, onError, null);
            return;
        }

        patientApiService.getPatientsByNameAsync(searchName).thenAccept(patients -> {
            Platform.runLater(() -> {
                patientData.clear();
                if (patients != null) {
                    patientData.addAll(patients);
                    System.out.println("🔍 검색 결과: " + patients.size() + "명");
                }
            });
        }).exceptionally(e -> {
            Platform.runLater(() -> {
                if (onError != null) onError.accept("검색 실패: " + e.getMessage());
            });
            return null;
        });
    }

    /**
     * 오늘 예약된 환자 목록 로드
     */
    public void loadTodayPatients(ObservableList<PatientDTO> patientData,
                                  Consumer<String> onError,
                                  Runnable onSuccess) {
        patientApiService.getAllPatientsAsync().thenAccept(allPatients -> {
            if (allPatients == null || allPatients.isEmpty()) {
                Platform.runLater(() -> {
                    patientData.clear();
                    if (onError != null) onError.accept("등록된 환자가 없습니다.");
                });
                return;
            }

            LocalDate today = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            List<PatientDTO> todayPatients = new ArrayList<>();
            AtomicInteger pendingRequests = new AtomicInteger(allPatients.size());

            for (PatientDTO patient : allPatients) {
                reservationApiService.getReservationsByPatientId(patient.getPatient_id())
                        .thenAccept(reservations -> {
                            boolean hasTodayReservation = false;

                            if (reservations != null && !reservations.isEmpty()) {
                                for (ReservationDTO reservation : reservations) {
                                    try {
                                        LocalDate reservationDate = LocalDate.parse(reservation.getDate(), formatter);
                                        if (reservationDate.isEqual(today)) {
                                            hasTodayReservation = true;
                                            break;
                                        }
                                    } catch (Exception e) {
                                        // 날짜 파싱 실패
                                    }
                                }
                            }

                            if (hasTodayReservation) {
                                synchronized (todayPatients) {
                                    todayPatients.add(patient);
                                }
                            }

                            if (pendingRequests.decrementAndGet() == 0) {
                                Platform.runLater(() -> {
                                    patientData.clear();
                                    if (!todayPatients.isEmpty()) {
                                        todayPatients.sort(Comparator.comparing(PatientDTO::getName));
                                        patientData.addAll(todayPatients);
                                        System.out.println("금일 환자 조회 성공: " + patientData.size() + "명");
                                        if (onSuccess != null) onSuccess.run();
                                    } else {
                                        if (onSuccess != null) onSuccess.run();
                                    }
                                });
                            }
                        })
                        .exceptionally(e -> {
                            if (pendingRequests.decrementAndGet() == 0) {
                                Platform.runLater(() -> {
                                    patientData.clear();
                                    if (onSuccess != null) onSuccess.run();
                                });
                            }
                            return null;
                        });
            }
        }).exceptionally(e -> {
            Platform.runLater(() -> {
                patientData.clear();
                if (onError != null) onError.accept("환자 목록 로드 실패: " + e.getMessage());
            });
            return null;
        });
    }

    /**
     * 환자 정보 업데이트
     */
    public CompletableFuture<Boolean> updatePatient(PatientDTO patient) {
        return patientApiService.updatePatientAsync(patient);
    }

    /**
     * 환자의 예약 정보 조회
     */
    public void loadPatientReservations(PatientDTO patient,
                                        Consumer<ReservationDTO> onReservationLoaded,
                                        Runnable onNoReservation) {
        reservationApiService.getReservationsByPatientId(patient.getPatient_id())
                .thenAccept(reservations -> {
                    Platform.runLater(() -> {
                        if (reservations != null && !reservations.isEmpty()) {
                            ReservationDTO reservation = reservations.get(0);
                            if (onReservationLoaded != null) {
                                onReservationLoaded.accept(reservation);
                            }
                        } else {
                            if (onNoReservation != null) {
                                onNoReservation.run();
                            }
                        }
                    });
                });
    }

    /**
     * 환자의 문진 정보 조회
     */
    public void loadMedicalInterview(String uid, String patientId, String reservationId,
                                     Consumer<MedicalInterviewDTO> onInterviewLoaded,
                                     Runnable onNoInterview) {
        medicalInterviewApiService.getInterviewByReservationAsync(uid, patientId, reservationId)
                .thenAccept(interviews -> {
                    Platform.runLater(() -> {
                        if (interviews != null && !interviews.isEmpty()) {
                            MedicalInterviewDTO interview = interviews.get(0);
                            if (onInterviewLoaded != null) {
                                onInterviewLoaded.accept(interview);
                            }
                        } else {
                            if (onNoInterview != null) {
                                onNoInterview.run();
                            }
                        }
                    });
                });
    }

    /**
     * 환자의 방문 이력 로드
     */
    public void loadPatientHistory(PatientDTO patient, ObservableList<String> historyData) {
        System.out.println("방문 이력 조회 시작: " + patient.getName());
        // 기존 데이터 완전히 제거
        Platform.runLater(() -> historyData.clear());
        
        reservationApiService.getReservationsByPatientId(patient.getPatient_id())
                .thenAccept(reservations -> {
                    Platform.runLater(() -> {
                        // 다시 한번 클리어 (비동기 처리 중 다른 데이터가 들어올 수 있음)
                        historyData.clear();
                        
                        if (reservations != null && !reservations.isEmpty()) {
                            System.out.println("총 예약 수: " + reservations.size());
                            LocalDate today = LocalDate.now();
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                            List<ReservationDTO> pastReservations = reservations.stream()
                                    .filter(reservation -> {
                                        try {
                                            LocalDate date = LocalDate.parse(reservation.getDate(), formatter);
                                            boolean isPastOrToday = !date.isAfter(today);
                                            System.out.println("예약일: " + reservation.getDate() + " (오늘 이전/포함: " + isPastOrToday + ")");
                                            return isPastOrToday; // 오늘 포함, 이후는 제외
                                        } catch (Exception e) {
                                            System.out.println("날짜 파싱 실패: " + reservation.getDate());
                                            return false;
                                        }
                                    })
                                    .sorted((r1, r2) -> r2.getDate().compareTo(r1.getDate()))
                                    .collect(Collectors.toList());

                            System.out.println("필터링된 과거/오늘 예약 수: " + pastReservations.size());

                            if (pastReservations.isEmpty()) {
                                historyData.add("방문 이력이 없습니다.");
                                return;
                            }

                            for (ReservationDTO reservation : pastReservations) {
                                String department = reservation.getDepartment();
                                if (department == null || department.trim().isEmpty()) {
                                    department = "일반의학과";
                                }
                                final String finalDepartment = department;
                                medicalInterviewApiService.getInterviewByReservationAsync(
                                        patient.getUid(),
                                        patient.getPatient_id(),
                                        reservation.getReservation_id()
                                ).thenAccept(interviews -> {
                                    Platform.runLater(() -> {
                                        String historyEntry;
                                        if (interviews != null && !interviews.isEmpty()) {
                                            MedicalInterviewDTO interview = interviews.get(0);
                                            historyEntry = String.format("%s - %s (%s)",
                                                    reservation.getDate(), finalDepartment,
                                                    interview.getSymptoms() != null ? interview.getSymptoms() : "진료");
                                        } else {
                                            historyEntry = String.format("%s - %s (%s)",
                                                    reservation.getDate(), finalDepartment, "진료 완료");
                                        }
                                        if (!historyData.contains(historyEntry)) {
                                            historyData.add(historyEntry);
                                            System.out.println("방문 이력 추가: " + historyEntry);
                                        }
                                    });
                                }).exceptionally(e -> {
                                    Platform.runLater(() -> {
                                        String fallbackEntry = String.format("%s - %s (%s)",
                                                reservation.getDate(), finalDepartment, "진료 완료");
                                        if (!historyData.contains(fallbackEntry)) {
                                            historyData.add(fallbackEntry);
                                            System.out.println("방문 이력 추가: " + fallbackEntry);
                                        }
                                    });
                                    return null;
                                });
                            }
                        } else {
                            historyData.add("방문 이력이 없습니다.");
                        }
                    });
                }).exceptionally(e -> {
                    Platform.runLater(() -> {
                        historyData.add("방문 이력 조회 실패: " + e.getMessage());
                    });
                    return null;
                });
    }
}