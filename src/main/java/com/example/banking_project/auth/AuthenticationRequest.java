package com.example.banking_project.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {
    //The requirements for sending auth request which are phone and password for this project.
    private String phone;
    private String password;
}
