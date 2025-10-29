/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.model;

/**
 * DTO class to return current Settings.
 *
 * @author Alan Quintero
 */
public class Settings {

    private String aiProvider;

    private String aiModel;

    private String whisperProvider;

    public Settings(final String aiProvider, final String aiModel, final String whisperProvider) {
        this.aiProvider = aiProvider;
        this.aiModel = aiModel;
        this.whisperProvider = whisperProvider;
    }

    public String getAiProvider() {
        return aiProvider;
    }

    public void setAiProvider(final String aiProvider) {
        this.aiProvider = aiProvider;
    }

    public String getAiModel() {
        return aiModel;
    }

    public void setAiModel(final String aiModel) {
        this.aiModel = aiModel;
    }

    public String getWhisperProvider() {
        return whisperProvider;
    }

    public void setWhisperProvider(final String whisperProvider) {
        this.whisperProvider = whisperProvider;
    }
}
