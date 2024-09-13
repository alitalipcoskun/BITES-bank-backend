package com.example.banking_project.handler;

import com.example.banking_project.exceptions.CreditCardNumberException;
import com.example.banking_project.exceptions.ErrorMessage;
import com.example.banking_project.exceptions.ResourceExistException;
import com.example.banking_project.exceptions.ResourceNotFoundException;
import jakarta.servlet.ServletException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.util.Date;


/*
    This class will receive thrown exceptions and send them to the frontend for giving information about
    the status of the process without giving 500 error all the time.
*/


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler
{
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorMessage> resourceNotFoundException(ResourceNotFoundException ex, WebRequest request){
        log.error("Resource Not Found Exception");
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorMessage.builder()
                        .timestamp(new Date())
                        .message(ex.getMessage())
                        .description(request.getDescription(false))
                        .build());
    }

    @ExceptionHandler(ResourceExistException.class)
    public ResponseEntity<ErrorMessage> resourceExistException(ResourceExistException ex, WebRequest request){
        log.error("Resource Exist Exception");
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ErrorMessage.builder()
                        .timestamp(new Date())
                        .message(ex.getMessage())
                        .description(request.getDescription(false))
                        .build());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorMessage> phonePasswordException(AuthenticationException ex, WebRequest request){
        log.error("Authentication Exception");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorMessage.builder()
                .timestamp(new Date())
                .message("Phone number or password is incorrect. Try again.")
                .description(request.getDescription(false))
                .build());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorMessage> IllegalArgumentException(IllegalArgumentException ex, WebRequest request){
        log.error("Illegal Argument Exception");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorMessage.builder()
                        .timestamp(new Date())
                        .message(ex.getMessage())
                        .description(request.getDescription(false))
                        .build());
    }

    @ExceptionHandler(ServletException.class)
    public ResponseEntity<ErrorMessage> ServletException(IllegalArgumentException ex, WebRequest req){
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ErrorMessage.builder()
                        .timestamp(new Date())
                        .message(ex.getMessage())
                        .description(req.getDescription(false))
                        .build());
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorMessage> IOException(IllegalArgumentException ex, WebRequest req){
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ErrorMessage.builder()
                        .timestamp(new Date())
                        .message(ex.getMessage())
                        .description(req.getDescription(false))
                        .build());
    }

    @ExceptionHandler(CreditCardNumberException.class)
    public ResponseEntity<ErrorMessage> creditCardError(CreditCardNumberException ex, WebRequest req){
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                .body(ErrorMessage.builder()
                        .timestamp(new Date())
                        .message(ex.getMessage())
                        .description(req.getDescription(false))
                        .build());
    }
}
