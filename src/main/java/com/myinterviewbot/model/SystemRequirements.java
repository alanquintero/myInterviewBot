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

    private boolean areAllSystemRequirementsMet;

    private boolean cpuHasMinimumRequirements;

    private boolean ramHasMinimumRequirements;

    private boolean gpuHasMinimumRequirements;

    private boolean whisperServiceAvailable;

    private boolean aiProviderAvailable;

    private boolean aiModelAvailable;

    private String systemRequirementsMessage;

    public boolean isAreAllSystemRequirementsMet() {
        return areAllSystemRequirementsMet;
    }

    public void setAreAllSystemRequirementsMet(final boolean areAllSystemRequirementsMet) {
        this.areAllSystemRequirementsMet = areAllSystemRequirementsMet;
    }

    public boolean isCpuHasMinimumRequirements() {
        return cpuHasMinimumRequirements;
    }

    public void setCpuHasMinimumRequirements(final boolean cpuHasMinimumRequirements) {
        this.cpuHasMinimumRequirements = cpuHasMinimumRequirements;
    }

    public boolean isRamHasMinimumRequirements() {
        return ramHasMinimumRequirements;
    }

    public void setRamHasMinimumRequirements(final boolean ramHasMinimumRequirements) {
        this.ramHasMinimumRequirements = ramHasMinimumRequirements;
    }

    public boolean isGpuHasMinimumRequirements() {
        return gpuHasMinimumRequirements;
    }

    public void setGpuHasMinimumRequirements(final boolean gpuHasMinimumRequirements) {
        this.gpuHasMinimumRequirements = gpuHasMinimumRequirements;
    }

    public boolean isWhisperServiceAvailable() {
        return whisperServiceAvailable;
    }

    public void setWhisperServiceAvailable(final boolean whisperServiceAvailable) {
        this.whisperServiceAvailable = whisperServiceAvailable;
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

    public String getSystemRequirementsMessage() {
        return systemRequirementsMessage;
    }

    public void setSystemRequirementsMessage(final String systemRequirementsMessage) {
        this.systemRequirementsMessage = systemRequirementsMessage;
    }
}
