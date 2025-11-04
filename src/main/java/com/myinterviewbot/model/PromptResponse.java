/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.model;

/**
 * DTO class to store the prompt response.
 *
 * @author Alan Quintero
 */
public class PromptResponse {

    private PromptStats promptStats;

    private Object promptResponse;

    public PromptResponse(final PromptStats promptStats, final Object promptResponse) {
        this.promptStats = promptStats;
        this.promptResponse = promptResponse;
    }

    public PromptStats getPromptStats() {
        return promptStats;
    }

    public void setPromptStats(final PromptStats promptStats) {
        this.promptStats = promptStats;
    }

    public Object getPromptResponse() {
        return promptResponse;
    }

    public void setPromptResponse(final Object promptResponse) {
        this.promptResponse = promptResponse;
    }

    @Override
    public String toString() {
        return "PromptResponse{" +
                "promptStats=" + promptStats +
                '}';
    }
}
