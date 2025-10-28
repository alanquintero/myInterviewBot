/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.model;

import java.util.List;

/**
 * DTO class to return the data for My Interviews page.
 *
 * @author Alan Quintero
 */
public class MyInterviews {

    private ScoreSummary scoreSummary;

    private List<InterviewEntry> entries;

    public MyInterviews(final ScoreSummary scoreSummary, final List<InterviewEntry> entries) {
        this.scoreSummary = scoreSummary;
        this.entries = entries;
    }

    public ScoreSummary getScoreSummary() {
        return scoreSummary;
    }

    public void setScoreSummary(final ScoreSummary scoreSummary) {
        this.scoreSummary = scoreSummary;
    }

    public List<InterviewEntry> getEntries() {
        return entries;
    }

    public void setEntries(final List<InterviewEntry> entries) {
        this.entries = entries;
    }
}
