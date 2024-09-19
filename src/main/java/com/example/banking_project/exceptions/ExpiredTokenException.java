package com.example.banking_project.exceptions;

public class ExpiredTokenException extends RuntimeException{
    public ExpiredTokenException(String msg){
        super(msg);
    }
}
