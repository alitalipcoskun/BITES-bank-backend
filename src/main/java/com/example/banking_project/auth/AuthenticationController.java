package com.example.banking_project.auth;
/*
This controller have two endpoints for login and sign up screens.
*/

import com.example.banking_project.auth.AuthenticationRequest;
import com.example.banking_project.auth.AuthenticationResponse;
import com.example.banking_project.auth.AuthenticationService;
import com.example.banking_project.auth.RegisterRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    //These endpoints are for creating user and checking the authentications.
    private final AuthenticationService service;
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @Valid @RequestBody RegisterRequest request
            ){
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse>authenticate(
            @RequestBody AuthenticationRequest request,
            HttpServletResponse response
    ){
        AuthenticationResponse authenticationResponse = service.authenticate(request, response);
        return ResponseEntity.ok(authenticationResponse)
                ;
    }
}
