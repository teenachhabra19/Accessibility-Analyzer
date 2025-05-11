package com.example.accessibility_analyzer.controller;

import com.example.accessibility_analyzer.model.AccessibilityReport;
import com.example.accessibility_analyzer.model.UploadedFile;
import com.example.accessibility_analyzer.service.FileRetrievalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FileRetrievalController {
    @Autowired
    private FileRetrievalService fileRetrievalService;
    @GetMapping("files")
    public ResponseEntity<List<UploadedFile>> getAllFiles(){
        return fileRetrievalService.getAllFiles();
    }
//    @GetMapping("files/{id}/report")
//    public ResponseEntity<String> getReportByFileId(@PathVariable Integer id) {
//        AccessibilityReport report = fileRetrievalService.getReportByFileId(id);
//
//        if (report == null) {
//            return ResponseEntity.notFound().build();
//        }
//
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"report_" + id + ".txt\"")
//                .contentType(MediaType.TEXT_PLAIN)
//                .body(report.getIssues());
//    }


}
