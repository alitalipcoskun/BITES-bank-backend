package com.example.banking_project.controllers;


import com.example.banking_project.config.JwtService;
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


@CrossOrigin(origins = "http://127.0.0.1:3000")  // Allow your frontend URL
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    @PostMapping("/profile")
    public ResponseEntity<User> getUserProfile(@Valid @RequestBody UserProfileRequest request) {
        //User JWT is provided by Bearer part.
        System.out.println(request);
        String jwt = request.getToken();
        System.out.println("JWT "+ jwt);
        String userPhone = jwtService.extractUsername(jwt);
        System.out.println("Phone "+ userPhone);

        //Returns response entity UNAUTHORIZED for the user that does not have JWT.
        if (userPhone == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        //Finding registered user
        User user = userRepository.findByPhone(userPhone)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        //Returning user profile information.
        return ResponseEntity.ok(user);
    }
}
