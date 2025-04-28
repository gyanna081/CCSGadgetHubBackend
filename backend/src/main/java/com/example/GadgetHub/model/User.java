package com.example.GadgetHub.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String uid;             // Firebase UID
    private String firstName;       // First name
    private String lastName;        // Last name
    private String email;           // Email
    private String role;            // "admin" or "student"
    private LocalDateTime createdAt; // When account was created

    // For Firestore compatibility - we'll store as string in Firestore but keep as LocalDateTime in Java
    public void setCreatedAt(String createdAtStr) {
        if (createdAtStr != null && !createdAtStr.isEmpty()) {
            try {
                this.createdAt = LocalDateTime.parse(createdAtStr);
            } catch (Exception e) {
                this.createdAt = LocalDateTime.now();
            }
        }
    }
}