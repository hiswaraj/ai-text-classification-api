package com.example.demo.dto.openrouter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ModelClassificationResult(
        String category,
        Double confidence,
        String explanation
) {
}
