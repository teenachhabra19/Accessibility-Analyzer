package com.example.accessibility_analyzer.repo;

import com.example.accessibility_analyzer.model.UploadedFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UploadedFileRepo extends JpaRepository<UploadedFile,Integer> {
}
