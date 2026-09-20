package com.example.demo.service.impl;

import com.example.demo.config.OpenRouterConfig;
import com.example.demo.dto.ClassificationResponse;
import com.example.demo.dto.openrouter.ModelClassificationResult;
import com.example.demo.dto.openrouter.OpenRouterChatRequest;
import com.example.demo.dto.openrouter.OpenRouterChatResponse;
import com.example.demo.exception.OpenRouterApiException;
import com.example.demo.service.ClassificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenRouterClassificationService implements ClassificationService {

    private static final Set<String> ALLOWED_CATEGORIES = Set.of("Complaint", "Query", "Feedback", "Other");

    private static final String SYSTEM_PROMPT = """
            You are an expert text classification AI.
            Classify the given input text into exactly one of the following categories:
            - Complaint
            - Query
            - Feedback
            - Other

            Guidelines:
            - "Complaint": Expresses dissatisfaction, reporting a bug, defect, service issue, or grievance.
            - "Query": Asks a question, seeks information, pricing details, clarification, or guidance.
            - "Feedback": Gives constructive suggestions, praise, thoughts, or general opinion about a product/service.
            - "Other": Greetings, random chit-chat, gibberish, spam, or anything not fitting the above.

            You MUST return your answer in valid JSON format matching this exact schema:
            {
              "category": "Complaint" | "Query" | "Feedback" | "Other",
              "confidence": 0.0 to 1.0,
              "explanation": "Short 1-sentence reason"
            }

            If unsure of confidence, provide an estimated confidence between 0.50 and 0.99.
            Output JSON only, with no Markdown wrapper or extra text.
            """;

    private final RestClient openRouterRestClient;
    private final OpenRouterConfig openRouterConfig;
    private final ObjectMapper objectMapper;

    @Override
    public ClassificationResponse classify(String text) {
        OpenRouterChatRequest chatRequest = new OpenRouterChatRequest(
                openRouterConfig.getModel(),
                List.of(
                        new OpenRouterChatRequest.Message("system", SYSTEM_PROMPT),
                        new OpenRouterChatRequest.Message("user", text)
                ),
                0.1,
                new OpenRouterChatRequest.ResponseFormat("json_object")
        );

        OpenRouterChatResponse chatResponse;
        try {
            chatResponse = openRouterRestClient.post()
                    .uri("/chat/completions")
                    .body(chatRequest)
                    .retrieve()
                    .body(OpenRouterChatResponse.class);
        } catch (RestClientException ex) {
            log.error("Failed to communicate with OpenRouter API: {}", ex.getMessage(), ex);
            throw new OpenRouterApiException("OpenRouter API request failed: " + ex.getMessage(), ex);
        }

        if (chatResponse == null || chatResponse.choices() == null || chatResponse.choices().isEmpty()) {
            log.error("Empty response received from OpenRouter API");
            throw new OpenRouterApiException("No classification choices returned from OpenRouter AI model.");
        }

        String content = chatResponse.choices().get(0).message().content();
        log.debug("OpenRouter raw response content: {}", content);

        ModelClassificationResult modelResult = parseModelResult(content);

        String normalizedCategory = normalizeCategory(modelResult.category());
        Double normalizedConfidence = normalizeConfidence(modelResult.confidence());

        return new ClassificationResponse(normalizedCategory, normalizedConfidence, text);
    }

    private ModelClassificationResult parseModelResult(String content) {
        if (content == null || content.isBlank()) {
            return new ModelClassificationResult("Other", 0.50, "Empty response from AI");
        }

        String cleaned = content.trim();
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7);
        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring(3);
        }
        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3);
        }
        cleaned = cleaned.trim();

        return objectMapper.readValue(cleaned, ModelClassificationResult.class);
    }

    private String normalizeCategory(String category) {
        if (category == null || category.isBlank()) {
            return "Other";
        }
        for (String allowed : ALLOWED_CATEGORIES) {
            if (allowed.equalsIgnoreCase(category.trim())) {
                return allowed;
            }
        }
        return "Other";
    }

    private Double normalizeConfidence(Double confidence) {
        if (confidence == null) {
            return 0.85; // Fallback mapping if AI does not return confidence
        }
        if (confidence < 0.0) {
            return 0.0;
        }
        if (confidence > 1.0) {
            return 1.0;
        }
        return Math.round(confidence * 100.0) / 100.0;
    }
}
