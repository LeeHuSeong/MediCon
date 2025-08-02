package com.medicon.server.dao.reservation;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.medicon.server.dto.reservation.MedicalInterviewDTO;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class MedicalInterviewDAOImpl implements MedicalInterviewDAO {

    private final Firestore db = FirestoreClient.getFirestore();

    // 예약ID로 문진 조회 - 새로운 구조 대응!
    @Override
    public List<MedicalInterviewDTO> findInterviewByReservationId(String reservationId) {
        List<MedicalInterviewDTO> list = new ArrayList<>();
        try {
            System.out.println("📝 문진 조회 시작 - reservation_id: " + reservationId);

            // 모든 users 문서에서 검색
            ApiFuture<QuerySnapshot> usersFuture = db.collection("users").get();
            List<QueryDocumentSnapshot> userDocs = usersFuture.get().getDocuments();

            for (QueryDocumentSnapshot userDoc : userDocs) {
                String userId = userDoc.getId();

                // 각 사용자의 patients 서브컬렉션 확인
                CollectionReference patientsRef = db.collection("users")
                        .document(userId)
                        .collection("patients");

                ApiFuture<QuerySnapshot> patientsFuture = patientsRef.get();
                List<QueryDocumentSnapshot> patientDocs = patientsFuture.get().getDocuments();

                for (QueryDocumentSnapshot patientDoc : patientDocs) {
                    String patientDocId = patientDoc.getId();

                    // 특정 예약 문서의 medical_interviews 서브컬렉션 확인
                    DocumentReference reservationDocRef = db.collection("users")
                            .document(userId)
                            .collection("patients")
                            .document(patientDocId)
                            .collection("reservations")
                            .document(reservationId);

                    CollectionReference interviewsRef = reservationDocRef.collection("medical_interviews");
                    ApiFuture<QuerySnapshot> interviewsFuture = interviewsRef.get();
                    List<QueryDocumentSnapshot> interviewsDocs = interviewsFuture.get().getDocuments();

                    for (QueryDocumentSnapshot interviewDoc : interviewsDocs) {
                        MedicalInterviewDTO interview = interviewDoc.toObject(MedicalInterviewDTO.class);
                        interview.setInterview_id(interviewDoc.getId());

                        // 필요한 필드 설정
                        if (interview.getReservation_id() == null) {
                            interview.setReservation_id(reservationId);
                        }

                        list.add(interview);
                    }
                }
            }

            System.out.println("✅ 문진 조회 완료 - " + list.size() + "개 발견");

        } catch (Exception e) {
            System.err.println("❌ 문진 조회 실패: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public List<MedicalInterviewDTO> findInterviewByPatientId(String patientId) {
        List<MedicalInterviewDTO> results = new ArrayList<>();
        try {
            System.out.println("📝 환자별 문진 조회 시작 - patient_id: " + patientId);

            // 모든 users 문서에서 해당 patient_id를 가진 환자 찾기
            ApiFuture<QuerySnapshot> usersFuture = db.collection("users").get();
            List<QueryDocumentSnapshot> userDocs = usersFuture.get().getDocuments();

            for (QueryDocumentSnapshot userDoc : userDocs) {
                String userId = userDoc.getId();

                // 각 사용자의 patients 서브컬렉션에서 해당 환자 찾기
                CollectionReference patientsRef = db.collection("users")
                        .document(userId)
                        .collection("patients");

                ApiFuture<QuerySnapshot> patientsFuture = patientsRef
                        .whereEqualTo("patient_id", patientId)
                        .get();
                List<QueryDocumentSnapshot> patientDocs = patientsFuture.get().getDocuments();

                for (QueryDocumentSnapshot patientDoc : patientDocs) {
                    String patientDocId = patientDoc.getId();

                    // 해당 환자의 모든 예약에서 문진 조회
                    CollectionReference reservationsRef = db.collection("users")
                            .document(userId)
                            .collection("patients")
                            .document(patientDocId)
                            .collection("reservations");

                    ApiFuture<QuerySnapshot> reservationsFuture = reservationsRef.get();
                    List<QueryDocumentSnapshot> reservationDocs = reservationsFuture.get().getDocuments();

                    for (QueryDocumentSnapshot reservationDoc : reservationDocs) {
                        String reservationId = reservationDoc.getId();

                        // 각 예약의 medical_interviews 서브컬렉션 조회
                        CollectionReference interviewsRef = reservationDoc.getReference()
                                .collection("medical_interviews");

                        ApiFuture<QuerySnapshot> interviewsFuture = interviewsRef.get();
                        List<QueryDocumentSnapshot> interviewsDocs = interviewsFuture.get().getDocuments();

                        for (QueryDocumentSnapshot interviewDoc : interviewsDocs) {
                            MedicalInterviewDTO interview = interviewDoc.toObject(MedicalInterviewDTO.class);
                            interview.setInterview_id(interviewDoc.getId());

                            // 필요한 필드 설정
                            if (interview.getPatient_id() == null) {
                                interview.setPatient_id(patientId);
                            }
                            if (interview.getReservation_id() == null) {
                                interview.setReservation_id(reservationId);
                            }

                            results.add(interview);
                        }
                    }
                }
            }

            System.out.println("✅ 환자별 문진 조회 완료 - " + results.size() + "개 발견");
        } catch (Exception e) {
            System.err.println("❌ 환자별 문진 조회 실패: " + e.getMessage());
            e.printStackTrace();
        }
        return results;
    }

    @Override
    public void saveInterview(String reservationId, MedicalInterviewDTO interview) {
        try {
            System.out.println("💾 문진 저장 시작 - reservation_id: " + reservationId);

            // 해당 patient_id를 가진 환자의 위치 찾기
            String patientId = interview.getPatient_id();
            String[] location = findPatientLocation(patientId);

            if (location != null) {
                String userId = location[0];
                String patientDocId = location[1];

                // 해당 위치의 예약에 문진 저장
                String docId = UUID.randomUUID().toString();

                db.collection("users")
                        .document(userId)
                        .collection("patients")
                        .document(patientDocId)
                        .collection("reservations")
                        .document(reservationId)
                        .collection("medical_interviews")
                        .document(docId)
                        .set(interview)
                        .get();

                System.out.println("✅ 문진 저장 완료");
            } else {
                System.err.println("❌ 환자를 찾을 수 없음: patient_id=" + patientId);
            }
        } catch (Exception e) {
            System.err.println("❌ 문진 저장 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void updateInterview(String reservationId, MedicalInterviewDTO interview) {
        try {
            System.out.println("✏️ 문진 수정 시작 - reservation_id: " + reservationId);

            // 해당 예약의 문진 찾기
            List<MedicalInterviewDTO> existingInterviews = findInterviewByReservationId(reservationId);

            if (!existingInterviews.isEmpty()) {
                // 첫 번째 문진을 수정 (보통 예약당 문진은 하나)
                MedicalInterviewDTO existing = existingInterviews.get(0);
                String patientId = existing.getPatient_id();
                String interviewId = existing.getInterview_id();

                // 환자 위치 찾기
                String[] location = findPatientLocation(patientId);

                if (location != null) {
                    String userId = location[0];
                    String patientDocId = location[1];

                    // 문진 수정
                    db.collection("users")
                            .document(userId)
                            .collection("patients")
                            .document(patientDocId)
                            .collection("reservations")
                            .document(reservationId)
                            .collection("medical_interviews")
                            .document(interviewId)
                            .set(interview)
                            .get();

                    System.out.println("✅ 문진 수정 완료");
                } else {
                    System.err.println("❌ 환자를 찾을 수 없음: patient_id=" + patientId);
                }
            } else {
                System.err.println("❌ 수정할 문진을 찾을 수 없음: reservation_id=" + reservationId);
            }
        } catch (Exception e) {
            System.err.println("❌ 문진 수정 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void deleteInterview(String reservationId, String notUsed) {
        try {
            System.out.println("🗑️ 문진 삭제 시작 - reservation_id: " + reservationId);

            // 해당 예약의 문진 찾기
            List<MedicalInterviewDTO> existingInterviews = findInterviewByReservationId(reservationId);

            if (!existingInterviews.isEmpty()) {
                // 첫 번째 문진을 삭제
                MedicalInterviewDTO existing = existingInterviews.get(0);
                String patientId = existing.getPatient_id();
                String interviewId = existing.getInterview_id();

                // 환자 위치 찾기
                String[] location = findPatientLocation(patientId);

                if (location != null) {
                    String userId = location[0];
                    String patientDocId = location[1];

                    // 문진 삭제
                    db.collection("users")
                            .document(userId)
                            .collection("patients")
                            .document(patientDocId)
                            .collection("reservations")
                            .document(reservationId)
                            .collection("medical_interviews")
                            .document(interviewId)
                            .delete()
                            .get();

                    System.out.println("✅ 문진 삭제 완료");
                } else {
                    System.err.println("❌ 환자를 찾을 수 없음: patient_id=" + patientId);
                }
            } else {
                System.err.println("❌ 삭제할 문진을 찾을 수 없음: reservation_id=" + reservationId);
            }
        } catch (Exception e) {
            System.err.println("❌ 문진 삭제 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 헬퍼 메서드: patient_id로 환자의 위치(userId, patientDocId) 찾기
    private String[] findPatientLocation(String patientId) {
        try {
            ApiFuture<QuerySnapshot> usersFuture = db.collection("users").get();
            List<QueryDocumentSnapshot> userDocs = usersFuture.get().getDocuments();

            for (QueryDocumentSnapshot userDoc : userDocs) {
                String userId = userDoc.getId();

                CollectionReference patientsRef = db.collection("users")
                        .document(userId)
                        .collection("patients");

                ApiFuture<QuerySnapshot> patientsFuture = patientsRef
                        .whereEqualTo("patient_id", patientId)
                        .get();
                List<QueryDocumentSnapshot> patientDocs = patientsFuture.get().getDocuments();

                if (!patientDocs.isEmpty()) {
                    String patientDocId = patientDocs.get(0).getId();
                    return new String[]{userId, patientDocId};
                }
            }
        } catch (Exception e) {
            System.err.println("환자 위치 찾기 실패: " + e.getMessage());
        }
        return null;
    }
}