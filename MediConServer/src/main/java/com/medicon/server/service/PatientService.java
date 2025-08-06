package com.medicon.server.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.Transaction;
import com.google.firebase.cloud.FirestoreClient;
import com.medicon.server.dto.auth.signup.PatientSignupRequest;
import com.medicon.server.dto.auth.signup.SignupResponse;
import com.medicon.server.dto.user.PatientDTO;
import com.medicon.server.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class PatientService {

    @Autowired
    private JwtUtil jwtUtil;

    public SignupResponse registerPatient(String uid, PatientSignupRequest req) {
        try {
            System.out.println("🔍 환자 등록 시작 - UID: " + uid);
            System.out.println("📧 이메일: " + req.getEmail());
            System.out.println("👤 이름: " + req.getName());
            
            Firestore db = FirestoreClient.getFirestore();

            // 날짜 기반 patient_id 생성 (트랜잭션 사용)
            String dateStr = new SimpleDateFormat("yyyyMMdd").format(new Date());
            System.out.println("📅 날짜: " + dateStr);
            
            String patientId = generateUniquePatientIdWithTransaction(db, dateStr);
            System.out.println("🆔 생성된 patient_id: " + patientId);

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

            System.out.println("💾 Firestore에 저장 시작...");
            // uid를 문서 ID로 사용하여 저장
            db.collection("patients").document(uid).set(data).get();
            System.out.println("✅ Firestore 저장 완료!");

            String jwt = jwtUtil.generateToken(uid, req.getEmail());
            System.out.println("🎉 환자 등록 완료: " + req.getName());
            return new SignupResponse(true, "환자 등록 성공", jwt);

        } catch (Exception e) {
            System.err.println("❌ 환자 등록 실패: " + e.getMessage());
            e.printStackTrace();
            return new SignupResponse(false, "환자 등록 실패: " + e.getMessage(), null);
        }
    }

    /**
     * 트랜잭션을 사용하여 고유한 patient_id 생성
     */
    private String generateUniquePatientIdWithTransaction(Firestore db, String dateStr) throws ExecutionException, InterruptedException {
        return db.runTransaction(transaction -> {
            // 해당 날짜의 카운터 문서 조회
            String counterDocId = "counter_" + dateStr;
            var counterDoc = db.collection("counters").document(counterDocId);
            DocumentSnapshot counterSnapshot = transaction.get(counterDoc).get();
            
            int currentCount = 0;
            if (counterSnapshot.exists()) {
                currentCount = counterSnapshot.getLong("patient_count").intValue();
            }
            
            // 다음 순번 생성
            int nextSeq = currentCount + 1;
            String patientId = "PAT" + dateStr + String.format("%03d", nextSeq);
            
            // 카운터 업데이트
            Map<String, Object> counterData = new HashMap<>();
            counterData.put("patient_count", nextSeq);
            counterData.put("last_updated", System.currentTimeMillis());
            transaction.set(counterDoc, counterData);
            
            return patientId;
        }).get();
    }
}
