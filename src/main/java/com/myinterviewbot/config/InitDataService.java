/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.config;

import com.myinterviewbot.service.InterviewDataService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final InterviewDataService interviewDataService;

    public InitDataService() {
        interviewDataService = InterviewDataService.getInstance();
    }

    @PostConstruct
    public void init() throws IOException {
        LOGGER.info("Initializing data...");
        interviewDataService.initData();
    }
}
