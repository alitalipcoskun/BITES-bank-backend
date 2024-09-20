package com.example.banking_project.auth;
/*
This controller have two endpoints for login and sign up screens.
*/

import com.example.banking_project.dtos.UserDTO;
import com.example.banking_project.requests.CodeValidateRequest;
import com.example.banking_project.requests.PasswordRecoveryRequest;
import com.example.banking_project.requests.ValidPasswordChangeRequest;
import com.example.banking_project.services.PasswordRecoveryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;


@CrossOrigin(origins = "http://localhost:3000")  // Allow your frontend URL
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    //These endpoints are for creating user and checking the authentications.
    private final AuthenticationService authService;
    private final PasswordRecoveryService passwordService;
    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(
            @Valid @RequestBody RegisterRequest request
            ){
        // Perform register operation on AuthenticationService.
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse>authenticate(
            @Valid @RequestBody AuthenticationRequest request
    ){
        // Perform authenticate operation on AuthenticationService.
        AuthenticationResponse authenticationResponse = authService.authenticate(request);
        return ResponseEntity.ok(authenticationResponse);
    }
    @PostMapping("/forget-password")
    public ResponseEntity<String> listTransactions(
            @Valid @RequestBody PasswordRecoveryRequest request
    ) throws ParseException {
        return ResponseEntity.ok(passwordService.createToken(request));
    }

    @PostMapping("/recover-password")
    public ResponseEntity<String> findValue(@Valid @RequestBody CodeValidateRequest request) throws ParseException {
        return ResponseEntity.ok(passwordService.getPasswordRecoveryData(request));
    }

    @PostMapping("/password-change")
    public ResponseEntity<String> passwordChange(@Valid @RequestBody ValidPasswordChangeRequest request) throws ParseException {
        return ResponseEntity.ok(passwordService.securePasswordChange(request));
    }

}
