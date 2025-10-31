/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.config;

import com.myinterviewbot.service.InterviewDataService;
import com.myinterviewbot.service.ai.model.AIService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private AIService aiService;

    private final InterviewDataService interviewDataService;

    public InitDataService() {
        interviewDataService = InterviewDataService.getInstance();
    }

    @PostConstruct
    public void init() throws IOException {
        LOGGER.info("Initializing data...");
        interviewDataService.initData();

        // It will run a prompt to determine if the app should be set to low performance mode
        aiService.executePrompt("Say only the word \"ready\".");
    }
}
