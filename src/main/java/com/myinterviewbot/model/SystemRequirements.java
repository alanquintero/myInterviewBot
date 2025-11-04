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

    private boolean slowPromptResponse;

    private double secondsTakenToRespondPrompt;

    private boolean aiProviderAvailable;

    private boolean aiModelAvailable;

    private boolean whisperServiceAvailable;

    public SystemRequirements(final boolean slowPromptResponse, final double secondsTakenToRespondPrompt) {
        this.slowPromptResponse = slowPromptResponse;
        this.secondsTakenToRespondPrompt = secondsTakenToRespondPrompt;
    }

    public boolean isSlowPromptResponse() {
        return slowPromptResponse;
    }

    public void setSlowPromptResponse(final boolean slowPromptResponse) {
        this.slowPromptResponse = slowPromptResponse;
    }

    public double getSecondsTakenToRespondPrompt() {
        return secondsTakenToRespondPrompt;
    }

    public void setSecondsTakenToRespondPrompt(final double secondsTakenToRespondPrompt) {
        this.secondsTakenToRespondPrompt = secondsTakenToRespondPrompt;
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
                "slowPromptResponse=" + slowPromptResponse +
                ", secondsTakenToRespondPrompt=" + secondsTakenToRespondPrompt +
                '}';
    }
}
