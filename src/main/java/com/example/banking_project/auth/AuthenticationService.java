package com.example.banking_project.auth;

import com.example.banking_project.auth.AuthenticationRequest;
import com.example.banking_project.auth.AuthenticationResponse;
import com.example.banking_project.auth.RegisterRequest;
import com.example.banking_project.config.JwtService;
import com.example.banking_project.entities.Role;
import com.example.banking_project.entities.User;
import com.example.banking_project.repos.AccountRepository;
import com.example.banking_project.repos.UserRepository;
import com.example.banking_project.services.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        /*
            This will allow us to create user and save it to the database.
            Moreover, it will return the generated token out of it.
        */

        //Creating user
        var user = User.builder()
                .name(request.getName())
                .surname(request.getSurname())
                .mail(request.getMail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword())) //We need to encode password for safety.
                .role(Role.USER)//Default role for every user at the moment.
                .build();

        //Saving to the db
        repository.save(user);
        //Generating token
        var jwt = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwt)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        //Authentication Manager has a role for giving permission.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getPhone(),
                        request.getPassword()
                )
        );

        //Checking the phone number and reaching to the info of user
        var user = repository.findByPhone(request.getPhone())
                .orElseThrow();// Catch exceptions and handle them.

        //If user is in db, returns its permission
        var jwt = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwt)
                .build();
    }
}
