package com.example.banking_project.exceptions;

import org.apache.tomcat.websocket.AuthenticationException;


public class UnauthorizedActionError extends AuthenticationException {
    public UnauthorizedActionError(String msg){
        super(msg);
    }
}
