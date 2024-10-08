package com.example.banking_project.auth;

import com.example.banking_project.entities.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    //JSON Web Tokens
    private String token;
    private Role role;
}
