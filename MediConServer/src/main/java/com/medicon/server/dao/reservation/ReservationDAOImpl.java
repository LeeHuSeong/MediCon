package com.medicon.server.dao.reservation;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.medicon.server.dto.reservation.ReservationDTO;

import java.util.ArrayList;
import java.util.List;

public class ReservationDAOImpl implements ReservationDAO {

    private static final String RESERVATIONS_COLLECTION = "reservations";
    private final Firestore db;

    public ReservationDAOImpl() {
        this.db = FirestoreClient.getFirestore();
    }

    // 예약 등록
    @Override
    public void saveReservation(ReservationDTO reservation) {
        try {
            db.collection(RESERVATIONS_COLLECTION)
              .document(reservation.getReservation_id())
              .set(reservation)
              .get(); // 동기화 처리
        } catch (Exception e) {
            System.err.println("[saveReservation] Firestore error: " + e.getMessage());
        }
    }

    // 예약 ID로 단건 조회
    @Override
    public ReservationDTO findReservationById(String reservationId) {
        try {
            DocumentReference docRef = db.collection(RESERVATIONS_COLLECTION).document(reservationId);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot doc = future.get();
            if (doc.exists()) {
                return doc.toObject(ReservationDTO.class);
            }
        } catch (Exception e) {
            System.err.println("[findReservationById] Firestore error: " + e.getMessage());
        }
        return null;
    }

    // 환자ID로 예약 목록 조회
    @Override
    public List<ReservationDTO> findReservationByPatientId(String patientId) {
        List<ReservationDTO> reservations = new ArrayList<>();
        try {
            ApiFuture<QuerySnapshot> future = db.collection(RESERVATIONS_COLLECTION)
                                                .whereEqualTo("patientId", patientId)
                                                .get();
            List<QueryDocumentSnapshot> docs = future.get().getDocuments();
            for (QueryDocumentSnapshot doc : docs) {
                reservations.add(doc.toObject(ReservationDTO.class));
            }
        } catch (Exception e) {
            System.err.println("[findReservationByPatientId] Firestore error: " + e.getMessage());
        }
        return reservations;
    }

    // 날짜로 예약 목록 조회
    @Override
    public List<ReservationDTO> findReservationByDate(String date) {
        List<ReservationDTO> reservations = new ArrayList<>();
        try {
            ApiFuture<QuerySnapshot> future = db.collection(RESERVATIONS_COLLECTION)
                                                .whereEqualTo("date", date)
                                                .get();
            List<QueryDocumentSnapshot> docs = future.get().getDocuments();
            for (QueryDocumentSnapshot doc : docs) {
                reservations.add(doc.toObject(ReservationDTO.class));
            }
        } catch (Exception e) {
            System.err.println("[findReservationByDate] Firestore error: " + e.getMessage());
        }
        return reservations;
    }

    // 예약 수정 (Firestore의 set은 upsert임)
    @Override
    public void updateReservation(ReservationDTO reservation) {
        saveReservation(reservation);
    }

    // 예약 삭제
    @Override
    public void deleteReservation(String reservationId) {
        try {
            db.collection(RESERVATIONS_COLLECTION)
              .document(reservationId)
              .delete()
              .get();
        } catch (Exception e) {
            System.err.println("[deleteReservation] Firestore error: " + e.getMessage());
        }
    }
}