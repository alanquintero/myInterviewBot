/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.model;

import java.util.List;

/**
 * DTO class to return current Settings.
 *
 * @author Alan Quintero
 */
public class Settings {

    private String aiProvider;

    private String selectedAiModel;

    private List<String> aiModels;

    private String whisperProvider;

    private String operatingSystem;

    public Settings() {
    }

    public String getAiProvider() {
        return aiProvider;
    }

    public void setAiProvider(final String aiProvider) {
        this.aiProvider = aiProvider;
    }

    public String getSelectedAiModel() {
        return selectedAiModel;
    }

    public void setSelectedAiModel(final String selectedAiModel) {
        this.selectedAiModel = selectedAiModel;
    }

    public List<String> getAiModels() {
        return aiModels;
    }

    public void setAiModels(List<String> aiModels) {
        this.aiModels = aiModels;
    }

    public String getWhisperProvider() {
        return whisperProvider;
    }

    public void setWhisperProvider(final String whisperProvider) {
        this.whisperProvider = whisperProvider;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public void setOperatingSystem(final String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }
}
