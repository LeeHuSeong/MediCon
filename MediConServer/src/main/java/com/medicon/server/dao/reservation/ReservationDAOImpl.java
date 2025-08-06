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

    private final Firestore db = FirestoreClient.getFirestore();

    @Override
    public ReservationDTO saveReservation(ReservationDTO reservation) {
        try {
            System.out.println("예약 저장 시작 - uid: " + reservation.getPatient_uid());

            // uid를 문서 ID로 사용하여 저장
            db.collection("patients")
                    .document(reservation.getPatient_uid()) // patient_uid 사용
                    .collection("reservations")
                    .document(reservation.getReservation_id())
                    .set(reservation)
                    .get();

            System.out.println("예약 저장 완료");
            return reservation;
        } catch (Exception e) {
            System.err.println("예약 저장 실패: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("예약 저장 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Override
    public ReservationDTO findReservationById(String reservationId) {
        try {
            System.out.println("예약 ID로 조회 시작 - reservation_id: " + reservationId);

            // 모든 환자의 예약에서 해당 ID 찾기
            ApiFuture<QuerySnapshot> patientsFuture = db.collection("patients").get();
            List<QueryDocumentSnapshot> patientDocs = patientsFuture.get().getDocuments();

            for (QueryDocumentSnapshot patientDoc : patientDocs) {
                String uid = patientDoc.getId(); // 문서 ID가 uid

                DocumentReference reservationRef = db.collection("patients")
                        .document(uid)
                        .collection("reservations")
                        .document(reservationId);

                ApiFuture<DocumentSnapshot> reservationFuture = reservationRef.get();
                DocumentSnapshot reservationDoc = reservationFuture.get();

                if (reservationDoc.exists()) {
                    ReservationDTO reservation = reservationDoc.toObject(ReservationDTO.class);
                    reservation.setReservation_id(reservationDoc.getId());
                    System.out.println("예약 ID로 조회 완료 - " + reservationId);
                    return reservation;
                }
            }

            System.out.println("해당 예약을 찾을 수 없음: " + reservationId);
            return null;

        } catch (Exception e) {
            System.err.println("예약 ID로 조회 실패: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<ReservationDTO> findReservationByPatientId(String patientId) {
        List<ReservationDTO> reservations = new ArrayList<>();
        try {
            // uid를 문서 ID로 사용하여 직접 조회
            String uid = patientId; // patientId는 실제로 uid

            ApiFuture<QuerySnapshot> reservationsFuture = db.collection("patients")
                    .document(uid)
                    .collection("reservations")
                    .get();
            List<QueryDocumentSnapshot> reservationDocs = reservationsFuture.get().getDocuments();

            for (QueryDocumentSnapshot reservationDoc : reservationDocs) {
                ReservationDTO reservation = reservationDoc.toObject(ReservationDTO.class);
                reservation.setReservation_id(reservationDoc.getId());
                reservations.add(reservation);
            }

            // 결과만 간단히 로그
            if (reservations.size() > 0) {
                System.out.println("[" + uid + "] 예약 " + reservations.size() + "건 조회");
            }

        } catch (Exception e) {
            System.err.println("[" + patientId + "] 예약 조회 실패: " + e.getMessage());
        }
        return reservations;
    }

    @Override
    public List<ReservationDTO> findReservationByDate(String date) {
        List<ReservationDTO> reservations = new ArrayList<>();
        try {
            System.out.println("날짜별 예약 조회 시작 - date: " + date);

            // 모든 환자의 예약에서 해당 날짜 찾기
            ApiFuture<QuerySnapshot> patientsFuture = db.collection("patients").get();
            List<QueryDocumentSnapshot> patientDocs = patientsFuture.get().getDocuments();

            for (QueryDocumentSnapshot patientDoc : patientDocs) {
                String uid = patientDoc.getId(); // 문서 ID가 uid

                ApiFuture<QuerySnapshot> reservationsFuture = db.collection("patients")
                        .document(uid)
                        .collection("reservations")
                        .whereEqualTo("date", date)
                        .get();
                List<QueryDocumentSnapshot> reservationDocs = reservationsFuture.get().getDocuments();

                for (QueryDocumentSnapshot reservationDoc : reservationDocs) {
                    ReservationDTO reservation = reservationDoc.toObject(ReservationDTO.class);
                    reservation.setReservation_id(reservationDoc.getId());
                    reservations.add(reservation);
                }
            }

            System.out.println("날짜별 예약 조회 완료 - " + reservations.size() + "건");

        } catch (Exception e) {
            System.err.println("날짜별 예약 조회 실패: " + e.getMessage());
            e.printStackTrace();
        }
        return reservations;
    }

    @Override
    public void updateReservation(ReservationDTO reservation) {
        try {
            System.out.println("예약 수정 시작 - reservation_id: " + reservation.getReservation_id());

            db.collection("patients")
                    .document(reservation.getPatient_uid()) // patient_uid 사용
                    .collection("reservations")
                    .document(reservation.getReservation_id())
                    .set(reservation)
                    .get();

            System.out.println("예약 수정 완료");

        } catch (Exception e) {
            System.err.println("예약 수정 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void deleteReservation(String reservationId) {
        try {
            System.out.println("예약 삭제 시작 - reservation_id: " + reservationId);

            // 모든 환자의 예약에서 해당 ID 찾아서 삭제
            ApiFuture<QuerySnapshot> patientsFuture = db.collection("patients").get();
            List<QueryDocumentSnapshot> patientDocs = patientsFuture.get().getDocuments();

            for (QueryDocumentSnapshot patientDoc : patientDocs) {
                String uid = patientDoc.getId(); // 문서 ID가 uid

                DocumentReference reservationRef = db.collection("patients")
                        .document(uid)
                        .collection("reservations")
                        .document(reservationId);

                ApiFuture<DocumentSnapshot> reservationFuture = reservationRef.get();
                DocumentSnapshot reservationDoc = reservationFuture.get();

                if (reservationDoc.exists()) {
                    reservationRef.delete().get();
                    System.out.println("예약 삭제 완료 - reservation_id: " + reservationId);
                    return;
                }
            }

            System.out.println("삭제할 예약을 찾을 수 없음: " + reservationId);

        } catch (Exception e) {
            System.err.println("예약 삭제 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
