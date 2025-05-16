package com.example.accessibility_analyzer.service;

import com.example.accessibility_analyzer.model.*;
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
import java.net.URL;
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
        try {
            String filename = file.getOriginalFilename();
            if (filename == null || !(filename.endsWith(".html") || filename.endsWith(".htm"))) {
                return createFailureResponse("Only .html or .htm files are supported.");
            }

            String html = new String(file.getBytes(), StandardCharsets.UTF_8);
            AccessibilityResponse response = analyzeHtml(html);

            if (response.isPassed() || !response.getIssues().isEmpty()) {
                UploadedFile uploadedFile = new UploadedFile();
                uploadedFile.setFileName(filename);
                uploadedFile.setFileContent(file.getBytes());
                uploadedFile.setUploadedAt(new Timestamp(System.currentTimeMillis()));
                uploadedFileRepo.save(uploadedFile);

                AccessibilityReport report = new AccessibilityReport();
                report.setUploadedFile(uploadedFile);
                report.setIssues(response.getIssues());
                report.setPassed(response.isPassed());
                report.setScore(response.getScore());
                report.setGeneratedAt(new Timestamp(System.currentTimeMillis()));
                report.setMessage(response.getMessage());
                accessibiltyReportRepo.save(report);
            }

            return response;

        } catch (IOException e) {
            logger.error("Failed to process uploaded file", e);
            return createFailureResponse("Failed to read the file: " + e.getMessage());
        }
    }


    public AccessibilityResponse analyzeFromUrl(String url) {
        try {
            Document document = Jsoup.connect(url).get();
            String html = document.html();
            return analyzeHtml(html);
        } catch (IOException e) {
            logger.error("Failed to fetch URL: " + url, e);
            return createFailureResponse("Failed to fetch the URL: " + e.getMessage());
        }
    }

    public AccessibilityResponse analyzeHtml(String html) {
        AccessibilityResponse response = new AccessibilityResponse();
        Document document = Jsoup.parse(html);

        if (document == null) {
            return createFailureResponse("Failed to parse HTML content.");
        }

        List<AccessibilityIssue> issues = analyzeDocument(document);
        int score = AccessibilityScorer.calculateScore(issues);
        boolean passed = issues.isEmpty();
        String message = passed ? "No accessibility issues found." : issues.size() + " accessibility issues found.";

        response.setScore(score);
        response.setPassed(passed);
        response.setIssues(issues);
        response.setMessage(message);

        return response;
    }
    private List<AccessibilityIssue> analyzeDocument(Document document) {
        List<AccessibilityIssue> issues = new ArrayList<>();

        if (document.title() == null || document.title().isEmpty()) {
            issues.add(new AccessibilityIssue("MISSING_TITLE", "<title>", "Missing <title> tag"));
        }

        Element htmlTag = document.selectFirst("html");
        if (htmlTag != null && !htmlTag.hasAttr("lang")) {
            issues.add(new AccessibilityIssue("MISSING_LANG", "<html>", "<html> tag is missing 'lang' attribute"));
        }

        for (Element img : document.select("img")) {
            if (!img.hasAttr("alt") || img.attr("alt").trim().isEmpty()) {
                issues.add(new AccessibilityIssue("IMAGE_MISSING_ALT", "<img>", "Image missing alt attribute"));
            }
        }

        for (Element input : document.select("input")) {
            String id = input.id();
            boolean hasLabel = !id.isEmpty() && !document.select("label[for=" + id + "]").isEmpty();
            if (id.isEmpty() || !hasLabel) {
                issues.add(new AccessibilityIssue("INPUT_MISSING_LABEL", "<input>", "Input field without associated label"));
            }
        }

        return issues;
    }
    private AccessibilityResponse createFailureResponse(String message) {
        AccessibilityResponse response = new AccessibilityResponse();
        response.setScore(0);
        response.setPassed(false);
        response.setMessage(message);
        return response;
    }
}
