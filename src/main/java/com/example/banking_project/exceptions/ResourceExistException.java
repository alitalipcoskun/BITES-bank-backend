package com.example.banking_project.exceptions;

public class ResourceExistException extends  RuntimeException{
    public ResourceExistException(String message){
        super(message);
    }
}
