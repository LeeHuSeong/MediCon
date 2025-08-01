package com.medicon.server.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.cloud.FirestoreClient;
import com.medicon.server.dto.salary.SalaryRecordRequest;
import com.medicon.server.dto.user.DoctorDTO;
import com.medicon.server.dto.user.NurseDTO;
import com.medicon.server.dto.user.UserDTO;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StaffService {

    public List<UserDTO> getStaffList(String role, String keyword) throws Exception {
        Firestore db = FirestoreClient.getFirestore();
        List<UserDTO> result = new ArrayList<>();

        List<Integer> authorityFilter;
        if ("doctor".equalsIgnoreCase(role)) {
            authorityFilter = List.of(2);
        } else if ("nurse".equalsIgnoreCase(role)) {
            authorityFilter = List.of(1);
        } else {
            authorityFilter = Arrays.asList(1, 2); // "all"
        }

        // 사용자 필터링 (authority 기반)
        ApiFuture<QuerySnapshot> future = db.collection("users")
                .whereIn("authority", authorityFilter)
                .get();

        Set<String> addedUids = new HashSet<>();  // 이미 추가된 UID를 추적

        for (DocumentSnapshot userDoc : future.get().getDocuments()) {
            String uid = userDoc.getId();
            Map<String, Object> userMap = userDoc.getData();

            String name = (String) userMap.getOrDefault("name", "");
            String email = (String) userMap.getOrDefault("email", "");

            // `name` 또는 `email`에 `keyword`가 포함된 직원만 필터링
            if (keyword.isEmpty() || name.contains(keyword) || email.contains(keyword)) {
                String roleStr = (String) userMap.getOrDefault("role", "");

                // `createdAt`을 Long으로 처리하고 int로 변환
                long createdAt = 0L;
                Object createdAtObj = userMap.get("createdAt");
                if (createdAtObj instanceof Number) {
                    createdAt = ((Number) createdAtObj).longValue();
                }

                // `authority`는 Long으로 처리하고 int로 변환
                int authority = 0;
                Object authorityObj = userMap.get("authority");
                if (authorityObj instanceof Number) {
                    authority = ((Number) authorityObj).intValue();
                }

                // 중복된 uid가 result에 이미 존재하는지 확인
                if (addedUids.contains(uid)) {
                    continue;  // 이미 추가된 uid는 건너뜀
                }
                addedUids.add(uid);  // uid 추가

                String subCollection = roleStr.equalsIgnoreCase("doctor") ? "doctors" : "nurses";
                CollectionReference subCol = db.collection("users").document(uid).collection(subCollection);
                ApiFuture<QuerySnapshot> subFuture = subCol.get();
                List<QueryDocumentSnapshot> subDocs = subFuture.get().getDocuments();

                // subDocs가 비어있지 않으면 첫 번째 문서만 처리
                if (!subDocs.isEmpty()) {
                    QueryDocumentSnapshot doc = subDocs.get(0);  // 첫 번째 문서만 사용
                    Map<String, Object> detail = doc.getData();

                    if (roleStr.equalsIgnoreCase("doctor")) {
                        DoctorDTO dto = new DoctorDTO(
                                uid, name, (String) userMap.get("phone"), email, roleStr, authority, createdAt,
                                (String) detail.getOrDefault("doctor_id", ""),
                                (String) detail.getOrDefault("department", ""),
                                (String) detail.getOrDefault("rank", ""),
                                (String) detail.getOrDefault("employee_number", "")
                        );
                        result.add(dto);
                    } else if (roleStr.equalsIgnoreCase("nurse")) {
                        NurseDTO dto = new NurseDTO(
                                uid, name, (String) userMap.get("phone"), email, roleStr, authority, createdAt,
                                (String) detail.getOrDefault("nurse_id", ""),
                                (String) detail.getOrDefault("department", ""),
                                (String) detail.getOrDefault("rank", ""),
                                (String) detail.getOrDefault("employee_number", "")
                        );
                        result.add(dto);
                    }
                }
            }
        }

        return result;
    }

    //사번 생성 메서드
    public String generateEmployeeNumber() {
        long timestamp = System.currentTimeMillis();
        int random = (int)(Math.random() * 900 + 100); // 100~999
        return "EMP" + timestamp + random;
    }

    public void updateStaffInfo(String uid, String role, Map<String, String> payload) throws Exception {
        Firestore db = FirestoreClient.getFirestore();

        // 1. users/{uid} 문서 업데이트
        Map<String, Object> userUpdates = new HashMap<>();
        if (payload.containsKey("name")) userUpdates.put("name", payload.get("name"));
        if (payload.containsKey("phone")) userUpdates.put("phone", payload.get("phone"));

        db.collection("users").document(uid).update(userUpdates);

        // 2. 하위 컬렉션 업데이트
        String subCollection = role.equalsIgnoreCase("doctor") ? "doctors" : "nurses";
        CollectionReference subCol = db.collection("users").document(uid).collection(subCollection);
        QuerySnapshot subs = subCol.get().get();
        if (subs.isEmpty()) return;

        DocumentReference docRef = subs.getDocuments().get(0).getReference();
        Map<String, Object> detailUpdates = new HashMap<>();
        if (payload.containsKey("department")) detailUpdates.put("department", payload.get("department"));
        if (payload.containsKey("rank")) detailUpdates.put("rank", payload.get("rank"));

        docRef.update(detailUpdates);
    }

    public void deleteStaff(String uid, String role) throws Exception {
        Firestore db = FirestoreClient.getFirestore();

        // 1. 하위 컬렉션 삭제 (doctors or nurses)
        String subCollection = role.equalsIgnoreCase("doctor") ? "doctors" : "nurses";
        CollectionReference subCol = db.collection("users").document(uid).collection(subCollection);
        ApiFuture<QuerySnapshot> subFuture = subCol.get();
        for (QueryDocumentSnapshot doc : subFuture.get().getDocuments()) {
            doc.getReference().delete();  // 문서 삭제
        }

        // 2. 메인 users 문서 삭제
        db.collection("users").document(uid).delete();

        // 3. Firebase Auth 계정 삭제
        try {
            FirebaseAuth.getInstance().deleteUser(uid);
        } catch (FirebaseAuthException e) {
            // 예외 처리: 로그에 기록하거나 사용자에게 실패 메시지를 전달
            throw new RuntimeException("Firebase 인증 계정 삭제 실패: " + e.getMessage());
        }
    }

}
