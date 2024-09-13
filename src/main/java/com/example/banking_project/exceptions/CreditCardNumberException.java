package com.example.banking_project.exceptions;

public class CreditCardNumberException extends RuntimeException{
    public CreditCardNumberException(String msg){
        super(msg);
    }
}
