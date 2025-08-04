package com.medicon.server.service;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.medicon.server.dto.auth.signup.PatientSignupRequest;
import com.medicon.server.dto.auth.signup.SignupResponse;
import com.medicon.server.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class PatientService {

    @Autowired private JwtUtil jwtUtil;

    // 일자별 patient_id 숫자 증가용 카운터
    private final Map<String, AtomicInteger> dailyCounter = new HashMap<>();

    public SignupResponse registerPatient(String uid, PatientSignupRequest req) {
        try {
            Firestore db = FirestoreClient.getFirestore();

            // 날짜 기반 patient_id 생성
            String dateStr = new SimpleDateFormat("yyyyMMdd").format(new Date());
            int seq = dailyCounter.computeIfAbsent(dateStr, k -> new AtomicInteger(0)).incrementAndGet();
            String patientId = "PAT" + dateStr + String.format("%03d", seq);

            Map<String, Object> data = new HashMap<>();
            data.put("uid", uid);
            data.put("email", req.getEmail());
            data.put("name", req.getName());
            data.put("phone", req.getPhone());
            data.put("role", "patient");
            data.put("authority", 0);
            data.put("birthdate", req.getBirthdate());
            data.put("gender", req.getGender());
            data.put("address", req.getAddress());
            data.put("rnn", req.getRnn());
            data.put("createdAt", System.currentTimeMillis());
            data.put("patient_id", patientId);

            // 경로: patients/{uid}
            db.collection("patients").document(uid).set(data).get();

            String jwt = jwtUtil.generateToken(uid, req.getEmail());
            return new SignupResponse(true, "환자 등록 성공", jwt);

        } catch (Exception e) {
            return new SignupResponse(false, "환자 등록 실패: " + e.getMessage(), null);
        }
    }
}
