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
    @Autowired
    private AccessibiltyReportRepo accessibiltyReportRepo;
    @Autowired
    private UploadedFileRepo uploadedFileRepo;

    public AccessibilityResponse handleFileUplZoad(MultipartFile file) {
        AccessibilityResponse response = new AccessibilityResponse();
        try{

            UploadedFile uploadedFile = new UploadedFile();
            uploadedFile.setFileName(file.getOriginalFilename());
            uploadedFile.setFileContent(file.getBytes());
            uploadedFile.setUploadedAt(new Timestamp(System.currentTimeMillis()));
            uploadedFileRepo.save(uploadedFile);

            String html=new String(file.getBytes(), StandardCharsets.UTF_8);
            Document document= Jsoup.parse(html);
            List<AccessibilityIssue> issues=new ArrayList<>();
            if(document.title()==null ||document.title().isEmpty()){
                issues.add(new AccessibilityIssue("Missing title","<title>","Missing <title> tag"));
            }
            Element htmlTag=document.selectFirst("html");
            if(htmlTag!=null &&!htmlTag.hasAttr("lang")){
                issues.add(new AccessibilityIssue("Missing lang","<html>","<html> tag is missing 'lang' attribute"));
            }
           for(Element img:document.select("img")){
               if(!img.hasAttr("alt") ||img.attr("alt").trim().isEmpty()){
                   issues.add(new AccessibilityIssue("Missing Alt","img","Image missing alt attribute"));
               }
           }
            for (Element input : document.select("input")) {
                String id = input.id();
                boolean hasLabel = false;
                if (!id.isEmpty()) {
                    hasLabel = !document.select("label[for=" + id + "]").isEmpty();
                }

                if (id.isEmpty() || !hasLabel) {
                    issues.add(new AccessibilityIssue("Missing label","input","Input field without associated label"));
                }
            }
            int score=Math.max(0,100-(issues.size()*10));
            boolean passed=issues.isEmpty();
            String message=passed?"No Accessibility Issues found":issues.size()+" "+"Accessibility issues found" ;

            AccessibilityReport report=new AccessibilityReport();
            report.setUploadedFile(uploadedFile);
            report.setIssues(issues);
            report.setPassed(issues.isEmpty());
            report.setGeneratedAt(new Timestamp(System.currentTimeMillis()));
            accessibiltyReportRepo.save(report);

            response.setScore(score);
            response.setPassed(passed);
            response.setIssues(issues);
            response.setMessage(message);
            return response;

        } catch (IOException e) {
            response.setScore(0);
            response.setPassed(false);
            response.setMessage("Failed to read the file: " + e.getMessage());
            return response;
        }
    }
}
