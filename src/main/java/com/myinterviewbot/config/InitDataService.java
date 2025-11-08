/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.config;

import com.myinterviewbot.service.InterviewDataService;
import com.myinterviewbot.service.SettingsService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Service responsible for initializing data when app starts.
 *
 * @author Alan Quintero
 */
@Service
public class InitDataService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InitDataService.class);

    private final SettingsService settingsService;
    private final InterviewDataService interviewDataService;

    @Value("${ai.provider}")
    private String aiProvider;

    @Value("${ai.model}")
    private String aiModel;

    @Value("${whisper.provider}")
    private String whisperProvider;

    public InitDataService() {
        settingsService = SettingsService.getInstance();
        interviewDataService = InterviewDataService.getInstance();
    }

    @PostConstruct
    public void init() throws IOException {
        LOGGER.info("Initializing data...");
        settingsService.initData(aiProvider, aiModel, whisperProvider);
        interviewDataService.initData();
    }
}
