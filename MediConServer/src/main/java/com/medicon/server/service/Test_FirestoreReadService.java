package com.medicon.server.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class Test_FirestoreReadService {

    public Map<String, Object> getUserData(String uid) throws Exception {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection("users").document(uid);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            return document.getData(); // 필드들을 Map으로 반환
        } else {
            throw new Exception("사용자를 찾을 수 없습니다: " + uid);
        }
    }
}
