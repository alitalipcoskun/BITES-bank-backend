package com.example.banking_project.controllers;


import com.example.banking_project.config.JwtService;
import com.example.banking_project.dtos.UserDTO;
import com.example.banking_project.entities.Account;
import com.example.banking_project.entities.User;
import com.example.banking_project.repos.UserRepository;
import com.example.banking_project.requests.ChangePasswordRequest;
import com.example.banking_project.requests.UserProfileRequest;
import com.example.banking_project.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@CrossOrigin(origins = "http://127.0.0.1:3000")  // Allow your frontend URL
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


    @PostMapping("/profile")
    public ResponseEntity<UserDTO> getUserProfile(
            @Valid @RequestBody UserProfileRequest request
    )
    {
        String jwt = request.getToken();
        return ResponseEntity.ok(userService.profile(jwt));
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(
        @Valid @RequestBody ChangePasswordRequest request
    )
    {
        return ResponseEntity.ok(userService.changePassword(request));
    }
}
