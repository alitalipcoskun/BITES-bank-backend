package com.example.banking_project.exceptions;


public class CodeNotMatchError extends RuntimeException {
    public CodeNotMatchError(String msg){
        super(msg);
    }
}
