package com.example.banking_project.auth;

import com.example.banking_project.config.JwtService;
import com.example.banking_project.dtos.UserDTO;
import com.example.banking_project.entities.Role;
import com.example.banking_project.entities.User;
import com.example.banking_project.exceptions.ResourceExistException;
import com.example.banking_project.exceptions.ResourceNotFoundException;
import com.example.banking_project.repos.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    // No repository is used other than UserRepository.
    private final UserRepository repository;

    // To send encoded password to db
    private final PasswordEncoder passwordEncoder;

    // For JWT operations
    private final JwtService jwtService;

    //For managing user auth
    private final AuthenticationManager authenticationManager;

    public UserDTO register(RegisterRequest request) {
        /*
            This will allow us to create user and save it to the database.
            If the user already exists on database in phoneNumber or mail attributes, then it throws error.
            Moreover, it will return the generated token out of it.
        */

        // Verify that userPhone or mail does not registered on database, otherwise throws error.


        // Verify that phone does not exist on the user database
        checkUserPhoneExits(request.getPhone());
        // Verify that mail does not exist on the user database
        checkUserMailExits(request.getMail());

        // Perform build operation for the user.
        User user = User.builder()
                .name(request.getName())
                .surname(request.getSurname())
                .mail(request.getMail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword())) //We need to encode password for safety.
                .role(Role.USER) //Default role for every user at the moment.
                .build();

        // Perform save operation to the user database
        repository.save(user);

        // Return as DTO
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .surname(user.getSurname())
                .mail(user.getMail())
                .phone(user.getPhone())
                .role(user.getRole())
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        /*
            The function first checks whether the user already exists or not, if exists, then authenticates
                  -> Checks for phone number
                  !!! If other identical values used for auth, they also can be implemented easily.
            if it does not exist, then throws error.
        */

        //Authentication Manager has a role for giving permission.
        User user;
        user = findUserByPhone(request.getPhone()); // Check user whether the phone number exists on database.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getPhone(),
                        request.getPassword()
                )
        ); // Thrown errors has caught by GlobalExceptionHandler.

        //If user is in db, returns its permission
        String jwt = jwtService.generateToken(user);

        return buildResponse(jwt, user.getRole());
    }

    private AuthenticationResponse buildResponse(String token, Role role){
        return AuthenticationResponse.builder()
                .token(token)
                .role(role)
                .build();
    }

    private User findUserByPhone(String phoneNumber){
        //Checking the phone number and reaching to the info of user
        return repository.findByPhone(phoneNumber)
                .orElseThrow(() -> new ResourceNotFoundException("User is not signed in to this service. Please sign in."));// Catch exceptions and send to the client.
    }

    private void checkUserPhoneExits(String phone){
        Optional<User> userOpt = repository.findByPhone(phone);
        checkUserExists(userOpt);
    }

    private <T> T checkUserExists(Optional<T> optionalT){
        if(optionalT.isEmpty()){
            // Expected behaviour.
            return null;
        }else{
            throw new ResourceExistException("The entered mail or phone already registered to the website. Try different."); // To give information to the client.
        }

    }

    private void checkUserMailExits(String mail){
        Optional<User> userOpt = repository.findByMail(mail);
        checkUserExists(userOpt);
    }
}
