package com.archive.archive.exceptions;

public class InvalidRequestStateException extends RuntimeException{
    public InvalidRequestStateException(String message){
        super(message);
    }
}
