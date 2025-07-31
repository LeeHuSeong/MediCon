package com.medicon.server.service;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.medicon.server.dto.signup.DoctorSignupRequest;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserDataService {

    public void createUserDocument(String uid, String email) {
        Firestore db = FirestoreClient.getFirestore();

        Map<String, Object> data = new HashMap<>();
        data.put("uid", uid);
        data.put("email", email);
        data.put("createdAt", System.currentTimeMillis());

        db.collection("users").document(uid).set(data);
    }

    public void saveDoctorDetail(String uid, DoctorSignupRequest request) throws Exception {
        Firestore db = FirestoreClient.getFirestore();
        Map<String, Object> doctorData = Map.of(
                "name", request.getName(),
                "phone", request.getPhone(),
                "rank", request.getRank(),
                "uid", uid,
                "department", request.getDepartment()  // 선택적으로 추가
        );
        db.collection("users").document(uid).collection("doctors").document("info").set(doctorData);
        try {
            db.collection("users")
                    .document(uid)
                    .collection("doctors")
                    .document("info")
                    .set(doctorData)
                    .get();  // Blocking call to catch exceptions
            System.out.println("Firestore 저장 성공");
        } catch (Exception e) {
            System.err.println("Firestore 저장 실패");
            e.printStackTrace(); // 콘솔에 전체 스택 출력
            throw e;
        }
    }
}
