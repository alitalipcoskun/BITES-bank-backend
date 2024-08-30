package com.example.banking_project.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    //Required data to register data to our database as a user. It may change on different projects.
    private String name;
    private String surname;
    private String mail;
    private String phone;
    private String password;
}
