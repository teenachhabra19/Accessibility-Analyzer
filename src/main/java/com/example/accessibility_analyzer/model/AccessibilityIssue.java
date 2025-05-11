package com.example.accessibility_analyzer.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccessibilityIssue {
    private String type;
    private String element;
    private String description;
}
