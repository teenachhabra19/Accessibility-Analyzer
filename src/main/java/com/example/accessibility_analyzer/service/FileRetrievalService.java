package com.example.accessibility_analyzer.service;

import com.example.accessibility_analyzer.model.AccessibilityReport;
import com.example.accessibility_analyzer.model.AccessibilityResponse;
import com.example.accessibility_analyzer.model.UploadedFile;
import com.example.accessibility_analyzer.repo.AccessibiltyReportRepo;
import com.example.accessibility_analyzer.repo.UploadedFileRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class FileRetrievalService {
    @Autowired
    private UploadedFileRepo uploadedFileRepo;
    @Autowired
    private AccessibiltyReportRepo accessibiltyReportRepo;

    public ResponseEntity<List<UploadedFile>> getAllFiles() {
        return new ResponseEntity<>(uploadedFileRepo.findAll(), HttpStatus.OK);
    }

    public ResponseEntity<AccessibilityResponse> getReportByFileId(Integer id) {
        AccessibilityReport accessibilityReport=accessibiltyReportRepo.findByUploadedFileId(id);
        if (accessibilityReport == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
      AccessibilityResponse response= new AccessibilityResponse(
               accessibilityReport.getScore(),
              accessibilityReport.isPassed(),
              accessibilityReport.getIssues(),
              accessibilityReport.getMessage()
       );

          return new ResponseEntity<>(response,HttpStatus.OK);

    }
}
