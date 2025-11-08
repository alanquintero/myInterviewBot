/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.model;

import java.util.List;

/**
 * DTO class to return current All Settings.
 *
 * @author Alan Quintero
 */
public class Settings {

    private AppSettings appSettings;

    private SystemSettings systemSettings;

    public Settings() {
        appSettings = new AppSettings();
        systemSettings = new SystemSettings();
    }

    public AppSettings getAppSettings() {
        return appSettings;
    }

    public void setAppSettings(final AppSettings appSettings) {
        this.appSettings = appSettings;
    }

    public SystemSettings getSystemSettings() {
        return systemSettings;
    }

    public void setSystemSettings(final SystemSettings systemSettings) {
        this.systemSettings = systemSettings;
    }

    public String getDefaultProfession() {
        return this.appSettings.getDefaultProfession();
    }

    public void setDefaultProfession(final String defaultProfession) {
        this.appSettings.setDefaultProfession(defaultProfession);
    }

    public boolean isShowQuestionCategory() {
        return this.appSettings.isShowQuestionCategory();
    }

    public void setShowQuestionCategory(final boolean showQuestionCategory) {
        this.appSettings.setShowQuestionCategory(showQuestionCategory);
    }

    public boolean isShowQuestionDifficulty() {
        return appSettings.isShowQuestionDifficulty();
    }

    public void setShowQuestionDifficulty(final boolean showQuestionDifficulty) {
        this.appSettings.setShowQuestionDifficulty(showQuestionDifficulty);
    }

    public String getAiProvider() {
        return systemSettings.getAiProvider();
    }

    public void setAiProvider(final String aiProvider) {
        this.systemSettings.setAiProvider(aiProvider);
    }

    public String getSelectedAiModel() {
        return systemSettings.getSelectedAiModel();
    }

    public void setSelectedAiModel(final String selectedAiModel) {
        this.systemSettings.setSelectedAiModel(selectedAiModel);
    }

    public List<String> getAiModels() {
        return systemSettings.getAiModels();
    }

    public void setAiModels(List<String> aiModels) {
        this.systemSettings.setAiModels(aiModels);
    }

    public String getWhisperProvider() {
        return systemSettings.getWhisperProvider();
    }

    public void setWhisperProvider(final String whisperProvider) {
        this.systemSettings.setWhisperProvider(whisperProvider);
    }

    public String getOperatingSystem() {
        return systemSettings.getOperatingSystem();
    }

    public void setOperatingSystem(final String operatingSystem) {
        this.systemSettings.setOperatingSystem(operatingSystem);
    }
}
