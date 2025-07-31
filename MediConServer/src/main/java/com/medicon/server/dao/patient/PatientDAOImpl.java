package com.medicon.server.dao.patient;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.medicon.server.dto.user.PatientDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatientDAOImpl implements PatientDAO {
    private static final String USERS_COLLECTION = "users";
    private static final String PATIENTS_COLLECTION = "patients";

    @Override
    public List<PatientDTO> findAllPatients() {
        List<PatientDTO> patients = new ArrayList<>();
        Firestore db = FirestoreClient.getFirestore();
        try {
            // 모든 유저 중 "환자"만 찾기
            ApiFuture<QuerySnapshot> userFuture = db.collection("users")
                    .whereEqualTo("role", "환자").get();
            List<QueryDocumentSnapshot> userDocs = userFuture.get().getDocuments();

            for (QueryDocumentSnapshot userDoc : userDocs) {
                String uid = userDoc.getString("uid");
                if (uid == null) continue;
                // 해당 유저의 patients 서브컬렉션 순회
                ApiFuture<QuerySnapshot> patFuture = db.collection("users").document(uid)
                        .collection("patients").get();
                List<QueryDocumentSnapshot> patDocs = patFuture.get().getDocuments();
                for (QueryDocumentSnapshot patDoc : patDocs) {
                    PatientDTO p = patDoc.toObject(PatientDTO.class);
                    // 상위 users 정보 덮어쓰기
                    p.setUid(uid);
                    p.setName(userDoc.getString("name"));
                    p.setPhone(userDoc.getString("phone"));
                    p.setEmail(userDoc.getString("email"));
                    p.setRole(userDoc.getString("role"));
                    p.setAuthority(userDoc.getLong("authority") != null ? userDoc.getLong("authority").intValue() : 0);
                    p.setCreateAt(userDoc.getLong("createAt") != null ? userDoc.getLong("createAt") : 0L);
                    patients.add(p);
                }
            }
        } catch (Exception e) {
            System.err.println("[findAllPatients] Firestore error: " + e.getMessage());
        }
        return patients;
    }

    @Override
    public List<PatientDTO> findPatientByName(String name) {
        List<PatientDTO> patients = new ArrayList<>();
        Firestore db = FirestoreClient.getFirestore();
        try {
            // 이름으로 유저 찾기
            ApiFuture<QuerySnapshot> userFuture = db.collection("users")
                    .whereEqualTo("name", name).get();
            List<QueryDocumentSnapshot> userDocs = userFuture.get().getDocuments();

            for (QueryDocumentSnapshot userDoc : userDocs) {
                String uid = userDoc.getString("uid");
                if (uid == null) continue;
                ApiFuture<QuerySnapshot> patFuture = db.collection("users").document(uid)
                        .collection("patients").get();
                List<QueryDocumentSnapshot> patDocs = patFuture.get().getDocuments();
                for (QueryDocumentSnapshot patDoc : patDocs) {
                    PatientDTO p = patDoc.toObject(PatientDTO.class);
                    // 상위 유저정보 병합
                    p.setUid(uid);
                    p.setName(userDoc.getString("name"));
                    p.setPhone(userDoc.getString("phone"));
                    p.setEmail(userDoc.getString("email"));
                    p.setRole(userDoc.getString("role"));
                    p.setAuthority(userDoc.getLong("authority") != null ? userDoc.getLong("authority").intValue() : 0);
                    p.setCreateAt(userDoc.getLong("createAt") != null ? userDoc.getLong("createAt") : 0L);
                    patients.add(p);
                }
            }
        } catch (Exception e) {
            System.err.println("[findPatientByName] Firestore error: " + e.getMessage());
        }
        return patients;
    }

    @Override
    public PatientDTO findPatientByUid(String uid) {
        Firestore db = FirestoreClient.getFirestore();
        try {
            // 상위 유저 문서 조회
            DocumentReference userRef = db.collection("users").document(uid);
            DocumentSnapshot userDoc = userRef.get().get();
            if (!userDoc.exists()) return null;

            // patients 서브컬렉션 중 첫 번째 환자(혹은 patient_id로 따로 지정 가능)만 반환
            ApiFuture<QuerySnapshot> patFuture = userRef.collection("patients").get();
            List<QueryDocumentSnapshot> patDocs = patFuture.get().getDocuments();
            if (!patDocs.isEmpty()) {
                PatientDTO p = patDocs.get(0).toObject(PatientDTO.class);
                p.setUid(uid);
                p.setName(userDoc.getString("name"));
                p.setPhone(userDoc.getString("phone"));
                p.setEmail(userDoc.getString("email"));
                p.setRole(userDoc.getString("role"));
                p.setAuthority(userDoc.getLong("authority") != null ? userDoc.getLong("authority").intValue() : 0);
                p.setCreateAt(userDoc.getLong("createAt") != null ? userDoc.getLong("createAt") : 0L);
                return p;
            }
        } catch (Exception e) {
            System.err.println("[findPatientByUid] Firestore error: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void savePatient(PatientDTO patient) {
        Firestore db = FirestoreClient.getFirestore();
        try {
            // patient_id를 문서ID로 활용 (신규 저장/수정 동일하게 동작)
            db.collection("patients")
                    .document(patient.getPatient_id())
                    .set(patient);
        } catch (Exception e) {
            System.err.println("[savePatient] Firestore error: " + e.getMessage());
        }
    }

    @Override
    public void updatePatient(PatientDTO patient) {
        Firestore db = FirestoreClient.getFirestore();
        try {
            // patient_id를 문서ID로 활용 (예: "3"이라는 문서에 덮어쓰기)
            db.collection("patients")
                    .document(patient.getPatient_id()) // ← patient_id 기준!
                    .set(patient); // set은 있으면 수정, 없으면 생성
        } catch (Exception e) {
            System.err.println("[updatePatient] Firestore error: " + e.getMessage());
        }
    }

    @Override
    public void deletePatient(String uid) {
        Firestore db = FirestoreClient.getFirestore();
        try {
            // 모든 users 문서를 조회해서 해당 uid와 일치하는 환자를 삭제
            ApiFuture<QuerySnapshot> usersFuture = db.collection(USERS_COLLECTION).get();
            List<QueryDocumentSnapshot> userDocs = usersFuture.get().getDocuments();

            for (QueryDocumentSnapshot userDoc : userDocs) {
                CollectionReference patientsRef = userDoc.getReference().collection(PATIENTS_COLLECTION);
                ApiFuture<QuerySnapshot> patientsFuture = patientsRef.whereEqualTo("uid", uid).get();
                List<QueryDocumentSnapshot> patientDocs = patientsFuture.get().getDocuments();

                for (QueryDocumentSnapshot patientDoc : patientDocs) {
                    patientDoc.getReference().delete();
                }
            }
        } catch (Exception e) {
            System.err.println("[deletePatient] Firestore error: " + e.getMessage());
        }
    }
}