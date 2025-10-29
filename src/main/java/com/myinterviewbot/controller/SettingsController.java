/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.controller;

import com.myinterviewbot.model.Settings;
import com.myinterviewbot.service.InterviewDataService;
import com.myinterviewbot.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for handling settings requests.
 *
 * @author Alan Quintero
 */
@RestController
@RequestMapping("/settings/v1")
public class SettingsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsController.class);

    private final InterviewDataService interviewDataService;

    public SettingsController() {
        this.interviewDataService = InterviewDataService.getInstance();
    }

    @Value("${ai.provider}")
    private String aiProvider;

    @Value("${ai.model}")
    private String aiModel;

    @Value("${whisper.provider}")
    private String whisperProvider;

    /**
     * Get all settings
     */
    @GetMapping("/all")
    public Settings getAllSettings() {
        LOGGER.info("Get All Settings");
        return new Settings(aiProvider, aiModel, whisperProvider);
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
