package com.example.GadgetHub.controller;

import com.example.GadgetHub.dto.FirebaseUserDto;
import com.example.GadgetHub.model.User;
import com.example.GadgetHub.service.UserService;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/sync")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/user")
    public ResponseEntity<?> syncUser(@RequestBody FirebaseUserDto firebaseUserDto) {
        try {
            System.out.println("Received sync request for: " + firebaseUserDto.getUid() + ", " + firebaseUserDto.getEmail());
            User syncedUser = userService.syncUser(firebaseUserDto);
            return ResponseEntity.ok(syncedUser);
        } catch (Exception e) {
            e.printStackTrace(); // Print full stack trace
            // Return the error details to the client
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            errorResponse.put("cause", e.getCause() != null ? e.getCause().getMessage() : "Unknown");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/get-by-uid")
    public ResponseEntity<User> getUserByUid(@RequestParam String uid) {
        return userService.getUserByUid(uid)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/test-firestore")
    public ResponseEntity<String> testFirestore() {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("test", "value");
            data.put("timestamp", java.time.LocalDateTime.now().toString());
            
            com.google.cloud.firestore.Firestore firestore = com.google.firebase.cloud.FirestoreClient.getFirestore();
            firestore.collection("test").document("test-doc").set(data).get();
            
            return ResponseEntity.ok("Firestore write successful");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Firestore error: " + e.getMessage());
        }
    }
    
    @PostMapping("/set-admin")
    public ResponseEntity<?> setAdmin(@RequestParam String uid) {
        try {
            com.google.cloud.firestore.DocumentReference docRef = FirestoreClient.getFirestore().collection("users").document(uid);
            Map<String, Object> updates = new HashMap<>();
            updates.put("role", "admin");
            docRef.update(updates).get();
            return ResponseEntity.ok(Map.of("success", true, "message", "User set as admin"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}