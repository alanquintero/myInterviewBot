/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.model;

public class PromptResponse {

    private boolean insufficientSystemRequirements;

    private Object promptResponse;

    public PromptResponse(final boolean insufficientSystemRequirements, final Object promptResponse) {
        this.insufficientSystemRequirements = insufficientSystemRequirements;
        this.promptResponse = promptResponse;
    }

    public boolean isInsufficientSystemRequirements() {
        return insufficientSystemRequirements;
    }

    public void setInsufficientSystemRequirements(final boolean insufficientSystemRequirements) {
        this.insufficientSystemRequirements = insufficientSystemRequirements;
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
                "insufficientSystemRequirements=" + insufficientSystemRequirements +
                ", promptResponse=" + promptResponse +
                '}';
    }
}
