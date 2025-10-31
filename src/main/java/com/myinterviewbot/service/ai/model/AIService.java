/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.service.ai.model;

import com.myinterviewbot.model.PromptResponse;

/**
 * Interface for AI Services
 *
 * @author Alan Quintero
 */
public interface AIService {

    PromptResponse executePrompt(final String prompt);
}
