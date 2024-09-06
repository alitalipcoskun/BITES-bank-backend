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
    private Integer statusCode;
    private HttpStatus status;
    private Date timestamp;
    private String title;
    private String description;
}
