package com.example.GadgetHub.service;

import com.example.GadgetHub.dto.FirebaseUserDto;
import com.example.GadgetHub.model.User;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service
public class UserService {

    private final Firestore firestore;

    public UserService() {
        this.firestore = FirestoreClient.getFirestore();
    }

    public User syncUser(FirebaseUserDto firebaseUser) {
        try {
            System.out.println("Starting to sync user: " + firebaseUser.getUid());
            DocumentReference docRef = firestore.collection("users").document(firebaseUser.getUid());
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();

            User user;
            if (document.exists()) {
                System.out.println("User exists in Firestore, retrieving data");
                user = document.toObject(User.class);
                return user;
            } else {
                System.out.println("User does not exist, creating new user in Firestore");
                // Using a Map for more reliable storage
                Map<String, Object> userData = new HashMap<>();
                userData.put("uid", firebaseUser.getUid());
                userData.put("email", firebaseUser.getEmail());
                userData.put("firstName", firebaseUser.getFirstName() != null ? firebaseUser.getFirstName() : "Unnamed");
                userData.put("lastName", firebaseUser.getLastName() != null ? firebaseUser.getLastName() : "");
                userData.put("role", "student");
                userData.put("createdAt", LocalDateTime.now().toString()); // Store as string

                ApiFuture<WriteResult> writeResult = docRef.set(userData);
                System.out.println("User created at: " + writeResult.get().getUpdateTime());
                
                // Create and return a User object
                user = new User();
                user.setUid(firebaseUser.getUid());
                user.setEmail(firebaseUser.getEmail());
                user.setFirstName(firebaseUser.getFirstName() != null ? firebaseUser.getFirstName() : "Unnamed");
                user.setLastName(firebaseUser.getLastName() != null ? firebaseUser.getLastName() : "");
                user.setRole("student");
                user.setCreatedAt(LocalDateTime.now().toString()); // Changed to use toString()
                
                return user;
            }
        } catch (Exception e) {
            System.err.println("Error in syncUser: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error syncing user: " + e.getMessage(), e);
        }
    }

    public Optional<User> getUserByUid(String uid) {
        try {
            DocumentReference docRef = firestore.collection("users").document(uid);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();

            if (document.exists()) {
                return Optional.ofNullable(document.toObject(User.class));
            } else {
                return Optional.empty();
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error retrieving user", e);
        }
    }
}