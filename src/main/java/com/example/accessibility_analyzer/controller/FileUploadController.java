package com.example.accessibility_analyzer.controller;

import com.example.accessibility_analyzer.model.AccessibilityResponse;
import com.example.accessibility_analyzer.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class FileUploadController {
    @Autowired
    private FileUploadService fileUploadService;
    @PostMapping("/analyze-file")
   public ResponseEntity<AccessibilityResponse> uploadHtmlFile(@RequestParam("file")MultipartFile file){
       AccessibilityResponse response=fileUploadService.handleFileUplZoad(file);
       if(response!=null){
           return new ResponseEntity<>(response,HttpStatus.CREATED);
       }else{
           return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
       }

   }
}
