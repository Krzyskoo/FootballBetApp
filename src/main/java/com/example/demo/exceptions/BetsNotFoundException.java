package com.example.demo.exceptions;

public class BetsNotFoundException extends RuntimeException{
    public BetsNotFoundException() {
        super();
    }
    public BetsNotFoundException(String message) {
        super(message);
    }
    public BetsNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
