/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.controller;

import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for handling resume requests.
 *
 * @author Alan Quintero
 */
@RestController("/resume/v1")
public class ResumeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResumeController.class);

    @PostMapping("/upload-resume")
    public Map<String, Object> uploadResume(@RequestParam("resume") final MultipartFile file) {
        LOGGER.info("upload resume");
        final Map<String, Object> response = new HashMap<>();
        if (file.isEmpty()) {
            LOGGER.error("file is empty");
            response.put("success", false);
            response.put("message", "No file selected.");
            return response;
        }

        try {
            // Ensure folder exists
            final Path uploadPath = Paths.get("uploads/resume");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            final String originalFilename = file.getOriginalFilename();
            String extension = "";

            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
            }

            String textContent;

            // Extract text based on file type
            if ("pdf".equals(extension)) {
                try (PDDocument document = PDDocument.load(file.getInputStream())) {
                    PDFTextStripper stripper = new PDFTextStripper();
                    textContent = stripper.getText(document);
                }
            } else if ("docx".equals(extension)) {
                try (XWPFDocument doc = new XWPFDocument(file.getInputStream())) {
                    XWPFWordExtractor extractor = new XWPFWordExtractor(doc);
                    textContent = extractor.getText();
                }
            } else {
                response.put("success", false);
                response.put("message", "Unsupported file type. Please upload PDF or DOCX.");
                return response;
            }

            // Save extracted text as a .txt file
            final String txtFilename = originalFilename.replaceAll("\\.[^.]+$", ".txt");
            final Path txtFilePath = uploadPath.resolve(txtFilename);
            Files.writeString(txtFilePath, textContent, StandardOpenOption.CREATE);

            response.put("success", true);
            response.put("message", "Resume uploaded and text extracted successfully!");
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            response.put("success", false);
            response.put("message", "Failed to upload and extract resume.");
        }

        return response;
    }
}

