package com.example.demo.controller;

import com.example.demo.dto.ClassificationRequest;
import com.example.demo.dto.ClassificationResponse;
import com.example.demo.service.ClassificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class TextClassificationController {

    private final ClassificationService classificationService;

    @PostMapping(value = "/classify", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ClassificationResponse> classifyJson(@Valid @RequestBody ClassificationRequest request) {
        ClassificationResponse response = classificationService.classify(request.text());
        return ResponseEntity.ok(response);
    }


    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("AI Text Classification API is running");
    }
}
