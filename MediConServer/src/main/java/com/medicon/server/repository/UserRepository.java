package com.medicon.server.repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

    private final Firestore firestore;

    public UserRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    public boolean existsByIdAndPassword(String id, String pw) throws Exception {
        ApiFuture<QuerySnapshot> future = firestore.collection("users")
                .whereEqualTo("id", id)
                .whereEqualTo("password", pw)
                .get();
        return !future.get().isEmpty();
    }
}

