/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.service.prompt;

import com.myinterviewbot.model.Evaluation;
import jakarta.servlet.http.HttpSession;

public interface Prompt {

    String generateQuestion(final String profession, final HttpSession session);

    String generateFeedback(final String transcript, final String profession, final String question);

    Evaluation generateEvaluation(final String transcript, final String profession, final String question);

}
