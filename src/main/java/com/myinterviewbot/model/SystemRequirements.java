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
public class SystemRequirements extends PromptStats {

    private boolean aiProviderAvailable;

    private boolean aiModelAvailable;

    private boolean whisperServiceAvailable;

    public SystemRequirements(final boolean slowPromptResponse, final double secondsTakenToRespondPrompt) {
        super(slowPromptResponse, secondsTakenToRespondPrompt);
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

    @Override
    public String toString() {
        return "SystemRequirements{" +
                "slowPromptResponse=" + isSlowPromptResponse() +
                ", secondsTakenToRespondPrompt=" + getSecondsTakenToRespondPrompt() +
                '}';
    }
}
