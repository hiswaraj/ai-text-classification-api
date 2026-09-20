package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

public record ClassificationRequest(
        @NotBlank(message = "Text cannot be blank")
        @Size(max = 5000, message = "Text cannot exceed 5000 characters")
        String text
) implements Serializable {
}
