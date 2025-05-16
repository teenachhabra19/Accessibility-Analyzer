package com.example.accessibility_analyzer.model;

import java.util.HashMap;
import java.util.Map;

public class ScoringWeights {
    private static final Map<String,Integer> weights=new HashMap<>();
    static {
        weights.put("MISSING_TITLE",10);
        weights.put("MISSING_LANG",5);
        weights.put("IMAGE_MISSING_ALT",5);
        weights.put("INPUT_MISSING_LABEL",8);

    }
    public static int getWeight(String issueType){
     return weights.getOrDefault(issueType,5);
    }
}
