package com.example.demo.dto.openrouter;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public record OpenRouterChatRequest(
        String model,
        List<Message> messages,
        Double temperature,
        @JsonProperty("response_format") ResponseFormat responseFormat
) implements Serializable {
    public record Message(
            String role,
            String content
    ) implements Serializable{}

    public record ResponseFormat(
            String type
    ) implements Serializable{}
}
