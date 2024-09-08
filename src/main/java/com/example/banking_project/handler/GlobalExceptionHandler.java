package com.example.banking_project.handler;

import com.example.banking_project.exceptions.ErrorMessage;
import com.example.banking_project.exceptions.ResourceExistException;
import com.example.banking_project.exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import java.util.Date;


/*
    This class will receive thrown exceptions and send them to the frontend for giving information about
    the status of the process without giving 500 error all the time.
*/

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler
{
    @ExceptionHandler(ResourceNotFoundException.class)
    public ErrorMessage resourceNotFoundException(ResourceNotFoundException ex, WebRequest request){
        return ErrorMessage.builder()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .status(HttpStatus.NOT_FOUND)
                .timestamp(new Date())
                .message(ex.getMessage())
                .description(request.getDescription(false))
                .build();
    }

    @ExceptionHandler(ResourceExistException.class)
    public ErrorMessage resourceExistException(ResourceExistException ex, WebRequest request){
        return ErrorMessage.builder()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .status(HttpStatus.FORBIDDEN)
                .timestamp(new Date())
                .message(ex.getMessage())
                .description(request.getDescription(false))
                .build();
    }

    @ExceptionHandler(AuthenticationException.class)
    public ErrorMessage phonePasswordException(AuthenticationException ex, WebRequest request){
        return ErrorMessage.builder()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .status(HttpStatus.UNAUTHORIZED)
                .timestamp(new Date())
                .message(ex.getMessage())
                .description(request.getDescription(false))
                .build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorMessage IllegalArgumentException(IllegalArgumentException ex, WebRequest request){
        return ErrorMessage.builder()
                .status(HttpStatus.UNAUTHORIZED)
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .timestamp(new Date())
                .message(ex.getMessage())
                .description(request.getDescription(false))
                .build();
    }
}
