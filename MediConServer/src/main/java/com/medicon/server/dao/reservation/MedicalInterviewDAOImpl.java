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
                String patientId = patientDoc.getId();

                // 2. 해당 환자의 특정 예약에서 문진 조회
                CollectionReference interviewsRef = db.collection("patients")
                        .document(patientId)
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
                        interview.setPatient_id(patientId);
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


    //모든 users 문서에서 해당 patient_id를 가진 환자 찾기
    @Override
    public List<MedicalInterviewDTO> findInterviewByPatientId(String patientId) {
        List<MedicalInterviewDTO> results = new ArrayList<>();
        try {
            System.out.println("환자별 문진 조회 시작 - patient_id: " + patientId);

            // 해당 환자의 모든 예약에서 문진 조회
            ApiFuture<QuerySnapshot> reservationsFuture = db.collection("patients")
                    .document(patientId)
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
                        interview.setPatient_id(patientId);
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

            String patientId = interview.getPatient_id();
            if (patientId != null) {
                String docId = UUID.randomUUID().toString();

                db.collection("patients")
                        .document(patientId)
                        .collection("reservations")
                        .document(reservationId)
                        .collection("medical_interviews")
                        .document(docId)
                        .set(interview)
                        .get();

                System.out.println("문진 저장 완료");
            } else {
                System.err.println("patient_id가 없습니다");
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

            String patientId = interviewDTO.getPatient_id();
            String reservationId = interviewDTO.getReservation_id();
            
            if (patientId == null || reservationId == null) {
                throw new IllegalArgumentException("patient_id 또는 reservation_id가 없습니다");
            }

            // interview_id 생성
            String interviewId = UUID.randomUUID().toString();
            interviewDTO.setInterview_id(interviewId);

            // Firestore에 저장
            db.collection("patients")
                    .document(patientId)
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

            // 해당 예약의 문진 찾기
            List<MedicalInterviewDTO> existingInterviews = findInterviewByReservationId(reservationId);

            if (!existingInterviews.isEmpty()) {
                MedicalInterviewDTO existing = existingInterviews.get(0);
                String patientId = existing.getPatient_id();
                String interviewId = existing.getInterview_id();

                // 문진 수정
                db.collection("patients")
                        .document(patientId)
                        .collection("reservations")
                        .document(reservationId)
                        .collection("medical_interviews")
                        .document(interviewId)
                        .set(interview)
                        .get();

                System.out.println("문진 수정 완료");
            } else {
                System.err.println("수정할 문진을 찾을 수 없음: reservation_id=" + reservationId);
            }
        } catch (Exception e) {
            System.err.println("문진 수정 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //예약ID로 찾아서 삭제
    @Override
    public void deleteInterview(String reservationId, String notUsed) {
        try {
            System.out.println("문진 삭제 시작 - reservation_id: " + reservationId);

            // 해당 예약의 문진 찾기
            List<MedicalInterviewDTO> existingInterviews = findInterviewByReservationId(reservationId);

            if (!existingInterviews.isEmpty()) {
                MedicalInterviewDTO existing = existingInterviews.get(0);
                String patientId = existing.getPatient_id();
                String interviewId = existing.getInterview_id();

                // 문진 삭제
                db.collection("patients")
                        .document(patientId)
                        .collection("reservations")
                        .document(reservationId)
                        .collection("medical_interviews")
                        .document(interviewId)
                        .delete()
                        .get();

                System.out.println("문진 삭제 완료");
            } else {
                System.err.println("삭제할 문진을 찾을 수 없음: reservation_id=" + reservationId);
            }
        } catch (Exception e) {
            System.err.println("문진 삭제 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }
}