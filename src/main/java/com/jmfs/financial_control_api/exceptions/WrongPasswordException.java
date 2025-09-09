package com.jmfs.financial_control_api.exceptions;

public class WrongPasswordException extends RuntimeException {
    public WrongPasswordException(String message){
        super(message);
    }
}
