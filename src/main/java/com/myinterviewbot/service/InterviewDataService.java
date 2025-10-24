/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.service;

import com.myinterviewbot.model.InterviewEntry;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myinterviewbot.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Singleton service class that manages interview data during the application's runtime.
 * <p>
 * This class holds all interviews in memory and provides methods to access, add, update,
 * and delete interview records. It can also persist the data to a JSON file and load it
 * when the application starts.
 * </p>
 *
 * <p><b>Responsibilities:</b></p>
 * <ul>
 *     <li>Store interview data (id, timestamp, profession, question, answer, feedback, video file path)</li>
 *     <li>Provide fast access and lookup using a map keyed by interview ID</li>
 *     <li>Load data from a persistent file on startup</li>
 *     <li>Save data to a persistent file when updated</li>
 * </ul>
 *
 * <p><b>Note:</b> This class is implemented as a singleton. Use {@link #getInstance()} to
 * obtain the single instance.</p>
 */
public class InterviewDataService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InterviewDataService.class);

    private static InterviewDataService instance;
    private final Map<Long, InterviewEntry> interviews;
    private final ObjectMapper objectMapper;
    private File storageFile;

    private InterviewDataService() {
        this.interviews = new LinkedHashMap<>();
        this.objectMapper = new ObjectMapper();
    }

    public static synchronized InterviewDataService getInstance() {
        if (instance == null) {
            instance = new InterviewDataService();
        }
        return instance;
    }

    public void initData() {
        LOGGER.info("initData");
        // Writable location inside uploads/interviews
        final File uploadDir = new File(Utils.INTERVIEWS_DIR);
        if (!uploadDir.exists()) {
            if (uploadDir.mkdirs()) {
                LOGGER.warn("Unable to create uploads/interviews directory");
                return;
            }
        }

        storageFile = new File(uploadDir, "interviews.json");

        // Create empty JSON file if it doesn't exist
        if (!storageFile.exists()) {
            try {
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(storageFile, new LinkedHashMap<>());
            } catch (IOException e) {
                LOGGER.error("Failed to create interviews.json: {}", e.getMessage());
            }
        }

        loadFromFile();
    }

    public void addInterview(final Long id, final InterviewEntry entry) {
        interviews.put(id, entry);
        saveToFile();
    }

    public Collection<InterviewEntry> getAllInterviews() {
        return interviews.values();
    }

    public boolean removeInterview(final Long id) {
        final InterviewEntry entry = interviews.remove(id);
        saveToFile();
        return entry != null;
    }

    public void clearInterviews() {
        interviews.clear();
        saveToFile();
    }

    private void loadFromFile() {
        if (storageFile.exists()) {
            try {
                Map<Long, InterviewEntry> loaded = objectMapper.readValue(
                        storageFile,
                        new TypeReference<>() {
                        }
                );
                interviews.putAll(loaded);
                LOGGER.info("Loaded {} interviews from file.", loaded.size());
            } catch (IOException e) {
                LOGGER.error("Failed to load interviews: {}", e.getMessage());
            }
        }
    }

    private void saveToFile() {
        LOGGER.info("Saving {} interviews to file.", interviews.size());
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(storageFile, interviews);
        } catch (IOException e) {
            LOGGER.error("Failed to save interviews: {}", e.getMessage());
        }
    }
}


