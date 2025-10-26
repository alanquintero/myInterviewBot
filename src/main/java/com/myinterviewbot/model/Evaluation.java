/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.model;

/**
 * DTO class to store the evaluation for each interview entry.
 *
 * @author Alan Quintero
 */
public class Evaluation {

    // Clarity
    private int clarityScore;
    private String clarityFeedback;

    // Structure
    private int structureScore;
    private String structureFeedback;

    // Relevance
    private int relevanceScore;
    private String relevanceFeedback;

    // Communication
    private int communicationScore;
    private String communicationFeedback;

    // Depth
    private int depthScore;
    private String depthFeedback;

    public int getClarityScore() {
        return clarityScore;
    }

    public void setClarityScore(int clarityScore) {
        this.clarityScore = clarityScore;
    }

    public String getClarityFeedback() {
        return clarityFeedback;
    }

    public void setClarityFeedback(String clarityFeedback) {
        this.clarityFeedback = clarityFeedback;
    }

    public int getStructureScore() {
        return structureScore;
    }

    public void setStructureScore(int structureScore) {
        this.structureScore = structureScore;
    }

    public String getStructureFeedback() {
        return structureFeedback;
    }

    public void setStructureFeedback(String structureFeedback) {
        this.structureFeedback = structureFeedback;
    }

    public int getRelevanceScore() {
        return relevanceScore;
    }

    public void setRelevanceScore(int relevanceScore) {
        this.relevanceScore = relevanceScore;
    }

    public String getRelevanceFeedback() {
        return relevanceFeedback;
    }

    public void setRelevanceFeedback(String relevanceFeedback) {
        this.relevanceFeedback = relevanceFeedback;
    }

    public int getCommunicationScore() {
        return communicationScore;
    }

    public void setCommunicationScore(int communicationScore) {
        this.communicationScore = communicationScore;
    }

    public String getCommunicationFeedback() {
        return communicationFeedback;
    }

    public void setCommunicationFeedback(String communicationFeedback) {
        this.communicationFeedback = communicationFeedback;
    }

    public int getDepthScore() {
        return depthScore;
    }

    public void setDepthScore(int depthScore) {
        this.depthScore = depthScore;
    }

    public String getDepthFeedback() {
        return depthFeedback;
    }

    public void setDepthFeedback(String depthFeedback) {
        this.depthFeedback = depthFeedback;
    }
}
