package com.example.demo.controller;

import com.example.demo.dto.ClassificationRequest;
import com.example.demo.dto.ClassificationResponse;
import com.example.demo.exception.GlobalExceptionHandler;
import com.example.demo.service.ClassificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TextClassificationControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private ClassificationService classificationService;

    @InjectMocks
    private TextClassificationController textClassificationController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(textClassificationController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET /api/v1/health should return 200 OK")
    void testHealthEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("AI Text Classification API is running"));
    }

    @Test
    @DisplayName("POST /api/v1/classify should return 200 and classification result")
    void testClassifySuccess() throws Exception {
        ClassificationResponse mockResponse = new ClassificationResponse(
                "Complaint",
                0.95,
                "The app crashed when I clicked checkout"
        );

        when(classificationService.classify(anyString())).thenReturn(mockResponse);

        ClassificationRequest request = new ClassificationRequest("The app crashed when I clicked checkout");

        mockMvc.perform(post("/api/v1/classify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.category").value("Complaint"))
                .andExpect(jsonPath("$.confidence").value(0.95))
                .andExpect(jsonPath("$.text").value("The app crashed when I clicked checkout"));
    }

    @Test
    @DisplayName("POST /api/v1/classify with blank text should return 400 Bad Request")
    void testClassifyBlankText() throws Exception {
        ClassificationRequest request = new ClassificationRequest("");

        mockMvc.perform(post("/api/v1/classify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").isArray());
    }
}
