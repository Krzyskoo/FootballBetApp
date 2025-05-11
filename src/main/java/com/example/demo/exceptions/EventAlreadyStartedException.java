package com.example.demo.exceptions;

public class EventAlreadyStartedException extends RuntimeException{
    public EventAlreadyStartedException() {
        super();
    }

    public EventAlreadyStartedException(String message) {
        super(message);
    }

    public EventAlreadyStartedException(String message, Throwable cause) {
        super(message, cause);
    }
}
