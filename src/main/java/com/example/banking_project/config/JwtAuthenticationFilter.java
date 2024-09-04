package com.example.banking_project.config;


/*
     In order to make this class a filter,
     we've multiple options. It is requested to make it active
     to handle all the requests for the design architecture.
     Extending the OncePerRequestFilter is one option because it already
     extends Filter class. Other approach is implementing Filter class.

     The first thing that this filter must handle is whether having JWTToken or not.

     After checking JWTToken, userDetailsService should be called whether the user is in our database or not.
     However, it is necessary to call JWTService before it because of extracting user information to send it to the database.
*/

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final HandlerExceptionResolver handlerExceptionResolver;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        /*
            These three parameters cannot be NULL.
            The request parameter our request to handle. We extract data from the request.
            The response is our response to give it to the client. New data may be added to the response.
            filterChain it is chain of responsibilities. List of filters to execute.
        */

        //When we make a call, we need to pass JWTAuthToken within the header. It should be in a header called Authorization.
        final String authHeader = request.getHeader("Authorization"); //Important, avoid TYPO in string argument.
        final String jwt;

        //For extracting user information
        final String userPhone;

        //If header is null or header is NOT A FIT (be careful about the conditions of if statements.) for JWToken, return it as null.
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }
        //Try catch block wraps the logic and uses HandlerExceptionResolver to forward the error to the global exception handler.
        try{
            //Extracting token from header, seperating header from "Bearer " substring.
            jwt = authHeader.substring(7);

            //To extract the user information, another class is created in this class. It is named as username because of the
            //service that is provided by Spring Boot.
            userPhone = jwtService.extractUsername(jwt);

            //Checking user permission and whether user is in database or not.
            //SecurityContextHolder.getContext().getAuthentication() -> NULL means user is not authenticated yet (not connected yet).
            if(userPhone != null && SecurityContextHolder.getContext().getAuthentication() == null){
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userPhone);
                if(jwtService.isTokenValid(jwt, userDetails)){
                    //If the user is valid, we need to update SecurityContext and send it to DispatcherServlet
                    //This variable is mandatory to update the security context.
                    UsernamePasswordAuthenticationToken authToken =  new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            filterChain.doFilter(request, response);
        } catch (Exception exception){
            handlerExceptionResolver.resolveException(request, response, null, exception);
        }

    }
}
