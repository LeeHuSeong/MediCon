package com.medicon.server.dao.patient;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.medicon.server.dto.user.PatientDTO;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class PatientDAOImpl implements PatientDAO {

    private final Firestore db = FirestoreClient.getFirestore();

    @Override
    public List<PatientDTO> findAllPatients() {
        List<PatientDTO> patients = new ArrayList<>();
        try {
            System.out.println("전체 환자 조회 시작");

            // uid를 문서 ID로 사용하는 새로운 구조
            ApiFuture<QuerySnapshot> future = db.collection("patients").get();
            List<QueryDocumentSnapshot> docs = future.get().getDocuments();

            for (QueryDocumentSnapshot doc : docs) {
                PatientDTO patient = doc.toObject(PatientDTO.class);
                // 문서 ID를 uid로 설정
                patient.setUid(doc.getId());
                patients.add(patient);
            }

            System.out.println("전체 환자 조회 완료 - " + patients.size() + "명");

        } catch (Exception e) {
            System.err.println("전체 환자 조회 실패: " + e.getMessage());
            e.printStackTrace();
        }
        return patients;
    }

    @Override
    public List<PatientDTO> findPatientByName(String name) {
        List<PatientDTO> patients = new ArrayList<>();
        try {
            System.out.println("환자 이름 검색 시작 - name: " + name);

            // 새로운 구조: patients 컬렉션에서 이름으로 검색
            ApiFuture<QuerySnapshot> future = db.collection("patients")
                    .whereEqualTo("name", name)
                    .get();
            List<QueryDocumentSnapshot> docs = future.get().getDocuments();

            for (QueryDocumentSnapshot doc : docs) {
                PatientDTO patient = doc.toObject(PatientDTO.class);
                if (patient.getPatient_id() == null) {
                    patient.setPatient_id(doc.getId());
                }
                patients.add(patient);
            }

            System.out.println("환자 이름 검색 완료 - " + patients.size() + "명");

        } catch (Exception e) {
            System.err.println("환자 이름 검색 실패: " + e.getMessage());
            e.printStackTrace();
        }
        return patients;
    }

    @Override
    public PatientDTO findPatientByUid(String uid) {
        try {
            System.out.println("환자 단건 조회 시작 - uid: " + uid);

            // uid를 문서 ID로 사용하여 직접 조회
            DocumentSnapshot doc = db.collection("patients")
                    .document(uid)
                    .get()
                    .get();

            if (doc.exists()) {
                PatientDTO patient = doc.toObject(PatientDTO.class);
                patient.setUid(doc.getId());
                System.out.println("환자 단건 조회 완료 - " + patient.getName());
                return patient;
            } else {
                System.out.println("환자를 찾을 수 없음: " + uid);
                return null;
            }

        } catch (Exception e) {
            System.err.println("환자 단건 조회 실패: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void savePatient(PatientDTO patient) {
        try {
            System.out.println("환자 저장 시작 - " + patient.getName());

            // uid를 문서 ID로 사용하여 저장
            String uid = patient.getUid();
            if (uid == null || uid.trim().isEmpty()) {
                throw new IllegalArgumentException("uid가 없습니다");
            }

            db.collection("patients")
                    .document(uid)
                    .set(patient)
                    .get();

            System.out.println("환자 저장 완료 - " + patient.getName());

        } catch (Exception e) {
            System.err.println("환자 저장 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void updatePatient(PatientDTO patient) {
        try {
            System.out.println("✏환자 정보 수정 시작 - " + patient.getName());

            // uid를 문서 ID로 사용하여 업데이트
            String uid = patient.getUid();
            if (uid != null && !uid.trim().isEmpty()) {
                db.collection("patients")
                        .document(uid)
                        .set(patient)
                        .get();

                System.out.println("환자 정보 수정 완료 - " + patient.getName());
            } else {
                System.err.println("uid가 없어서 수정할 수 없음");
            }

        } catch (Exception e) {
            System.err.println("환자 정보 수정 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void deletePatient(String uid) {
        try {
            System.out.println("환자 삭제 시작 - uid: " + uid);

            // uid를 문서 ID로 사용하여 직접 삭제
            db.collection("patients")
                    .document(uid)
                    .delete()
                    .get();
            
            System.out.println("환자 삭제 완료 - uid: " + uid);

        } catch (Exception e) {
            System.err.println("환자 삭제 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // patient_id로 환자 조회 (예약/문진에서 사용)
    public PatientDTO findPatientByPatientId(String patientId) {
        try {
            System.out.println("patient_id로 환자 조회 - patient_id: " + patientId);

            // patient_id 필드로 검색
            ApiFuture<QuerySnapshot> future = db.collection("patients")
                    .whereEqualTo("patient_id", patientId)
                    .get();
            List<QueryDocumentSnapshot> docs = future.get().getDocuments();

            if (!docs.isEmpty()) {
                PatientDTO patient = docs.get(0).toObject(PatientDTO.class);
                System.out.println("patient_id로 환자 조회 완료 - " + patient.getName());
                return patient;
            } else {
                System.out.println("해당 patient_id의 환자를 찾을 수 없음: " + patientId);
                return null;
            }

        } catch (Exception e) {
            System.err.println("patient_id로 환자 조회 실패: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}