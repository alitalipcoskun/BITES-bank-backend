package com.example.banking_project.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.Date;

@Getter
@AllArgsConstructor
@Builder
@Setter
public class ErrorMessage {
    private Date timestamp;
    private String message;
    private String description;
}
