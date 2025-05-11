package com.example.demo.exceptions;

public class InvalidResultPredictedException extends RuntimeException{
    public InvalidResultPredictedException() {
        super();
    }

    public InvalidResultPredictedException(String message) {
        super(message);
    }

    public InvalidResultPredictedException(String message, Throwable cause) {
        super(message, cause);
    }
}
