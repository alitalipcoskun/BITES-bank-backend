package com.example.banking_project.exceptions;

public class CreditCardDateException extends RuntimeException{
    public CreditCardDateException(String msg){
        super(msg);
    }
}
