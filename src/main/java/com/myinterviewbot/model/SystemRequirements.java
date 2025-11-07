/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.model;

/**
 * DTO class to store the system requirements.
 *
 * @author Alan Quintero
 */
public class SystemRequirements {

    private boolean aiProviderAvailable;

    private boolean aiModelAvailable;

    private boolean whisperServiceAvailable;

    private boolean slowPromptResponse;

    private boolean promptExecutedSuccessfully;

    public SystemRequirements(final boolean slowPromptResponse, final boolean promptExecutedSuccessfully) {
        this.slowPromptResponse = slowPromptResponse;
        this.promptExecutedSuccessfully = promptExecutedSuccessfully;
    }

    public boolean isAiProviderAvailable() {
        return aiProviderAvailable;
    }

    public void setAiProviderAvailable(final boolean aiProviderAvailable) {
        this.aiProviderAvailable = aiProviderAvailable;
    }

    public boolean isAiModelAvailable() {
        return aiModelAvailable;
    }

    public void setAiModelAvailable(final boolean aiModelAvailable) {
        this.aiModelAvailable = aiModelAvailable;
    }

    public boolean isWhisperServiceAvailable() {
        return whisperServiceAvailable;
    }

    public void setWhisperServiceAvailable(final boolean whisperServiceAvailable) {
        this.whisperServiceAvailable = whisperServiceAvailable;
    }

    public boolean isSlowPromptResponse() {
        return slowPromptResponse;
    }

    public void setSlowPromptResponse(final boolean slowPromptResponse) {
        this.slowPromptResponse = slowPromptResponse;
    }

    public boolean isPromptExecutedSuccessfully() {
        return promptExecutedSuccessfully;
    }

    public void setPromptExecutedSuccessfully(final boolean promptExecutedSuccessfully) {
        this.promptExecutedSuccessfully = promptExecutedSuccessfully;
    }

    @Override
    public String toString() {
        return "SystemRequirements{" +
                "slowPromptResponse=" + slowPromptResponse +
                ", promptExecutedSuccessfully=" + promptExecutedSuccessfully +
                '}';
    }
}