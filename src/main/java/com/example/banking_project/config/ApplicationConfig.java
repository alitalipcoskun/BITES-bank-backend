package com.example.banking_project.config;

import com.example.banking_project.repos.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "http://localhost:3000")  // Allow your frontend URL
@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {
    private final UserRepository userRepository;
    //Beans should be public!
    @Bean
    public UserDetailsService userDetailsService(){
        //lambda expression
        return username -> userRepository.findByPhone(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        /*
            It is the data access object which is responsible for fetching data, (userDetails)
            and also encode password and so forth.
            We have many implementations. One of them is DaoAuthenticationProvider.
        */
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        //We need to tell to the authentication provider which user details service to use in order to fetch
        //information of our user hence we may have different userDetailsService.
        authProvider.setUserDetailsService(userDetailsService());

        //Provide password encoder
        authProvider.setPasswordEncoder(passwordEncoder());

        //This is the minimum requirement for an authProvider
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    /*
        Last step to finish this class is AuthenticationManager
        It is responsible to manage auth, with its methods.
    */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
       //This config already hold information about the manager.
       return config.getAuthenticationManager();
    }
    //We need to provide at least two endpoints where user can create an account and login.
}
