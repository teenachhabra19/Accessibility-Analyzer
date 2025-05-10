package com.example.accessibility_analyzer.repo;

import com.example.accessibility_analyzer.model.AccessibilityReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccessibiltyReportRepo extends JpaRepository<AccessibilityReport,Integer> {
    AccessibilityReport findByUploadedFileId(Integer id);
}
