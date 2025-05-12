package com.example.accessibility_analyzer.service;

import com.example.accessibility_analyzer.model.AccessibilityIssue;
import com.example.accessibility_analyzer.model.AccessibilityReport;
import com.example.accessibility_analyzer.model.AccessibilityResponse;
import com.example.accessibility_analyzer.model.UploadedFile;
import com.example.accessibility_analyzer.repo.AccessibiltyReportRepo;
import com.example.accessibility_analyzer.repo.UploadedFileRepo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileUploadService {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadService.class);

    @Autowired
    private AccessibiltyReportRepo accessibiltyReportRepo;

    @Autowired
    private UploadedFileRepo uploadedFileRepo;

    public AccessibilityResponse handleFileUpload(MultipartFile file) {
        AccessibilityResponse response = new AccessibilityResponse();

        try {
            String filename = file.getOriginalFilename();
            if (filename == null || !(filename.endsWith(".html") || filename.endsWith(".htm"))) {
                response.setScore(0);
                response.setPassed(false);
                response.setMessage("Only .html or .htm files are supported.");
                return response;
            }

            String html = new String(file.getBytes(), StandardCharsets.UTF_8);
            Document document = Jsoup.parse(html);

            if (document == null) {
                response.setScore(0);
                response.setPassed(false);
                response.setMessage("Failed to parse HTML file.");
                return response;
            }

            List<AccessibilityIssue> issues = analyzeDocument(document);
            int score = Math.max(0, 100 - (issues.size() * 10));
            boolean passed = issues.isEmpty();
            String message = passed ? "No Accessibility Issues found" : issues.size() + " Accessibility issues found";

            UploadedFile uploadedFile = new UploadedFile();
            uploadedFile.setFileName(filename);
            uploadedFile.setFileContent(file.getBytes());
            uploadedFile.setUploadedAt(new Timestamp(System.currentTimeMillis()));
            uploadedFileRepo.save(uploadedFile);

            AccessibilityReport report = new AccessibilityReport();
            report.setUploadedFile(uploadedFile);
            report.setIssues(issues);
            report.setPassed(passed);
            report.setGeneratedAt(new Timestamp(System.currentTimeMillis()));
            report.setScore(score);
            report.setMessage(message);
            accessibiltyReportRepo.save(report);

            // Build response
            response.setScore(score);
            response.setPassed(passed);
            response.setIssues(issues);
            response.setMessage(message);

        } catch (IOException e) {
            logger.error("Failed to process uploaded file", e);
            response.setScore(0);
            response.setPassed(false);
            response.setMessage("Failed to read the file: " + e.getMessage());
        }

        return response;
    }

    private List<AccessibilityIssue> analyzeDocument(Document document) {
        List<AccessibilityIssue> issues = new ArrayList<>();

        if (document.title() == null || document.title().isEmpty()) {
            issues.add(new AccessibilityIssue("Missing title", "<title>", "Missing <title> tag"));
        }

        Element htmlTag = document.selectFirst("html");
        if (htmlTag != null && !htmlTag.hasAttr("lang")) {
            issues.add(new AccessibilityIssue("Missing lang", "<html>", "<html> tag is missing 'lang' attribute"));
        }

        for (Element img : document.select("img")) {
            if (!img.hasAttr("alt") || img.attr("alt").trim().isEmpty()) {
                issues.add(new AccessibilityIssue("Missing Alt", "img", "Image missing alt attribute"));
            }
        }

        for (Element input : document.select("input")) {
            String id = input.id();
            boolean hasLabel = false;
            if (!id.isEmpty()) {
                hasLabel = !document.select("label[for=" + id + "]").isEmpty();
            }
            if (id.isEmpty() || !hasLabel) {
                issues.add(new AccessibilityIssue("Missing label", "input", "Input field without associated label"));
            }
        }

        return issues;
    }
}
