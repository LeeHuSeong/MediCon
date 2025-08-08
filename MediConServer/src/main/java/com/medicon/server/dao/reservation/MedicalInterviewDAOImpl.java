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

    //예약ID로 문진 조회
    @Override
    public List<MedicalInterviewDTO> findInterviewByReservationId(String reservationId) {
        List<MedicalInterviewDTO> list = new ArrayList<>();
        try {
            System.out.println("문진 조회 시작 - reservation_id: " + reservationId);

            // 1. 먼저 해당 예약이 어느 환자 것인지 찾기
            ApiFuture<QuerySnapshot> patientsFuture = db.collection("patients").get();
            List<QueryDocumentSnapshot> patientDocs = patientsFuture.get().getDocuments();

            for (QueryDocumentSnapshot patientDoc : patientDocs) {
                String uid = patientDoc.getId(); // 문서 ID가 uid

                // 2. 해당 환자의 특정 예약에서 문진 조회
                CollectionReference interviewsRef = db.collection("patients")
                        .document(uid)
                        .collection("reservations")
                        .document(reservationId)
                        .collection("medical_interviews");

                ApiFuture<QuerySnapshot> interviewsFuture = interviewsRef.get();
                List<QueryDocumentSnapshot> interviewsDocs = interviewsFuture.get().getDocuments();

                for (QueryDocumentSnapshot interviewDoc : interviewsDocs) {
                    MedicalInterviewDTO interview = interviewDoc.toObject(MedicalInterviewDTO.class);
                    interview.setInterview_id(interviewDoc.getId());

                    // 필요한 필드 설정
                    if (interview.getReservation_id() == null) {
                        interview.setReservation_id(reservationId);
                    }
                    if (interview.getPatient_id() == null) {
                        interview.setPatient_id(uid); // uid를 patient_id로 설정
                    }

                    list.add(interview);
                }
            }

            System.out.println("문진 조회 완료 - " + list.size() + "개 발견");

        } catch (Exception e) {
            System.err.println("문진 조회 실패: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    //uid로 환자별 문진 조회
    @Override
    public List<MedicalInterviewDTO> findInterviewByPatientId(String patientId) {
        List<MedicalInterviewDTO> results = new ArrayList<>();
        try {
            System.out.println("환자별 문진 조회 시작 - uid: " + patientId);

            // uid를 문서 ID로 사용하여 직접 조회
            String uid = patientId; // patientId는 실제로 uid

            ApiFuture<QuerySnapshot> reservationsFuture = db.collection("patients")
                    .document(uid)
                    .collection("reservations")
                    .get();
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
                        interview.setPatient_id(uid); // uid를 patient_id로 설정
                    }
                    if (interview.getReservation_id() == null) {
                        interview.setReservation_id(reservationId);
                    }

                    results.add(interview);
                }
            }

            System.out.println("환자별 문진 조회 완료 - " + results.size() + "개 발견");
        } catch (Exception e) {
            System.err.println("환자별 문진 조회 실패: " + e.getMessage());
            e.printStackTrace();
        }
        return results;
    }

    @Override
    public void saveInterview(String reservationId, MedicalInterviewDTO interview) {
        try {
            //로그체크
            System.out.println("문진 저장 시작 - reservation_id: " + reservationId);

            String uid = interview.getPatient_id(); // patient_id는 실제로 uid
            if (uid != null) {
                String docId = UUID.randomUUID().toString();

                db.collection("patients")
                        .document(uid)
                        .collection("reservations")
                        .document(reservationId)
                        .collection("medical_interviews")
                        .document(docId)
                        .set(interview)
                        .get();

                System.out.println("문진 저장 완료");
            } else {
                System.err.println("uid가 없습니다");
            }
        } catch (Exception e) {
            System.err.println("문진 저장 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public MedicalInterviewDTO saveInterview(MedicalInterviewDTO interviewDTO) {
        try {
            System.out.println("문진 저장 시작 - DTO 방식");

            String uid = interviewDTO.getPatient_id(); // patient_id 필드에 uid가 저장됨
            String reservationId = interviewDTO.getReservation_id();
            
            if (uid == null || reservationId == null) {
                throw new IllegalArgumentException("uid 또는 reservation_id가 없습니다");
            }

            // interview_id 생성
            String interviewId = UUID.randomUUID().toString();
            interviewDTO.setInterview_id(interviewId);

            // Firestore에 저장
            db.collection("patients")
                    .document(uid)
                    .collection("reservations")
                    .document(reservationId)
                    .collection("medical_interviews")
                    .document(interviewId)
                    .set(interviewDTO)
                    .get();

            System.out.println("문진 저장 완료 - interview_id: " + interviewId);
            return interviewDTO;
            
        } catch (Exception e) {
            System.err.println("문진 저장 실패: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("문진 저장 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Override
    public void updateInterview(String reservationId, MedicalInterviewDTO interview) {
        try {
            System.out.println("문진 수정 시작 - reservation_id: " + reservationId);

            String uid = interview.getPatient_id(); // patient_id는 실제로 uid
            if (uid != null) {
                db.collection("patients")
                        .document(uid)
                        .collection("reservations")
                        .document(reservationId)
                        .collection("medical_interviews")
                        .document(interview.getInterview_id())
                        .set(interview)
                        .get();

                System.out.println("문진 수정 완료");
            } else {
                System.err.println("uid가 없어서 수정할 수 없음");
            }
        } catch (Exception e) {
            System.err.println("문진 수정 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void deleteInterview(String reservationId, String interviewId) {
        try {
            System.out.println("문진 삭제 시작 - interview_id: " + interviewId);

            // 모든 환자의 예약에서 해당 문진 찾아서 삭제
            ApiFuture<QuerySnapshot> patientsFuture = db.collection("patients").get();
            List<QueryDocumentSnapshot> patientDocs = patientsFuture.get().getDocuments();

            for (QueryDocumentSnapshot patientDoc : patientDocs) {
                String uid = patientDoc.getId(); // 문서 ID가 uid

                DocumentReference interviewRef = db.collection("patients")
                        .document(uid)
                        .collection("reservations")
                        .document(reservationId)
                        .collection("medical_interviews")
                        .document(interviewId);

                ApiFuture<DocumentSnapshot> interviewFuture = interviewRef.get();
                DocumentSnapshot interviewDoc = interviewFuture.get();

                if (interviewDoc.exists()) {
                    interviewRef.delete().get();
                    System.out.println("문진 삭제 완료 - interview_id: " + interviewId);
                    return;
                }
            }

            System.out.println("삭제할 문진을 찾을 수 없음: " + interviewId);

        } catch (Exception e) {
            System.err.println("문진 삭제 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }
}