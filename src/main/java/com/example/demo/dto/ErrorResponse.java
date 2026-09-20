package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        List<String> details
) implements Serializable {
    public ErrorResponse(int status, String error, String message) {
        this(Instant.now(), status, error, message, null);
    }

    public ErrorResponse(int status, String error, String message, List<String> details) {
        this(Instant.now(), status, error, message, details);
    }
}
