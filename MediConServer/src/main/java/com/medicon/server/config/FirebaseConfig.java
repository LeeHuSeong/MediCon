package com.medicon.server.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initFirebase() throws IOException {
        // Classpath로부터 JSON 불러오기
        InputStream serviceAccount = getClass()
                .getClassLoader()
                .getResourceAsStream("medim-bb24e-firebase-adminsdk-fbsvc-8374d18ac3.json");

        if (serviceAccount == null) {
            throw new IllegalStateException("Firebase service account file not found!");
        }

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
            System.out.println("FirebaseApp initialized.");
        }
    }
}
