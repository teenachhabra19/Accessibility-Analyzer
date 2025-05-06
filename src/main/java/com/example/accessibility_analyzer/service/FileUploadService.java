package com.example.accessibility_analyzer.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileUploadService {
    public String handleFileUplZoad(MultipartFile file) {
        try{
            String html=new String(file.getBytes(), StandardCharsets.UTF_8);
            Document document= Jsoup.parse(html);
            List<String> issues=new ArrayList<>();
            if(document.title()==null ||document.title().isEmpty()){
                issues.add("Missing <title> tag");
            }
            Element htmlTag=document.selectFirst("html");
            if(htmlTag!=null &&!htmlTag.hasAttr("lang")){
                issues.add("<html> tag is missing 'lang' attribute");
            }
           for(Element img:document.select("img")){
               if(!img.hasAttr("alt") ||img.attr("alt").trim().isEmpty()){
                   issues.add("Image missing alt attribute at:"+img.outerHtml());
               }
           }
            for (Element input : document.select("input")) {
                String id = input.id();
                boolean hasLabel = false;
                if (!id.isEmpty()) {
                    hasLabel = !document.select("label[for=" + id + "]").isEmpty();
                }

                if (id.isEmpty() || !hasLabel) {
                    issues.add("Input field without associated label: " + input.outerHtml());
                }
            }
           if(issues.isEmpty()){
               return "No Accessibility issues found";
           }else{
               return "Accessibility Issues found:\n"+ String.join("\n",issues);
           }
        } catch (IOException e) {
            return "Failed to read the file: " + e.getMessage();
        }
    }
}
