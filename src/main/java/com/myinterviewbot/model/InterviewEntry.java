/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.model;

/**
 * DTO class to store each interview entry.
 *
 * @author Alan Quintero
 */
public class InterviewEntry {

    private long timestamp;
    private String profession;
    private String question;
    private String answer;
    private String feedback;
    private String videoUrl;

    public InterviewEntry() {
    }

    public InterviewEntry(final long timestamp, final String profession, final String question, final String answer, final String feedback, final String videoUrl) {
        this.timestamp = timestamp;
        this.profession = profession;
        this.question = question;
        this.answer = answer;
        this.feedback = feedback;
        this.videoUrl = videoUrl;
    }

    // Getters and Setters
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final long timestamp) {
        this.timestamp = timestamp;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(final String profession) {
        this.profession = profession;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(final String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(final String answer) {
        this.answer = answer;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(final String feedback) {
        this.feedback = feedback;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(final String videoUrl) {
        this.videoUrl = videoUrl;
    }
}
