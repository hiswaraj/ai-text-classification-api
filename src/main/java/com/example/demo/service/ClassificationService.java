package com.example.demo.service;

import com.example.demo.dto.ClassificationResponse;

public interface ClassificationService {
    ClassificationResponse classify(String text);
}
