/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.controller;

import com.myinterviewbot.model.settings.Settings;
import com.myinterviewbot.service.InterviewDataService;
import com.myinterviewbot.service.SettingsService;
import com.myinterviewbot.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for handling settings requests.
 *
 * @author Alan Quintero
 */
@RestController
@RequestMapping("/settings/v1")
public class SettingsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsController.class);

    private final SettingsService settingsService;
    private final InterviewDataService interviewDataService;

    public SettingsController() {
        this.settingsService = SettingsService.getInstance();
        this.interviewDataService = InterviewDataService.getInstance();
    }

    /**
     * Get all settings
     */
    @GetMapping("/all")
    public Settings getAllSettings() {
        LOGGER.info("Get All Settings");
        final Settings settings = settingsService.getSettings();
        LOGGER.info("Settings: {}", settings);
        return settings;
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateSettings(@RequestBody Settings updatedSettings) {
        try {
            final Settings settings = settingsService.getSettings();

            // Update only known fields
            if (updatedSettings.getSystemSettings().getSelectedAiModel() != null) {
                settings.getSystemSettings().setSelectedAiModel(updatedSettings.getSystemSettings().getSelectedAiModel());
            }
            if (updatedSettings.getSystemSettings().getRecordingMode() != null && !updatedSettings.getSystemSettings().getRecordingMode().isEmpty()) {
                settings.getSystemSettings().setRecordingMode(updatedSettings.getSystemSettings().getRecordingMode());
            } else {
                settings.getSystemSettings().setRecordingMode("video");
            }
            if (updatedSettings.getAppSettings().getDefaultProfession() != null && !updatedSettings.getAppSettings().getDefaultProfession().isEmpty()) {
                settings.getAppSettings().setDefaultProfession(updatedSettings.getAppSettings().getDefaultProfession());
            } else {
                // Setting value to default profession
                settings.getAppSettings().setDefaultProfession("Software Engineer");
            }
            settings.getAppSettings().setShowQuestionCategory(updatedSettings.getAppSettings().isShowQuestionCategory());
            settings.getAppSettings().setShowQuestionDifficulty(updatedSettings.getAppSettings().isShowQuestionDifficulty());
            settings.getAppSettings().setShowSavedQuestions(updatedSettings.getAppSettings().isShowSavedQuestions());

            settingsService.saveSettings(settings);
            return ResponseEntity.ok("Settings updated successfully.");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update settings.");
        }
    }

    /**
     * Deletes all interviews.
     *
     * <p>This action removes all recorded videos, transcripts, and feedback data.</p>
     *
     * @return a success or failure message
     */
    @DeleteMapping("/interviews/clear")
    public String clearAllInterviews() {
        LOGGER.info("Received request to clear all interviews...");
        interviewDataService.clearInterviews();
        return Utils.clearAllInterviews();
    }
}
