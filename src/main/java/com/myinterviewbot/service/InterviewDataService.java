/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.service;

import com.myinterviewbot.model.Evaluation;
import com.myinterviewbot.model.InterviewEntry;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myinterviewbot.model.MyInterviews;
import com.myinterviewbot.model.ScoreSummary;
import com.myinterviewbot.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

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

    public void initData() throws IOException {
        LOGGER.info("initData");
        // Ensure base upload directory exists
        final File uploadsDir = new File(Utils.INTERVIEWS_DIR);
        if (!uploadsDir.exists() && !uploadsDir.mkdirs()) {
            throw new IOException("Failed to create uploads/interviews directory");
        }

        storageFile = new File(uploadsDir, "interviews.json");

        // Create empty JSON file if it doesn't exist
        if (!storageFile.exists()) {
            try {
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(storageFile, new LinkedHashMap<>());
            } catch (IOException e) {
                throw new IOException("Failed to create interviews.json");
            }
        }

        loadFromFile();
    }

    public void addInterview(final Long id, final InterviewEntry entry) {
        LOGGER.info("addInterview");
        interviews.put(id, entry);
        saveToFile();
    }

    public MyInterviews getMyInterviews() {
        LOGGER.info("getMyInterviews");
        final List<InterviewEntry> entries = interviews.values()
                .stream()
                .sorted(Comparator.comparingLong(InterviewEntry::getTimestamp).reversed())
                .toList();
        LOGGER.info("getMyInterviews size: {}", entries.size());
        return new MyInterviews(createScoreSummary(entries), entries);
    }

    public boolean removeInterview(final Long id) {
        LOGGER.info("removeInterview");
        final InterviewEntry entry = interviews.remove(id);
        saveToFile();
        return entry != null;
    }

    public void clearInterviews() {
        LOGGER.info("clearInterviews");
        interviews.clear();
        saveToFile();
    }

    private void loadFromFile() {
        LOGGER.info("loadFromFile");
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

    ScoreSummary createScoreSummary(final List<InterviewEntry> entries) {
        LOGGER.info("createScoreSummary");
        if (entries == null || entries.isEmpty()) {
            return new ScoreSummary();
        }
        // Clarity
        int clarityCount = 0;
        int clarityAccumulativeScore = 0;
        // Structure
        int structureCount = 0;
        int structureAccumulativeScore = 0;
        // Relevance
        int relevanceCount = 0;
        int relevanceAccumulativeScore = 0;
        // Communication
        int communicationCount = 0;
        int communicationAccumulativeScore = 0;
        // Depth
        int depthCount = 0;
        int depthAccumulativeScore = 0;

        for (final InterviewEntry entry : entries) {
            final Evaluation evaluation = entry.getEvaluation();
            if (evaluation == null) {
                continue;
            }
            if (evaluation.getClarityScore() > 0) {
                clarityCount++;
                clarityAccumulativeScore += evaluation.getClarityScore();
            }
            if (evaluation.getStructureScore() > 0) {
                structureCount++;
                structureAccumulativeScore += evaluation.getStructureScore();
            }
            if (evaluation.getRelevanceScore() > 0) {
                relevanceCount++;
                relevanceAccumulativeScore += evaluation.getRelevanceScore();
            }
            if (evaluation.getCommunicationScore() > 0) {
                communicationCount++;
                communicationAccumulativeScore += evaluation.getCommunicationScore();
            }
            if (evaluation.getDepthScore() > 0) {
                depthCount++;
                depthAccumulativeScore += evaluation.getDepthScore();
            }
        }
        final ScoreSummary scoreSummary = new ScoreSummary();
        if (clarityCount > 0) {
            scoreSummary.setClarityScoreAverage((double) clarityAccumulativeScore / clarityCount);
        }
        if (structureCount > 0) {
            scoreSummary.setStructureScoreAverage((double) structureAccumulativeScore / structureCount);
        }
        if (relevanceCount > 0) {
            scoreSummary.setRelevanceScoreAverage((double) relevanceAccumulativeScore / relevanceCount);
        }
        if (communicationCount > 0) {
            scoreSummary.setCommunicationScoreAverage((double) communicationAccumulativeScore / communicationCount);
        }
        if (depthCount > 0) {
            scoreSummary.setDepthScoreAverage((double) depthAccumulativeScore / depthCount);
        }
        return scoreSummary;
    }
}


