package com.example.banking_project.exceptions;

import java.util.function.Supplier;

public class ResourceNotFoundException extends  RuntimeException{
    public ResourceNotFoundException(String msg){
        super(msg);
    }
}
