package com.medicon.server.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.medicon.server.dto.user.DoctorDTO;
import com.medicon.server.dto.user.NurseDTO;
import com.medicon.server.dto.user.UserDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class StaffService {

    public List<UserDTO> getStaffList(String role) throws Exception {
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

        // 1. 사용자 필터링 (authority 기반)
        ApiFuture<QuerySnapshot> future = db.collection("users")
                .whereIn("authority", authorityFilter)
                .get();

        for (DocumentSnapshot userDoc : future.get().getDocuments()) {
            String uid = userDoc.getId();
            Map<String, Object> userMap = userDoc.getData();

            int authority = ((Number) userMap.get("authority")).intValue();
            String name = (String) userMap.getOrDefault("name", "");
            String email = (String) userMap.getOrDefault("email", "");
            String phone = (String) userMap.getOrDefault("phone", "");
            String roleStr = (String) userMap.getOrDefault("role", "");

            // createdAt 안전하게 파싱
            long createdAt = 0L;
            Object createdAtObj = userMap.get("createdAt");
            if (createdAtObj instanceof Number) {
                createdAt = ((Number) createdAtObj).longValue();
            }

            String subCollection = (authority == 2) ? "doctors" : "nurse";

            // 2. 하위 컬렉션 조회
            CollectionReference subCol = db.collection("users").document(uid).collection(subCollection);
            ApiFuture<QuerySnapshot> subFuture = subCol.get();
            List<QueryDocumentSnapshot> subDocs = subFuture.get().getDocuments();

            for (QueryDocumentSnapshot doc : subDocs) {
                Map<String, Object> detail = doc.getData();

                if (authority == 2) { // 의사
                    DoctorDTO dto = new DoctorDTO(
                            uid, name, phone, email, roleStr, authority, createdAt,
                            (String) detail.getOrDefault("doctor_id", ""),
                            (String) detail.getOrDefault("department", ""),
                            (String) detail.getOrDefault("rank", ""),
                            (String) detail.getOrDefault("employee_number", "")
                    );
                    result.add(dto);

                } else if (authority == 1) { // 간호사
                    NurseDTO dto = new NurseDTO(
                            uid, name, phone, email, roleStr, authority, createdAt,
                            (String) detail.getOrDefault("nurse_id", ""),
                            (String) detail.getOrDefault("department", ""),
                            (String) detail.getOrDefault("rank", ""),
                            (String) detail.getOrDefault("employee_number", "")
                    );
                    result.add(dto);
                }
            }
        }

        return result;
    }
}
