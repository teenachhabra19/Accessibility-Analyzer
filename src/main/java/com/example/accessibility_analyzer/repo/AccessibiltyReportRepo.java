package com.example.accessibility_analyzer.repo;

import com.example.accessibility_analyzer.model.AccessibilityReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface AccessibiltyReportRepo extends JpaRepository<AccessibilityReport,Integer> {
    AccessibilityReport findByUploadedFileId(Integer id);
}
