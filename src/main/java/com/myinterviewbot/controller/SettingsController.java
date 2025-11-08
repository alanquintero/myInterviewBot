/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.controller;

import com.myinterviewbot.model.settings.AppSettings;
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
        return settingsService.getSettings();
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateSettings(@RequestBody Settings updatedSettings) {
        try {
            final Settings settings = settingsService.getSettings();

            // Update only known fields
            if (updatedSettings.getSelectedAiModel() != null) {
                settings.setSelectedAiModel(updatedSettings.getSelectedAiModel());
            }
            if (updatedSettings.getRecordingMode() != null && !updatedSettings.getRecordingMode().isEmpty()) {
                settings.setRecordingMode(updatedSettings.getRecordingMode());
            } else {
                settings.setRecordingMode("video");
            }
            if (updatedSettings.getDefaultProfession() != null && !updatedSettings.getDefaultProfession().isEmpty()) {
                settings.setDefaultProfession(updatedSettings.getDefaultProfession());
            } else {
                // Setting value to default profession
                settings.setDefaultProfession("Software Engineer");
            }
            settings.setShowQuestionCategory(updatedSettings.isShowQuestionCategory());
            settings.setShowQuestionDifficulty(updatedSettings.isShowQuestionDifficulty());

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
