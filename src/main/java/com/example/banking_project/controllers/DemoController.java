package com.example.banking_project.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")  // Allow your frontend URL
@RestController
@RequestMapping("/api/v1/demo-controller")
@RequiredArgsConstructor
public class DemoController {


    @GetMapping
    public ResponseEntity<String> sayHello() {
        //Operations that will be done in databases.
        return ResponseEntity.status(HttpStatus.OK).body("Hello there!");
    }
}
