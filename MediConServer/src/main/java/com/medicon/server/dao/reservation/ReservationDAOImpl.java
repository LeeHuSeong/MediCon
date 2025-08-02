package com.medicon.server.dao.reservation;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.medicon.server.dto.reservation.ReservationDTO;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
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
                ReservationDTO reservation = doc.toObject(ReservationDTO.class);
                reservation.setReservation_id(doc.getId()); // 문서 ID 설정
                return reservation;
            }
        } catch (Exception e) {
            System.err.println("[findReservationById] Firestore error: " + e.getMessage());
        }
        return null;
    }

    // 환자ID로 예약 목록 조회 - 핵심 수정 부분!
    @Override
    public List<ReservationDTO> findReservationByPatientId(String patientId) {
        List<ReservationDTO> reservations = new ArrayList<>();
        try {
            System.out.println("📅 예약 조회 시작 - patient_id: " + patientId);

            // 모든 users 문서에서 patients 서브컬렉션 확인
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

                    // 해당 환자의 reservations 서브컬렉션 조회
                    CollectionReference reservationsRef = db.collection("users")
                            .document(userId)
                            .collection("patients")
                            .document(patientDocId)
                            .collection("reservations");

                    ApiFuture<QuerySnapshot> reservationsFuture = reservationsRef.get();
                    List<QueryDocumentSnapshot> reservationDocs = reservationsFuture.get().getDocuments();

                    for (QueryDocumentSnapshot reservationDoc : reservationDocs) {
                        ReservationDTO reservation = reservationDoc.toObject(ReservationDTO.class);
                        reservation.setReservation_id(reservationDoc.getId());

                        // patient_id가 없다면 명시적으로 설정
                        if (reservation.getPatient_id() == null) {
                            reservation.setPatient_id(patientId);
                        }

                        reservations.add(reservation);
                    }
                }
            }

            System.out.println("✅ 예약 조회 완료 - " + reservations.size() + "개 발견");

        } catch (Exception e) {
            System.err.println("❌ 예약 조회 실패: " + e.getMessage());
            e.printStackTrace();
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
                ReservationDTO reservation = doc.toObject(ReservationDTO.class);
                reservation.setReservation_id(doc.getId()); // 문서 ID 설정
                reservations.add(reservation);
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