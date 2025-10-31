/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.service.prompt;

import com.myinterviewbot.config.GlobalConfig;
import com.myinterviewbot.model.Evaluation;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class PromptService implements Prompt {

    private final LowPerformancePromptService lowPerformancePromptService;
    private final HighPerformancePromptService highPerformancePromptService;

    public PromptService(final LowPerformancePromptService lowPerformancePromptService, final HighPerformancePromptService highPerformancePromptService) {
        this.lowPerformancePromptService = lowPerformancePromptService;
        this.highPerformancePromptService = highPerformancePromptService;
    }

    public String generateQuestion(final String profession, final HttpSession session) {
        if (GlobalConfig.slowPerformanceMode) {
            return lowPerformancePromptService.generateQuestion(profession, session);
        } else {
            return highPerformancePromptService.generateQuestion(profession, session);
        }
    }

    @Override
    public String generateFeedback(String transcript, String profession, String question) {
        if (GlobalConfig.slowPerformanceMode) {
            return lowPerformancePromptService.generateFeedback(transcript, profession, question);
        } else {
            return highPerformancePromptService.generateFeedback(transcript, profession, question);
        }
    }

    @Override
    public Evaluation generateEvaluation(String transcript, String profession, String question) {
        if (GlobalConfig.slowPerformanceMode) {
            return lowPerformancePromptService.generateEvaluation(transcript, profession, question);
        } else {
            return highPerformancePromptService.generateEvaluation(transcript, profession, question);
        }
    }


}
