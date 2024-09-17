package com.example.banking_project.controllers;

import com.example.banking_project.requests.CodeValidateRequest;
import com.example.banking_project.requests.PasswordRecoveryRequest;
import com.example.banking_project.services.PasswordRecoveryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@CrossOrigin(origins = "http://localhost:3000")  // Allow your frontend URL
@RestController
@RequestMapping("/api/v1/recovery")
@RequiredArgsConstructor
public class PasswordRecoveryController {

    private final PasswordRecoveryService service;
    @PostMapping("/password")
    public ResponseEntity<String> listTransactions(
            @Valid @RequestBody PasswordRecoveryRequest request
    ){
        return ResponseEntity.ok(service.createToken(request));
    }

    @PostMapping("/recover-password")
    public ResponseEntity<String> findValue(@Valid @RequestBody CodeValidateRequest request){
        return ResponseEntity.ok(service.getPasswordRecoveryData(request));
    }
}
