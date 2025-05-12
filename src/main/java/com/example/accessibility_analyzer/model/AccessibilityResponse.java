package com.example.accessibility_analyzer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccessibilityResponse {
    private int score;
    private boolean passed;
    private List<AccessibilityIssue> issues;
    private String message;
}
