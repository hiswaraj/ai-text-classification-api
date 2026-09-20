package com.example.demo.exception;

public class OpenRouterApiException extends RuntimeException {
    public OpenRouterApiException(String message) {
        super(message);
    }

    public OpenRouterApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
