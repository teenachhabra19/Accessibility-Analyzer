package com.example.accessibility_analyzer.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccessibilityReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="uploaded_file_id")
    private UploadedFile uploadedFile;
    @ElementCollection
    @CollectionTable(name="accessibility_issue",joinColumns = @JoinColumn(name="report_id"))
    private List<AccessibilityIssue> issues;
    private boolean passed;
    private Timestamp generatedAt;

}
