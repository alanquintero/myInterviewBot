/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.model;

/**
 * DTO class to calculate the score summary.
 *
 * @author Alan Quintero
 */
public class ScoreSummary {

    private double clarityScoreAverage;

    private double structureScoreAverage;

    private double relevanceScoreAverage;

    private double communicationScoreAverage;

    private double depthScoreAverage;

    public double getClarityScoreAverage() {
        return clarityScoreAverage;
    }

    public void setClarityScoreAverage(final double clarityScoreAverage) {
        this.clarityScoreAverage = clarityScoreAverage;
    }

    public double getStructureScoreAverage() {
        return structureScoreAverage;
    }

    public void setStructureScoreAverage(final double structureScoreAverage) {
        this.structureScoreAverage = structureScoreAverage;
    }

    public double getRelevanceScoreAverage() {
        return relevanceScoreAverage;
    }

    public void setRelevanceScoreAverage(final double relevanceScoreAverage) {
        this.relevanceScoreAverage = relevanceScoreAverage;
    }

    public double getCommunicationScoreAverage() {
        return communicationScoreAverage;
    }

    public void setCommunicationScoreAverage(final double communicationScoreAverage) {
        this.communicationScoreAverage = communicationScoreAverage;
    }

    public double getDepthScoreAverage() {
        return depthScoreAverage;
    }

    public void setDepthScoreAverage(final double depthScoreAverage) {
        this.depthScoreAverage = depthScoreAverage;
    }
}
