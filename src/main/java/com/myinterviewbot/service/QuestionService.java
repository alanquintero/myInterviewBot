/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myinterviewbot.model.Question;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Singleton service class that manages question data during the application's runtime.
 *
 * @author Alan Quintero
 */
public class QuestionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuestionService.class);

    private static QuestionService instance;
    private Set<String> questions;
    private final ObjectMapper objectMapper;
    private File storageFile;
    private String aiModelFromConfig;

    private QuestionService() {
        this.questions = new HashSet<>();
        this.objectMapper = new ObjectMapper();
    }

    public static synchronized QuestionService getInstance() {
        if (instance == null) {
            instance = new QuestionService();
        }
        return instance;
    }

    public void initData() throws IOException {
        LOGGER.info("initData");
        // Ensure base upload directory exists
        final File uploadsDir = new File("uploads/questions");
        if (!uploadsDir.exists() && !uploadsDir.mkdirs()) {
            throw new IOException("Failed to create uploads/questions directory");
        }

        storageFile = new File(uploadsDir, "questions.json");

        // Create empty JSON file if it doesn't exist
        if (!storageFile.exists()) {
            try {
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(storageFile, questions);
            } catch (IOException e) {
                throw new IOException("Failed to create questions.json");
            }
        }

        loadFromFile();
    }

    private void loadFromFile() {
        LOGGER.info("loadFromFile");
        if (storageFile.exists()) {
            try {
                questions = objectMapper.readValue(storageFile, new TypeReference<>() {
                });
                LOGGER.info("Loaded questions from file: {}", questions);
            } catch (IOException e) {
                LOGGER.error("Failed to load questions: {}", e.getMessage());
            }
        }
    }

    public Set<String> getQuestions() {
        return questions;
    }

    public boolean saveQuestion(final Question question) {
        try {
            questions.add(question.getQuestion());
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(storageFile, questions);
            return true;
        } catch (Exception e) {
            LOGGER.error("Failed to save questions.json", e);
        }
        return false;
    }
}
