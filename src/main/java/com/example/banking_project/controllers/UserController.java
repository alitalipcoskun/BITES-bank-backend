package com.example.banking_project.controllers;


import com.example.banking_project.config.JwtService;
import com.example.banking_project.dtos.UserDTO;
import com.example.banking_project.entities.Account;
import com.example.banking_project.entities.User;
import com.example.banking_project.repos.UserRepository;
import com.example.banking_project.requests.UserProfileRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@CrossOrigin(origins = "http://127.0.0.1:3000")  // Allow your frontend URL
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    @PostMapping("/profile")
    public ResponseEntity<UserDTO> getUserProfile(@Valid @RequestBody UserProfileRequest request) {
        //User JWT is provided by Bearer part.
        String jwt = request.getToken();
        String userPhone = jwtService.extractUsername(jwt);

        //Returns response entity UNAUTHORIZED for the user that does not have JWT.
        if (userPhone == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        //Finding registered user
        User user = userRepository.findByPhone(userPhone)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        //Perform convertion user account information to String list.
        List<String> accList = user.getAccounts().stream().map(Account::getNo).collect(Collectors.toList());

        //Returning user information
        return ResponseEntity.ok(UserDTO.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .phone(user.getPhone())
                        .mail(user.getMail())
                        .surname(user.getSurname())
                        .role(user.getRole())
                        .created_at(user.getCreated_at())
                        .updated_at(user.getUpdated_at())
                        .accounts(accList)
                .build());
    }
}
