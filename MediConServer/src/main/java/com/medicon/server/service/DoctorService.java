package com.medicon.server.service;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.medicon.server.dto.auth.signup.DoctorSignupRequest;
import com.medicon.server.dto.auth.signup.SignupResponse;
import com.medicon.server.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class DoctorService {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private StaffService staffService;

    public SignupResponse registerDoctor(String uid, DoctorSignupRequest req) {
        try {
            Firestore db = FirestoreClient.getFirestore();

            // 상위 문서: users/{uid}
            Map<String, Object> userFields = new HashMap<>();
            userFields.put("uid", uid);
            userFields.put("email", req.getEmail());
            userFields.put("name", req.getName());
            userFields.put("phone", req.getPhone());
            userFields.put("role", "doctor");
            userFields.put("authority", 2);
            userFields.put("createdAt", System.currentTimeMillis());

            db.collection("users").document(uid).set(userFields).get();

            // 하위 컬렉션: users/{uid}/doctors/info
            Map<String, Object> doctorInfo = new HashMap<>();
            doctorInfo.put("uid", uid);
            doctorInfo.put("department", req.getDepartment());
            doctorInfo.put("rank", req.getRank());
            doctorInfo.put("doctor_id", UUID.randomUUID().toString());
            doctorInfo.put("employee_number", staffService.generateEmployeeNumber());
            db.collection("users")
                    .document(uid)
                    .collection("doctors")
                    .document("info")
                    .set(doctorInfo)
                    .get();

            String jwt = jwtUtil.generateToken(uid, req.getEmail());
            return new SignupResponse(true, "의사 등록 성공", jwt);

        } catch (Exception e) {
            return new SignupResponse(false, "의사 등록 실패: " + e.getMessage(), null);
        }
    }
}
