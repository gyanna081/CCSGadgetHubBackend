package com.example.GadgetHub.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FirebaseUserDto {
    private String uid;
    private String email;
    private String firstName;
    private String lastName;
}
