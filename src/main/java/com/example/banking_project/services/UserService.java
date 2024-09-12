package com.example.banking_project.services;

import com.example.banking_project.config.JwtService;
import com.example.banking_project.dtos.UserDTO;
import com.example.banking_project.entities.Account;
import com.example.banking_project.entities.User;
import com.example.banking_project.repos.UserRepository;
import com.example.banking_project.requests.ChangePasswordRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public UserDTO profile(String jwt){
        //User JWT is provided by Bearer part.

        String userPhone = jwtService.extractUsername(jwt);

        //Returns response entity UNAUTHORIZED for the user that does not have JWT.
        if (userPhone == null) {
            throw new IllegalArgumentException("User does not have permission to fetch the profile");
        }

        User user = findUserByPhone(userPhone);

        //Perform convertion user account information to String list.
        List<String> accList = user.getAccounts().stream().map(Account::getNo).collect(Collectors.toList());

        //Returning user information
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .phone(user.getPhone())
                .mail(user.getMail())
                .surname(user.getSurname())
                .role(user.getRole())
                .created_at(user.getCreated_at())
                .updated_at(user.getUpdated_at())
                .accounts(accList)
                .build();
    }


    public String changePassword(ChangePasswordRequest request) {

        // Extracting the "current_password" argument from request
        String currPassword = request.getCurrentPassword();

        // Extracting the phone number of the current session
        String userPhone = extractPhone();

        // Extracting current user information
        User user = findUserByPhone(userPhone);

        // Validating current password with the saved encoded password.
        checkPassword(currPassword, user.getPassword());

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);

        return "Password change successfully done!";
    }

    private String extractPhone(){
        /*
            This code retrieves the currently authenticated user's information (as a UserDetails object) from the
            security context in a Spring Security application. It allows you to access the logged-in user's details,
            such as their username and roles.
        */
        //User JWT is provided by Bearer part.
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //Returns response entity UNAUTHORIZED for the user that does not have JWT.
        if (userDetails == null) {
            throw new IllegalArgumentException("User not found!");
        }
        log.info("User found!");

        //Extracting subject
        return userDetails.getUsername();
    }

    private User findUserByPhone(String userPhone){
        //Finding registered user
        return userRepository.findByPhone(userPhone)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    private void checkPassword(String current_password, String encoded_password){
        if(!passwordEncoder.matches(current_password, encoded_password)){
            log.error("Match failed!");
            throw new IllegalArgumentException("Current password value that you've entered does not match with your current password");
        }
    }
}
