package com.medicon.server.service;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
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
}
