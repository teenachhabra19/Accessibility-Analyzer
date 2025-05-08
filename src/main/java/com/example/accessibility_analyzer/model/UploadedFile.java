package com.example.accessibility_analyzer.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadedFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fileName;
    @Lob
    private byte[] fileContent;
    private Timestamp uploadedAt;
    @OneToOne(mappedBy = "uploadedFile", cascade = CascadeType.ALL)
    private AccessibilityReport report;

}
