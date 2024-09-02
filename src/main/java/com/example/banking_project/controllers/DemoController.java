package com.example.banking_project.controllers;

import com.example.banking_project.entities.Account;
import com.example.banking_project.entities.User;
import com.example.banking_project.repos.AccountRepository;
import com.example.banking_project.repos.TransactionRepository;
import com.example.banking_project.repos.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/demo-controller")
@RequiredArgsConstructor
public class DemoController {


    @GetMapping
    public ResponseEntity<String> sayHello() {
        //Operations that will be done in databases.
        System.out.println("Entered!");
        return ResponseEntity.status(HttpStatus.OK).body("Hello there!");
    }
}
