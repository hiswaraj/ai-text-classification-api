package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ClassificationResponse(
        String category,
        Double confidence,
        String text
) implements Serializable {
}
