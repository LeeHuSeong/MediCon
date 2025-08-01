package com.medicon.server.dao.reservation;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.medicon.server.dto.reservation.MedicalInterviewDTO;

import java.util.ArrayList;
import java.util.List;

public class MedicalInterviewDAOImpl implements MedicalInterviewDAO {

    private static final String RESERVATIONS_COLLECTION = "reservations";
    private static final String MEDICAL_INTERVIEW_COLLECTION = "medical_interview";
    private static final String INTERVIEW_DOC_ID = "main";
    private final Firestore db = FirestoreClient.getFirestore();

    // 예약ID로 문진 조회 (무조건 1개만 반환)
    @Override
    public List<MedicalInterviewDTO> findInterviewByReservationId(String reservationId) {
        List<MedicalInterviewDTO> list = new ArrayList<>();
        try {
            DocumentSnapshot doc = db.collection(RESERVATIONS_COLLECTION)
                    .document(reservationId)
                    .collection(MEDICAL_INTERVIEW_COLLECTION)
                    .document(INTERVIEW_DOC_ID)
                    .get().get();
            if (doc.exists()) {
                list.add(doc.toObject(MedicalInterviewDTO.class));
            }
        } catch (Exception e) {
            System.err.println("[findInterviewByReservationId] " + e.getMessage());
        }
        return list;
    }

    // 환자ID로 모든 문진 조회
    @Override
    public List<MedicalInterviewDTO> findInterviewByPatientId(String patientId) {
        List<MedicalInterviewDTO> results = new ArrayList<>();
        try {
            // 1. 해당 환자의 모든 예약ID 찾기
            ApiFuture<QuerySnapshot> future = db.collection(RESERVATIONS_COLLECTION)
                    .whereEqualTo("patient_id", patientId)
                    .get();
            List<QueryDocumentSnapshot> reservations = future.get().getDocuments();

            // 2. 각 예약ID에 대해 "main" 문진 하나씩 가져오기
            for (QueryDocumentSnapshot reservation : reservations) {
                String reservationId = reservation.getId();
                List<MedicalInterviewDTO> interviews = findInterviewByReservationId(reservationId);
                results.addAll(interviews);
            }
        } catch (Exception e) {
            System.err.println("[findInterviewByPatientId] " + e.getMessage());
        }
        return results;
    }

    // 문진 저장
    @Override
    public void saveInterview(String reservationId, MedicalInterviewDTO interview) {
        try {
            db.collection(RESERVATIONS_COLLECTION)
                    .document(reservationId)
                    .collection(MEDICAL_INTERVIEW_COLLECTION)
                    .document(INTERVIEW_DOC_ID)
                    .set(interview)
                    .get();
        } catch (Exception e) {
            System.err.println("[saveInterview] " + e.getMessage());
        }
    }

    // 문진 수정
    @Override
    public void updateInterview(String reservationId, MedicalInterviewDTO interview) {
        saveInterview(reservationId, interview);
    }

    // 문진 삭제
    @Override
    public void deleteInterview(String reservationId, String notUsed) {
        try {
            db.collection(RESERVATIONS_COLLECTION)
                    .document(reservationId)
                    .collection(MEDICAL_INTERVIEW_COLLECTION)
                    .document(INTERVIEW_DOC_ID)
                    .delete()
                    .get();
        } catch (Exception e) {
            System.err.println("[deleteInterview] " + e.getMessage());
        }
    }
}