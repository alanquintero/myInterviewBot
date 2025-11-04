/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.service;

import com.myinterviewbot.model.PromptResponse;
import com.myinterviewbot.service.ai.model.AIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service responsible for checking the system requirements.
 *
 * @author Alan Quintero
 */
@Service
public class SystemRequirementsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SystemRequirementsService.class);

    @Autowired
    private AIService aiService;

    /**
     * Runs a prompt to collect the prompt stats used to determine if the system has an acceptable prompt response time.
     *
     * @return PromptResponse
     */
    public PromptResponse executeInitialPrompt() {
        LOGGER.info("Execute Initial Prompt");
        final String prompt = "You are an interview coach. Give a short STAR feedback for this example answer: At my previous job, I improved the load time of a service by 30% by introducing caching. The challenge was balancing consistency and performance. I implemented a TTL cache, monitored results, and reduced user complaints by 40%.";
        return aiService.executePrompt(prompt);
    }
}
