package com.medicon.server.service;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.medicon.server.dto.signup.NurseSignupRequest;
import com.medicon.server.dto.signup.SignupResponse;
import com.medicon.server.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class NurseService {

    @Autowired private JwtUtil jwtUtil;
    @Autowired private StaffService staffService;

    public SignupResponse registerNurse(String uid, NurseSignupRequest req) {
        try {
            Firestore db = FirestoreClient.getFirestore();

            // 상위 문서: users/{uid}
            Map<String, Object> userFields = new HashMap<>();
            userFields.put("uid", uid);
            userFields.put("email", req.getEmail());
            userFields.put("name", req.getName());
            userFields.put("phone", req.getPhone());
            userFields.put("role", "nurse");
            userFields.put("authority", 1);
            userFields.put("createdAt", System.currentTimeMillis());

            db.collection("users").document(uid).set(userFields).get();

            // 하위 컬렉션: users/{uid}/nurses/info
            Map<String, Object> nurseInfo = new HashMap<>();
            nurseInfo.put("uid", uid);
            nurseInfo.put("rank", req.getRank());
            nurseInfo.put("department", req.getDepartment()); // 추가
            nurseInfo.put("nurse_id", UUID.randomUUID().toString());
            nurseInfo.put("employee_number", staffService.generateEmployeeNumber());

            db.collection("users")
                    .document(uid)
                    .collection("nurses")
                    .document("info")
                    .set(nurseInfo)
                    .get();

            String jwt = jwtUtil.generateToken(uid, req.getEmail());
            return new SignupResponse(true, "간호사 등록 성공", jwt);

        } catch (Exception e) {
            return new SignupResponse(false, "간호사 등록 실패: " + e.getMessage(), null);
        }
    }
}
