package com.example.banking_project.exceptions;



public class ResourceNotFoundException extends  RuntimeException {
    public ResourceNotFoundException(String msg){
        super(msg);
    }
}
