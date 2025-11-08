/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.model;

/**
 * DTO class to receive input parameters related to Prompt requests from the front-end.
 *
 * @author Alan Quintero
 */
public class PromptRequest {

    private String profession;
    private String category;
    private String difficulty;
    private String question;
    private Transcript transcript;
    private String feedback;

    public String getProfession() {
        return profession;
    }

    public void setProfession(final String profession) {
        this.profession = profession;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(final String category) {
        this.category = category;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(final String difficulty) {
        this.difficulty = difficulty;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(final String question) {
        this.question = question;
    }

    public Transcript getTranscript() {
        return transcript;
    }

    public void setTranscript(final Transcript transcript) {
        this.transcript = transcript;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(final String feedback) {
        this.feedback = feedback;
    }

    @Override
    public String toString() {
        return "InterviewInputParams{" +
                "profession='" + profession + '\'' +
                ", category='" + category + '\'' +
                ", difficulty='" + difficulty + '\'' +
                ", question='" + question + '\'' +
                ", transcript=" + transcript +
                ", feedback='" + feedback + '\'' +
                '}';
    }
}
