package com.example.GadgetHub.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initializeFirebase() {
        try {
            ClassPathResource resource = new ClassPathResource("firebase-service-account.json");
            InputStream serviceAccount = resource.getInputStream();
            
            System.out.println("✅ Firebase service account file found, attempting initialization...");
            
            FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setStorageBucket("ccs-gadgethub.appspot.com")
                .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                // Test Firestore connection immediately
                try {
                    FirestoreClient.getFirestore();
                    System.out.println("✅ Firebase and Firestore initialized successfully");
                } catch (Exception e) {
                    System.err.println("❌ Firestore initialization failed: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.out.println("✅ Firebase already initialized with apps: " + FirebaseApp.getApps().size());
            }
        } catch (IOException e) {
            System.err.println("❌ Failed to initialize Firebase: " + e.getMessage());
            e.printStackTrace();
        }
    }
}