package com.example.accessibility_analyzer.model;

import java.util.List;

public class AccessibilityScorer {
    public static int calculateScore(List<AccessibilityIssue> issues){
        int totalPenalty=issues.stream().mapToInt(issue->ScoringWeights.getWeight(issue.getType())).sum();
        return Math.max(0,100-totalPenalty);
    }
}
