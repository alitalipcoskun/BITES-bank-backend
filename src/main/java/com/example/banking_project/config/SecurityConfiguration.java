package com.example.banking_project.config;

import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.CorsFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

//These two (Configuration and EnableWebSecurity) must be used together after Spring Boot 3.0.

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    /*
        At the start of the application, Spring Security will look up to find out the type
        security filter chain and this security filter chain is the bean responsible of configuring
        all the HTTP security of our application.
    */

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        /*
          Whitelist will be applied here.
          Whitelist means endpoints that do not require authentication or any tokens.
          Creating account, log in endpoints are example to be in whitelist.

          Configuring Decision Management:
          After implementing filter, we want a once per request filter, which means every request should be authenticated.
          This means that we should not store the authentication state or session state should not be stored. (Stateless)
          AND
          After that authentication provider is introduced to Spring application.
         We require JWT filter to be used before that hence JWT Filter handles everything and update SecurityContextHolder
          and after that we call username password authentication.
         * */

        http
                .csrf(csrf -> csrf.disable())  // Disabling CSRF protection
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/v1/auth/**", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()//Permitted links for all sessions
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:3000");  // Your frontend origin
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }

}
